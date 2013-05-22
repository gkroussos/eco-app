/**
 * 
 */
package uk.ac.bbk.dcs.ecoapp.db;

import java.util.ArrayList;
import java.util.List;

import uk.ac.bbk.dcs.ecoapp.model.Site;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * This class is a set of helper functions for the EcoApp database. Extended and amended
 * from the original EcoSQLiteOpenHelper (and renamed for clarity)
 * @author Giorgio
 * @author Dave Durbin
 * 
 */
public class EcoDatabaseHelper extends SQLiteOpenHelper {
	public final static int VERSION = 4;

	/** Database name */
	public final static String DB_NAME = "ecoapp.db";
	
	/** Table name for Sites DB */
	public final static String TABLE_SITES = "sites";
	
	/** Table name for schema version */
	public final static String TABLE_SITES_VERSION = "sites_version";

	/** Column names for sites table */
	public final static String SITE_ID = "id";
	public final static String SITE_NAME = "sname";
	public final static String SITE_DESCRIPTION = "description";
	public final static String SITE_TYPE = "type";
	public final static String SITE_LINK = "link";
	public final static String SITE_LATITUDE = "latitude";
	public final static String SITE_LONGITUDE = "longitude";
	public final static String SITE_ICON = "icon";
	public final static String SITE_CARBON_SAVING = "carbonSaving";
	public final static String SITE_FACEBOOK_ID = "facebookId";
	
	/** Column names for sites_version table */
	public final static String SITE_VERSION = "version";

	// Column indices for cursors when retrieving records
	private static final int NAME_COL_IDX = 0;
	private static final int DESCRIPTION_COL_IDX = 1;
	private static final int TYPE_COL_IDX = 2;
	private static final int LINK_COL_IDX = 3;
	private static final int LATITUDE_COL_IDX = 4;
	private static final int LONGITUDE_COL_IDX = 5;
	private static final int ICON_COL_IDX = 6;
	private static final int CARBON_SAVING_IDX = 7;
	private static final int FACEBOOK_ID_IDX = 8;
	
	/** Sites table creation SQL */
	private static final String SITE_TABLE_CREATE_SQL = "create table " + TABLE_SITES + 
			" (" +
				SITE_ID + " integer primary key autoincrement not null," +
				SITE_NAME + " text," +
				SITE_DESCRIPTION + " text," +
				SITE_TYPE + " text," +
				SITE_LINK + " link," +
				SITE_LATITUDE + " double," +
				SITE_LONGITUDE + " double," +
				SITE_ICON + " text," +
				SITE_CARBON_SAVING + " long," +
				SITE_FACEBOOK_ID + " text" +
			");";
	
	/** Sites version table create SQL */
	private static final String SITE_VERSION_TABLE_CREATE_SQL = "create table " + TABLE_SITES_VERSION +
			" (" +
				SITE_VERSION + " double" +
			");";

	/** Sites table drop SQL */
	private static final String SITE_TABLE_DROP_SQL = "drop table if exists " + TABLE_SITES + ";";
	
	/** Sites version table drop SQL */
	private static final String SITE_VERSION_TABLE_DROP_SQL ="drop table if exists " + TABLE_SITES_VERSION + ";"; 


	/**
	 * Default constructor
	 * 
	 * @param context
	 */
	public EcoDatabaseHelper(Context context) {
		super(context, DB_NAME, null, VERSION);
	}

	/**
	 * Drop the tables and recreate them
	 *
	 * @param database
	 */
	public void dropAndCreate(SQLiteDatabase database) {
		database.execSQL( SITE_TABLE_DROP_SQL);
		database.execSQL( SITE_VERSION_TABLE_DROP_SQL);
		
		createTables(database);
	}

	/**
	 * Create the Sites and Version tables in the database
	 * @param database The SQLite database
	 */
	private void createTables(SQLiteDatabase database) {
		database.execSQL( SITE_TABLE_CREATE_SQL );
		database.execSQL( SITE_VERSION_TABLE_CREATE_SQL );
	}

	/**
	 * Read the database record at the given cursor position
	 * into a Site object
	 * @param cursor The cursor
	 * @return a Site
	 */
	private Site getSiteFromRecordAtCursor( Cursor cursor ) {
		Site site = new Site();
		site.setName(cursor.getString( NAME_COL_IDX));
		site.setDescription(cursor.getString(DESCRIPTION_COL_IDX));
		site.setType(cursor.getString(TYPE_COL_IDX));
		site.setLink(cursor.getString(LINK_COL_IDX));
		site.setLatitude(cursor.getDouble(LATITUDE_COL_IDX));
		site.setLongitude(cursor.getDouble(LONGITUDE_COL_IDX));
		site.setIcon(cursor.getString(ICON_COL_IDX));
		site.setCarbonSaving(cursor.getLong(CARBON_SAVING_IDX));
		site.setFacebookNodeId(cursor.getString(FACEBOOK_ID_IDX));

		return site;
	}

	/**
	 * Get list of Sites from local database.
	 * 
	 */
	public List<Site> getSites( ) {
		// Create an empty list to populate
		ArrayList<Site> siteList = new ArrayList<Site>( );


		// Attempt the read
		SQLiteDatabase sqlDB = getWritableDatabase();

		// Check that it's open
		if (sqlDB.isOpen()) {
			// query the sites data
			String[] columnsToRetrieve = new String[] { 
					SITE_NAME, SITE_DESCRIPTION, SITE_TYPE,
					SITE_LINK, SITE_LATITUDE, SITE_LONGITUDE, 
					SITE_ICON, SITE_CARBON_SAVING, SITE_FACEBOOK_ID };

			Cursor cursor = sqlDB.query(TABLE_SITES, columnsToRetrieve, null, null, null, null, null);
			// If the query succeeded
			if (cursor != null) {
				// If there are some records
				if( cursor.getCount() > 0 ) {
					cursor.moveToFirst();
					do {
						Site site = getSiteFromRecordAtCursor( cursor );
						siteList.add(site);
					} while (cursor.moveToNext());
				}
				cursor.close();
			}
			sqlDB.close();
		}
		return siteList;
	}

	
	//
	// Implementation of SQLiteOpenHelper abstract methods
	//
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite
	 * .SQLiteDatabase)
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		// Drop and recreate the tables
		dropAndCreate(db);
	}

	/*
	 * (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Do nothing
	}
}
