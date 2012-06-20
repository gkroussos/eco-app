package uk.ac.dcs.bbk.ecoapp;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.sax.Element;
import android.sax.RootElement;
import android.util.Log;
import org.xml.sax.Attributes;

import uk.ac.dcs.bbk.ecoapp.db.EcoSQLiteOpenHelper;
import uk.ac.dcs.bbk.ecoapp.db.Site;

import android.sax.EndElementListener;
import android.sax.EndTextElementListener;
import android.sax.StartElementListener;
import android.widget.ProgressBar;

public class EcoAppActivity extends Activity {

	private ArrayList<Site> sitesList = new ArrayList<Site>();
	private EcoSQLiteOpenHelper eOpenHelper;
	private SQLiteDatabase sqlDB;
	private ProgressBar updateDbBar;
	private String versionString = null;
	private Site site;
	private String versionUrl = null;
	private String dataUrl = null;
	private Class<ViewAsListActivity> listActivity = ViewAsListActivity.class;
	private Class<ViewAsMapActivity> mapActivity = ViewAsMapActivity.class;
	private static int GET_VERSION = 0;
	private static int GET_DATA = 1;
	private GoogleAnalyticsTracker tracker;
	protected int splashTime = 4000; // Milliseconds to display the loading
										// screen
	private static int CONNECT_TIMEOUT = 4000;
	private boolean networkAvailable = true; // Network availability
	private MediaPlayer mp;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ecoapp);
		
		// the progress bar
		updateDbBar = (ProgressBar) this.findViewById(R.id.updateDbBar);
		updateDbBar.setIndeterminate(true);

		// play background sound
		mp = new MediaPlayer();
		mp = MediaPlayer.create(EcoAppActivity.this, R.raw.startup);
		mp.start();

		// check the network state
		networkAvailable = this.checkNetworkAvailability(EcoAppActivity.this);

		if (networkAvailable) {

			// start the location logging service
			if (!isLocationLoggingServiceRunning()) {
				this.startService(new Intent(this, LocationLoggingService.class));
				Log.i("LocationLoggingService", "Start service from Loading Activity.");
			}

			// For Ray
			tracker = GoogleAnalyticsTracker.getInstance();
			tracker.startNewSession("UA-30293248-1", this);
			tracker.trackPageView("UserOpenEcoApp");
			/*tracker.trackEvent("Button", // Category
					"PageView", // Action
					"OpenApp", // Label
					0); // Value */
			tracker.dispatch(); 

			// get the XML data properties
			Properties p = new Properties();
			try {
				p.load(this.getResources().openRawResource(R.raw.data));
				versionUrl = p.getProperty("url.version");
				Log.i("data version url", versionUrl);

				dataUrl = p.getProperty("url.data");
				Log.i("data url", dataUrl);

			} catch (NotFoundException e1) {
				// TODO Auto-generated catch block
				Log.e("Property file not found", e1.getMessage());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				Log.e("Property file IO error", e1.getMessage());
			}

			// get data version
			xmlParser(versionUrl, GET_VERSION);

			try {
				eOpenHelper = new EcoSQLiteOpenHelper(this);
				sqlDB = eOpenHelper.getWritableDatabase();

				double localVersion = 0;
				if (sqlDB.isOpen()) {
					// fetch the local sites version
					Cursor cursor = sqlDB.query(EcoSQLiteOpenHelper.TABLE_SITES_VERSION, new String[] { EcoSQLiteOpenHelper.SITE_VERSION }, null, null, null, null, null);
					if (cursor != null) {
						cursor.moveToFirst();
						if (!cursor.isAfterLast()) {
							do {
								localVersion = cursor.getDouble(0);
							} while (cursor.moveToNext());
						}
						cursor.close();
					}
				}

				Log.i("localVersion", localVersion + "");

				double siteVersion = 0;
				if (versionString != null) {
					siteVersion = Double.parseDouble(versionString);
					Log.i("siteVersion", versionString);
				}

				// the local data version is out of date
				if (siteVersion > 0 && localVersion < siteVersion) {

					updateDbBar.setIndeterminate(false);

					// get data
					xmlParser(dataUrl, GET_DATA);

					// there do have some new sites data
					if (sitesList.size() > 0) {
						updateDbBar.setMax(sitesList.size());

						int rsPro = 0;

						// reset the database
						eOpenHelper.onCreate(sqlDB);
						for (int i = 0; i < sitesList.size(); i++) {

							Site loc = sitesList.get(i);
							ContentValues cv = new ContentValues();
							cv.put(EcoSQLiteOpenHelper.SITE_NAME, loc.getName());
							cv.put(EcoSQLiteOpenHelper.SITE_DESCRIPTION, loc.getDescription());
							cv.put(EcoSQLiteOpenHelper.SITE_TYPE, loc.getType());
							cv.put(EcoSQLiteOpenHelper.SITE_LATITUDE, loc.getLatitude());
							cv.put(EcoSQLiteOpenHelper.SITE_LONGITUDE, loc.getLongitude());
							cv.put(EcoSQLiteOpenHelper.SITE_ICON, loc.getIcon());
							long rs = sqlDB.insert(EcoSQLiteOpenHelper.TABLE_SITES, null, cv);

							if (rs != -1) {
								updateDbBar.setProgress(i);
								rsPro++;
							}

						}

						// all new data has been inserted into tables
						if (rsPro == sitesList.size()) {
							// update the local data version
							ContentValues vcv = new ContentValues();
							vcv.put(EcoSQLiteOpenHelper.SITE_VERSION, siteVersion);
							sqlDB.insert(EcoSQLiteOpenHelper.TABLE_SITES_VERSION, null, vcv);

						}
					}

				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Log.e("Eco App Error", e.getMessage());
			} finally {
				if (sqlDB != null && sqlDB.isOpen()) {
					sqlDB.close();
				}
			}

		}
		gotoTargetActivity();

	}

	/**
	 * parse XML file
	 * 
	 * @param urlString
	 *            - URLs of the XML files
	 * @param type
	 *            - 0: get version; 1: get data
	 */
	private void xmlParser(String urlString, int type) {
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			Log.i("SAXParser", "Start parsing");
			SAXParser parser = factory.newSAXParser();
			XMLReader xmlreader = parser.getXMLReader();
			URL url = new URL(urlString);
			URLConnection con = url.openConnection();
			con.setConnectTimeout(CONNECT_TIMEOUT);
			InputStream in = con.getInputStream();
			InputSource is = new InputSource(in);
			if (type == EcoAppActivity.GET_VERSION) {
				xmlreader.setContentHandler(getVersionRootElement().getContentHandler());
			} else if (type == EcoAppActivity.GET_DATA) {
				xmlreader.setContentHandler(getDataRootElement().getContentHandler());
			}
			xmlreader.parse(is);
			Log.i("SAXParser", "End parsing");
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			Log.e("ParserConfigurationException", e.getMessage());
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			Log.e("SAXException", e.getMessage());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			Log.e("MalformedURLException", e.getMessage());
		} catch (IOException e) {// cannot access the Internet
			// TODO Auto-generated catch block
			Log.e("XML file is not accessable", e.getMessage());
		}
	}

	/**
	 * Go to target Activity after 'Delayed' time
	 */
	private void gotoTargetActivity() {

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {

				if (networkAvailable) {
					startActivity(new Intent(EcoAppActivity.this, listActivity));
				} else {
					startActivity(new Intent(EcoAppActivity.this, mapActivity));
				}

				EcoAppActivity.this.finish();
			}
		}, splashTime);

	}

	/**
	 * version XML parser, fetch data version from XML
	 * 
	 * @return a root XML element
	 */
	private RootElement getVersionRootElement() {

		RootElement rootElement = new RootElement("ecoapp");
		Element verElement = rootElement.getChild("dataversion");
		verElement.setEndTextElementListener(new EndTextElementListener() {
			@Override
			public void end(String body) {
				versionString = body;
			}
		});

		return rootElement;

	}

	/**
	 * data XML parser, fetch data from XML
	 * 
	 * @return a root XML element
	 */
	private RootElement getDataRootElement() {

		RootElement rootElement = new RootElement("sites");

		Element siteElement = rootElement.getChild("site");
		siteElement.setStartElementListener(new StartElementListener() {
			@Override
			public void start(Attributes attributes) {
				site = new Site();
			}
		});
		siteElement.setEndElementListener(new EndElementListener() {
			@Override
			public void end() {
				sitesList.add(site);
			}
		});

		Element nameElement = siteElement.getChild("name");
		nameElement.setEndTextElementListener(new EndTextElementListener() {
			@Override
			public void end(String body) {
				site.setName(body);
			}
		});

		Element descElement = siteElement.getChild("description");
		descElement.setEndTextElementListener(new EndTextElementListener() {
			@Override
			public void end(String body) {
				site.setDescription(body);
			}
		});

		Element typeElement = siteElement.getChild("type");
		typeElement.setEndTextElementListener(new EndTextElementListener() {
			@Override
			public void end(String body) {
				site.setType(body);
			}
		});

		Element locElement = siteElement.getChild("location");
		locElement.setEndTextElementListener(new EndTextElementListener() {
			@Override
			public void end(String body) {
				double latitude = 0, longitude = 0;
				String[] coordinates = null;
				try {
					coordinates = body.split(",");
					latitude = Double.parseDouble(coordinates[0]);
					Log.i("latitude", latitude + "");
					longitude = Double.parseDouble(coordinates[1]);
					Log.i("longitude", longitude + "");
				} catch (NullPointerException e) {
					// TODO Auto-generated catch block
					Log.e("coordinates empty error", e.toString());
				} catch (NumberFormatException e1) {
					// TODO Auto-generated catch block
					Log.e("coordinates format error", e1.toString());
				}
				site.setLatitude(latitude);
				site.setLongitude(longitude);
			}
		});

		Element iconElement = siteElement.getChild("icon");
		iconElement.setEndTextElementListener(new EndTextElementListener() {
			@Override
			public void end(String body) {
				site.setIcon(body);
			}
		});

		return rootElement;

	}

	/**
	 * check network availability
	 * 
	 * @param activity
	 *            - current activity
	 * @return boolean - true: network available; false: no network
	 */
	public boolean checkNetworkAvailability(Activity activity) {

		ConnectivityManager cManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo nInfo = cManager.getActiveNetworkInfo();
		if (nInfo == null || !nInfo.isConnected()) {
			return false;
		} else {
			return true;
		}

	}

	private boolean isLocationLoggingServiceRunning() {
		ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if ("uk.ac.dcs.bbk.ecoapp.LocationLoggingService".equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// TODO Auto-generated method stub
		if (mp != null) {
			mp.release();
			mp = null;
		}

	}

}