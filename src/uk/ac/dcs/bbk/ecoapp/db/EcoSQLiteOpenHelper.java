/**
 * 
 */
package uk.ac.dcs.bbk.ecoapp.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * @author Giorgio
 * 
 */
public class EcoSQLiteOpenHelper extends SQLiteOpenHelper {
	public final static int VERSION = 1;
	public final static String DB_NAME = "ecoapp.db";
	public final static String TABLE_SITES = "sites";
	public final static String SITE_ID = "id";
	public final static String SITE_NAME = "sname";
	public final static String SITE_DESCRIPTION = "description";
	public final static String SITE_TYPE = "type";
	public final static String SITE_LATITUDE = "latitude";
	public final static String SITE_LONGITUDE = "longitude";
	public final static String SITE_ICON = "icon";
	public final static String TABLE_SITES_VERSION = "sites_version";
	public final static String SITE_VERSION = "version";

	public EcoSQLiteOpenHelper(Context context) {
		super(context, DB_NAME, null, VERSION);
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite
	 * .SQLiteDatabase)
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		dropAndCreate(db);
	}

	protected void dropAndCreate(SQLiteDatabase db) {
		db.execSQL("drop table if exists " + TABLE_SITES + ";");
		db.execSQL("drop table if exists " + TABLE_SITES_VERSION + ";");
		createTables(db);
	}
	
	protected void createTables(SQLiteDatabase db) {
		db.execSQL(
				"create table " + TABLE_SITES +" (" +
				SITE_ID + " integer primary key autoincrement not null," +
				SITE_NAME + " text," +
				SITE_DESCRIPTION + " text," +
				SITE_TYPE + " text," +
				SITE_LATITUDE + " double," +
				SITE_LONGITUDE + " double," +
				SITE_ICON + " text" +
				");"
			);
		db.execSQL(
				"create table " + TABLE_SITES_VERSION +" (" +
				SITE_VERSION + " double" +
				");"
			);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite
	 * .SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		Log.i("EcoApp", "onUpgrade");
	}

}
