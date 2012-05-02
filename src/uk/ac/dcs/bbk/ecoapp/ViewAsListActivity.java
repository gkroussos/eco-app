package uk.ac.dcs.bbk.ecoapp;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;
import java.util.Comparator;
import uk.ac.dcs.bbk.ecoapp.db.EcoSQLiteOpenHelper;
import uk.ac.dcs.bbk.ecoapp.db.Site;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView; 
import android.widget.Toast;   


public class ViewAsListActivity extends ListActivity {

	private EcoSQLiteOpenHelper eOpenHelper;
	private SQLiteDatabase sqlDB;
	private ArrayList<Site> sitesList;
	private ArrayList<HashMap<String, Object>> listItems;
	private SimpleAdapter listItemAdapter;
	private String text;
	private Class<ViewAsMapActivity> mapActivity = ViewAsMapActivity.class;
	double currentLatitude;
	double currentLongitude;
	double distance;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.viewaslist2);

		getCurrentLocation();
		
		initListView();
		this.setListAdapter(listItemAdapter);
		
	}

	private void getCurrentLocation(){
		
	  // Define a listener that responds to location updates -- new code from here:
          LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                makeUseOfNewLocation(location);
            }
            public void onStatusChanged(String provider, int status, Bundle extras) {}
            public void onProviderEnabled(String provider) {}
            public void onProviderDisabled(String provider) {}
          };
 
          try {
            // Acquire a reference to the system Location Manager
            LocationManager locationManager =
                (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
            // Register the listener with the Location Manager to receive location updates
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            // Register the listener with the Location Manager to receive location updates
    		//locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
          } catch (SecurityException e) {
             // requires ACCESS_FINE_LOCATION permission
             // v.setText(e.getMessage());
          }

	}
	
	private void makeUseOfNewLocation(Location loc) {
        	TextView v = (TextView)this.findViewById(R.id.SiteName);
       		// String text = "lat: " + loc.getLatitude() + ", long: " + loc.getLongitude();
        	text = "lat: " + loc.getLatitude() + ", long: " + loc.getLongitude();
        	v.setText(text);
        
        	currentLatitude  = loc.getLatitude();
        	currentLongitude = loc.getLongitude();
        
    	}

	private void initListView() {

		sitesList = new ArrayList<Site>();
		listItems = new ArrayList<HashMap<String, Object>>();

		// Read from DB		
		eOpenHelper = new EcoSQLiteOpenHelper(this);
		sqlDB = eOpenHelper.getWritableDatabase();

		if (sqlDB.isOpen()) {
			// query the sites data
			Cursor cursor = sqlDB.query(EcoSQLiteOpenHelper.TABLE_SITES,
					new String[] { EcoSQLiteOpenHelper.SITE_NAME,
							EcoSQLiteOpenHelper.SITE_ICON,
							EcoSQLiteOpenHelper.SITE_DESCRIPTION,
							EcoSQLiteOpenHelper.SITE_TYPE,
							EcoSQLiteOpenHelper.SITE_LATITUDE,
							EcoSQLiteOpenHelper.SITE_LONGITUDE }, null, null,
					null, null, null);
			if (cursor != null) {
				cursor.moveToFirst();
				if (!cursor.isAfterLast()) {
					do {
						Site site = new Site();
						site.setName(cursor.getString(0));
						site.setIcon(cursor.getString(1));
						site.setDescription(cursor.getString(2));
						site.setType(cursor.getString(3));
						site.setLatitude(cursor.getDouble(4));
						site.setLongitude(cursor.getDouble(5));
						sitesList.add(site);
					} while (cursor.moveToNext());
				}
				cursor.close();
			}
			sqlDB.close();
		}

		// sorting according to my current location
		
		double dx;//horizontal difference 
		double dy;//vertical difference 

		for (int i = 0; i < sitesList.size(); i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("SiteName", sitesList.get(i).getName());
			map.put("SiteDescription", sitesList.get(i).getDescription());
			map.put("SiteIcon", R.drawable.ic_launcher);
			//map.put("SiteButton", R.drawable.ic_launcher);
			
			dx   = currentLongitude - sitesList.get(i).getLongitude();        //horizontal difference 
			dy   = currentLatitude  - sitesList.get(i).getLatitude();         //vertical difference 
			distance    = Math.sqrt( dx*dx + dy*dy ); //distance using Pythagoras theorem
			map.put("SiteDistance", distance);
			
			listItems.add(map);
		}

		// sort a map according to distance 
//		Comparator comparator = Collections.checkedSortedMap(listItems, "SiteDistance");
//		Collections.sort(listItems,comparator);
		
		listItemAdapter = new SimpleAdapter(this, listItems,
				R.layout.list_item, new String[] { "SiteName", "SiteIcon", "SiteDescription", "SiteButton" },
				new int[] { R.id.SiteName, R.id.SiteIcon, R.id.SiteDescription, R.id.SiteButton });
		
		listItemAdapter.setViewBinder(new ViewBinder() {
			public boolean setViewValue(View view, Object data,
					String textRepresentation) {
				if (view instanceof ImageView && data instanceof Bitmap) {
					ImageView iv = (ImageView) view;
					iv.setImageBitmap((Bitmap) data);
					return true;
				} else
					return false;
			}
		});

	}
	
	
	public void onSetHome(View v){
		// Do something when the button is clicked
	    Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show();
	    
	    //set button image
	    //v.setBackgroundDrawable(getResources().getDrawable(R.drawable.highlightednodrop));
	    
	}
	
	public void onSetMe(View v){
		
		// Do something when the button is clicked
	    Toast.makeText(this, "Me", Toast.LENGTH_SHORT).show();
	    
		//set button image
	    //v.setBackgroundDrawable(getResources().getDrawable(R.drawable.highlightednodrop));
	}

	public void onSetMap(View v){
		
		startActivity(new Intent(ViewAsListActivity.this, mapActivity));
		
		ViewAsListActivity.this.finish();
		
		// Do something when the button is clicked
	    Toast.makeText(this, "Map", Toast.LENGTH_SHORT).show();
	   
	}
	
	// Search Button Click
	public void onSearch(View v){
		
		// Do something when the button is clicked
	    Toast.makeText(this, "Search", Toast.LENGTH_SHORT).show();
		
	}	

}