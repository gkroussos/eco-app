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
	public static final String DB_NAME = "ecoapp.db";
	public final static String TABLE_LOCATIONS = "locations";
	public final static String LOC_ID = "id";
	public final static String LOC_NAME = "name";
	public final static String LOC_COORDINATE = "coordinate";
	public final static String TABLE_LOCPUBDATE = "locpubdate";
	public final static String PDATE = "pdate";

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
		db.execSQL("drop table if exists " + TABLE_LOCATIONS + ";");
		db.execSQL("drop table if exists " + TABLE_LOCPUBDATE + ";");
		createTables(db);
	}
	
	protected void createTables(SQLiteDatabase db) {
		db.execSQL(
				"create table " + TABLE_LOCATIONS +" (" +
				LOC_ID + " integer primary key not null," +
				LOC_NAME + " text," +
				LOC_COORDINATE + " text" +
				");"
			);
		db.execSQL(
				"create table " + TABLE_LOCPUBDATE +" (" +
				PDATE + " text" +
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
