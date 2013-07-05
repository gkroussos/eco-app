package uk.ac.bbk.dcs.ecoapp.db;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.restfb.json.JsonArray;
import com.restfb.json.JsonObject;
import com.restfb.json.JsonTokener;

import uk.ac.bbk.dcs.ecoapp.R;
import uk.ac.bbk.dcs.ecoapp.db.xml.DataVersionContentHandler;
import uk.ac.bbk.dcs.ecoapp.db.xml.SiteListContentHandler;
import uk.ac.bbk.dcs.ecoapp.model.Site;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;


// FIXME implement facebook node ID's in the database
// To 'like' a company we need to have the FB Open Graph node ID. 
// This data should properly be provided in the database feed and should be parsed and stored
// like all the other company data in a local DB
// As a hack to get the functionality working we have stored the FB data for those 
// companies in v0.4 of the data stream in a properties file as part of the app.
// The DatabaseUpdater class loads this file and applies the FB field to each business
// in the database whenever it is run.
// Once the FB IDs are in the data stream, code to support this hack should be removed,
// All such code is tagged with FIXME and references this note. 


/**
 * This class is responsible for updating versions of the database held
 * locally on the device with that held remotely.
 * An instance is constructed with a set of Properties which reflect the location
 * of a remote database.
 * Calling the checkAndUpdate( ) method will cause the class to compare local and
 * remote database versions and, where the remote version is at a more recent level, 
 * will synchronously download the new data and update the local store.
 *  
 * Calling checkAndUpdate with (force = true) will cause the data to be reloaded 
 * regardless of whether the local database reports that it is up to date
 * 
 * @author Dave Durbin
 */
public class DatabaseUpdater {
	/** TAG for logging */
	private final String					TAG = getClass( ).getCanonicalName();
	
	/** Context used for accessing services */
	private Context							context_;

	/** URL for version number */
	private URL								versionURL_;

	/** URL for data */
	private URL 							dataURL_;

	/** List of Listeners */
	private List<DatabaseUpdaterListener> 	listeners_;
	
	/** FIXME Properties object containing mapping from company name to FBID : see note at top of file*/
	private Map<String,String>				fbNodeIdMap_;
	
	/** Keys for retrieving data form FBID JSON structure */
	private static final String 			JSON_KEY_SITE_NAME = "site_name";
	private static final String 			JSON_KEY_FB_NAME= "facebook_name";
	private static final String 			JSON_KEY_FB_ID= "facebook_id";
	// End of FIXME

	/* Convenient constants */
	private static final String 			PROP_VERSION_URL = "url.version";
	private static final String 			PROP_DATA_URL = "url.data";

	/**
	 * Construct an updater.
	 * Properties should include:
	 * <li>url.version	- the URL at which the remote version can be checked</li>
	 * <li>url.data		- the URL from which data can be downloaded</li>
	 * @param properties
	 */
	public DatabaseUpdater( Context context, Properties properties ) throws Exception {
		// Validate Context
		if( context != null ) {
			context_ = context;
		} else {
			throw new Exception( "Context cannot be null." ); 
		}

		// Validate properties
		if( properties != null ) {
			// Parse version URL
			versionURL_ = readURLFromProperties( properties, PROP_VERSION_URL);
			dataURL_    = readURLFromProperties( properties, PROP_DATA_URL);

			if( versionURL_ == null || dataURL_ == null ) {
				throw new Exception( "Problem configuring database updater. Updates unavailable." ); 
			}
			
			listeners_ = new ArrayList<DatabaseUpdaterListener>( );
		} else {
			throw new Exception( "Properties cannot be null." ); 
		}
		
		// FIXME Load FBIDs from file. See comment at top of code
		// File is JSON format array
		fbNodeIdMap_ = new HashMap<String,String>( );
		InputStream is = context_.getResources().openRawResource(R.raw.facebook);
		Reader reader = new InputStreamReader(is);
		JsonTokener tokener = new JsonTokener(reader);
		JsonArray array = new JsonArray(tokener);
		for( int i=0; i<array.length(); i++ ) {
			JsonObject jo = array.getJsonObject(i);
			String siteName = (String) jo.get(JSON_KEY_SITE_NAME);
			String facebookID= (String) jo.get(JSON_KEY_FB_ID);
			fbNodeIdMap_.put(siteName, facebookID);
		}
		// END of FIXME
	}


	/**
	 * Check whether the network is available.
	 * @return true if it is available, otherwise false
	 */
	private boolean isNetworkAvailable( ) {
		// Assume it's not
		boolean networkIsAvailable = false;

		// Check to see if we have an available network and it's connected
		ConnectivityManager connectivityManager = (ConnectivityManager) context_.getSystemService( Context.CONNECTIVITY_SERVICE );
		NetworkInfo networkInfo = connectivityManager .getActiveNetworkInfo();

		if (networkInfo != null && networkInfo.isConnected()) {
			networkIsAvailable = true;
		}

		return networkIsAvailable;
	}

	/**
	 * Perform a database update by downloading the new entries from 
	 * the remote server and then dropping and creating he database anew
	 * inserting the list of retrieved sites.
	 * This method will also notify any listeners as the update progresses
	 */
	private void updateDatabase( double newDbVersion ) {
		Log.i(TAG, "Performing update to DB version " + newDbVersion);
		
		List<Site> siteList = readRemoteData( );
		
		// FIXME 
		// Here we iterate over all sites read from remote DB and insert
		// facebook data where we have it.
		for( Site site : siteList ) {
			String facebookId = fbNodeIdMap_.get(site.getName());
			if( facebookId != null ) {
				site.setFacebookNodeId(facebookId);
			}
		}
		// End of FIXME

		// Open the database
		EcoDatabaseHelper helper = new EcoDatabaseHelper(context_);
		SQLiteDatabase database = helper.getWritableDatabase();

		// Delete the current records
		helper.dropAndCreate(database);

		// Insert new ones
		notifyListenersNumSites( siteList.size() );
		int siteIndex = 1;

		// Insert each site
		for( Site site : siteList ) {
			// Add the site to the database
			ContentValues cv = new ContentValues();
			cv.put(EcoDatabaseHelper.SITE_NAME, site.getName());
			cv.put(EcoDatabaseHelper.SITE_DESCRIPTION, site.getDescription());
			cv.put(EcoDatabaseHelper.SITE_TYPE, site.getType());
			cv.put(EcoDatabaseHelper.SITE_LINK, site.getLink());
			cv.put(EcoDatabaseHelper.SITE_LATITUDE, site.getLatitude());
			cv.put(EcoDatabaseHelper.SITE_LONGITUDE, site.getLongitude());
			cv.put(EcoDatabaseHelper.SITE_ICON, site.getIcon());
			cv.put(EcoDatabaseHelper.SITE_CARBON_SAVING, site.getCarbonSaving());
			cv.put(EcoDatabaseHelper.SITE_FACEBOOK_ID, site.getFacebookNodeId());

			if( database.insert(EcoDatabaseHelper.TABLE_SITES, null, cv) == -1 ) {
				Log.w(TAG, "Failed to insert record" );
			}

			// Notify the listeners
			notifyListenersSiteIndex( siteIndex );
			++siteIndex;
		}
		
		// Update the local DB version number
		ContentValues cv = new ContentValues();
		cv.put( EcoDatabaseHelper.SITE_VERSION, newDbVersion);
		if( database.insert(EcoDatabaseHelper.TABLE_SITES_VERSION, null, cv) == -1) { 
			Log.w(TAG, "Failed to update local database version number" );
		}
		database.close();
	}

	/**
	 * Check whether the remote database has changed version and update the local one if it has
	 * Where the forceUpdate flag is set, no version check is performed but a update of the
	 * database is forced.
	 * 
	 * @param forceUpdate Don't check whether an update is needed, just re sycnh the data 
	 * from the remote server
	 */
	public void checkAndUpdate( boolean forceUpdate ) {
		// First of all check for network connectivity without which we can't update
		if( isNetworkAvailable( ) ) {
			
			// Get the remote database version
			double remoteDBVersion = getRemoteDatabaseVersion( );
			Log.i(TAG, "Remote DB Version is " + remoteDBVersion );

			// If we are forcing update, don't bother getting version for comparison
			if( !forceUpdate ) {
				// If there is a more recent DB version, we want to download it.
				double localDBVersion = getLocalDatabaseVersion();
				Log.i(TAG, "Local DB Version is " + localDBVersion );
				if( remoteDBVersion > localDBVersion) {
					Log.i(TAG, "Update is required");
					forceUpdate = true;
				} else {
					Log.i(TAG, "No update is required");
				}
			} else {
				Log.i(TAG, "Forcing update");
			}

			// If we are forcing the update, do it
			if( forceUpdate ) {
				updateDatabase( remoteDBVersion ) ;
			}
		} else {
			Log.e( getClass( ).getCanonicalName(), "Network unavailable. Cannot update." );
		}
	}



	/**
	 * Utility method to extract a URL from a Property file, handling exceptions
	 * @param properties The Properties object
	 * @param propertyName The name of the Property
	 * @return the URL object corresponding to the property or null if invalid
	 */
	private URL readURLFromProperties( Properties properties, String propertyName ) {
		URL url = null;
		String urlAsString = properties.getProperty(propertyName) ;

		if( urlAsString != null ) {
			try {
				url = new URL( urlAsString);
			} catch( MalformedURLException e ) {
				Log.e( getClass( ).getCanonicalName(), "Invalid URL for "+propertyName, e);
			}
		} else {
			Log.e( getClass( ).getCanonicalName(), "Missing property "+propertyName);
		}

		return url;
	}

	/**
	 * 
	 * @return the version of the remote database as a double
	 */
	private double getRemoteDatabaseVersion( ) {
		double version = 0.0;

		// Open a connection to the remote version URL
		// and get an inputStream
		URLConnection 	connection = null; 
		InputStream 	inputStream = null;
		try {
			connection = versionURL_.openConnection();
			inputStream = connection.getInputStream();
		} catch( IOException e ) {
			Log.e( getClass( ).getCanonicalName(), "Problem getting remote database version.", e);

			// Tidy up. NB inputStream must be null to get here
			if( connection != null ) {
				connection = null;
			}
		}

		// If we opened the stream correctly, read it.
		if( inputStream != null ) {
			// Wrap inputStream as an Inputsource
			InputSource source = new InputSource( inputStream );

			/** Handling XML */
			SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
			DataVersionContentHandler handler = new DataVersionContentHandler( );

			try {
				SAXParser saxParser = saxParserFactory .newSAXParser();
				XMLReader xmlReader = saxParser.getXMLReader();

				xmlReader.setContentHandler( handler );
				xmlReader.parse( source );
			} catch (ParserConfigurationException e) {
				Log.e( getClass().getCanonicalName(), "Problem creating parser", e);
			} catch (SAXException e) {
				Log.e( getClass().getCanonicalName(), "Problem parsing Sites from input", e);
			} catch (IOException e) {
				Log.e( getClass().getCanonicalName(), "Problem parsing Sites from input", e);
			}
			version = handler.getVersion();
		} else {
			Log.e( getClass().getCanonicalName(), "inputStream was null");
		}

		// FIXME We're over reporting the local database version to force an update
		// This will cause the Facebook data to be read and then written to DB
		// Once FB data is included in the 'official' data stream, this code should be removed
		// and the legal feed updated to any version > 0.45
		version = 0.45;
		// End of FIXME

		return version;
	}

	/**
	 * 
	 * @return The version of the local database if it exists
	 */
	private double getLocalDatabaseVersion( ) {
		double localVersion = 0.0;

		EcoDatabaseHelper eOpenHelper = new EcoDatabaseHelper(context_);
		SQLiteDatabase sqlDB = eOpenHelper.getWritableDatabase();

		if (sqlDB.isOpen()) {
			//Fetch the local sites version
			Cursor cursor = sqlDB.query(EcoDatabaseHelper.TABLE_SITES_VERSION, // table 
					new String[] { EcoDatabaseHelper.SITE_VERSION }, // Columns 
					null, // Where clause
					null, // Where clause variables
					null, // Group by
					null, // Having
					null);// Order by

			// Really ought to be non-null
			if( cursor.getCount() > 0 ) {
				cursor.moveToFirst();
				localVersion = cursor.getDouble(0);
				cursor.close();
			} else {
				// Not a problem, there are no records so we don't have a version
				Log.i( getClass().getCanonicalName(), "No records in local version database" );
			}
			sqlDB.close();
		} else {
			Log.e( getClass().getCanonicalName(), "Local version database not open" );
		}

		return localVersion;
	}

	/**
	 * Parse a list of Sites from an XML input stream
	 * 
	 * @param inputStream an InputStream containing an XML representatipon of a list of Sites
	 * @return
	 */
	private List<Site> readRemoteData( ) {
		List<Site> siteList = null;

		// Open a connection to the remote version URL
		// and get an inputStream
		URLConnection 	connection = null; 
		InputStream 	inputStream = null;
		try {
			connection = dataURL_.openConnection();
			inputStream = connection.getInputStream();
		} catch( IOException e ) {
			Log.e( getClass( ).getCanonicalName(), "Problem getting remote data.", e);

			// Tidy up. NB inputStream must be null to get here
			if( connection != null ) {
				connection = null;
			}
		}



		if( inputStream != null ) {
			// Wrap inputStream as an Inputsource
			InputSource source = new InputSource( inputStream );

			/** Handling XML */
			SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
			SiteListContentHandler handler = new SiteListContentHandler( );

			try {
				SAXParser saxParser = saxParserFactory .newSAXParser();
				XMLReader xmlReader = saxParser.getXMLReader();

				// Parse XML using handler
				xmlReader.setContentHandler( handler );
				xmlReader.parse( source );
			} catch (ParserConfigurationException e) {
				Log.e( getClass().getCanonicalName(), "Problem creating parser", e);
			} catch (SAXException e) {
				Log.e( getClass().getCanonicalName(), "Problem parsing Sites from input", e);
			} catch (IOException e) {
				Log.e( getClass().getCanonicalName(), "Problem parsing Sites from input", e);
			}
			siteList = handler.getSiteList();
		} else {
			Log.e( getClass().getCanonicalName(), "inputStream was null");
		}

		return siteList;
	}

	/*
	 * Listener Management
	 */

	/**
	 * Notify any listeners that we're about to download n sites	 * @
	 * @param numSites The number of sites to be downloaded
	 */
	private void notifyListenersNumSites( int numSites ) {
		synchronized( listeners_ ) {
			for ( DatabaseUpdaterListener listener : listeners_ ) {
				try {
					listener.willDownloadSites(numSites);
				} catch (Throwable t) {
					Log.w( listener.getClass().getCanonicalName(), "Problem with DatabaseUpdateListener", t);
				}
			}
		}
	}

	/**
	 * Notify any listeners of progress
	 * @param numSites The number of sites to be downloaded
	 */
	private void notifyListenersSiteIndex( int siteIndex ) {
		synchronized( listeners_ ) {
			for ( DatabaseUpdaterListener listener : listeners_ ) {
				try {
					listener.willDownloadSite(siteIndex);
				} catch (Throwable t) {
					Log.w( listener.getClass().getCanonicalName(), "Problem with DatabaseUpdateListener", t);
				}
			}
		}
	}

	/**
	 * Add a new Listener. If the Listener is already in the list
	 * It is not added again.
	 * @param listener The listener to add.
	 */
	public void addListener( DatabaseUpdaterListener listener ) {
		synchronized( listeners_ ) {
			if( ! listeners_.contains(listener) ) { 
				listeners_.add(listener);
			}
		}
	}
}
