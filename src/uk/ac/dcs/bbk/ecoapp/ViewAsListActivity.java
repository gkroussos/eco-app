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
<<<<<<< HEAD
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
=======
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
>>>>>>> view as list part by Chatitze
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
		
<<<<<<< HEAD
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
=======
		//-------------------------- list item click --------------------------
		//try
		  ListView lv = getListView();
		  lv.setTextFilterEnabled(true);
		 // lv.setClickable(true); // new

		  lv.setOnItemClickListener(new OnItemClickListener() {
		  
			  public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
			  	
			//	  Object o = parent.getItemAtPosition(2);
			//	  String urltext2 = o.toString();
				  
				//  TextView textView = (TextView) lv.findViewById(R.id.SiteDescription);
				  TextView textView = (TextView) parent.findViewById(R.id.SiteDescription);
				  String urltext = textView.getText().toString(); 
				    
				 
				  Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(urltext));
				  startActivity(browserIntent); 
		      }
		  });
		//--------------------------- list item click -------------------------
>>>>>>> view as list part by Chatitze

		
	}
	
	private void makeUseOfNewLocation(Location loc) {
        	TextView v = (TextView)this.findViewById(R.id.SiteName);
       		// String text = "lat: " + loc.getLatitude() + ", long: " + loc.getLongitude();
        	text = "lat: " + loc.getLatitude() + ", long: " + loc.getLongitude();
        	v.setText(text);
        
        	currentLatitude  = loc.getLatitude();
        	currentLongitude = loc.getLongitude();
        
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
		// show my curent location in the first list items SiteName
     //   TextView v = (TextView)this.findViewById(R.id.SiteName);
     //   text = "lat: " + loc.getLatitude() + ", long: " + loc.getLongitude();
     //   v.setText(text);
        
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
		
		double dx;//horizontal difference 
		double dy;//vertical difference 

		// sorting according to my current location
		
		double dx;//horizontal difference 
		double dy;//vertical difference 

		for (int i = 0; i < sitesList.size(); i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("SiteName", sitesList.get(i).getName());
			map.put("SiteDescription", sitesList.get(i).getDescription());
<<<<<<< HEAD
			map.put("SiteIcon", R.drawable.ic_launcher);
			//map.put("SiteButton", R.drawable.ic_launcher);
=======
			
			String imgUrl = sitesList.get(i).getIcon();
			// imgUrl = "http://www.dcs.bbk.ac.uk/~qhuang01/video_chat.png";
			if (imgUrl.startsWith("http://")) {
				map.put("SiteIcon", getBitmap(imgUrl));
			} else {
				map.put("SiteIcon", R.drawable.default_logo);
			}
>>>>>>> view as list part by Chatitze
			
			dx   = currentLongitude - sitesList.get(i).getLongitude();        //horizontal difference 
			dy   = currentLatitude  - sitesList.get(i).getLatitude();         //vertical difference 
			distance    = Math.sqrt( dx*dx + dy*dy ); //distance using Pythagoras theorem
<<<<<<< HEAD
			map.put("SiteDistance", distance);
=======
			map.put("SiteDistance", new Double(distance));
>>>>>>> view as list part by Chatitze
			
			listItems.add(map);
			
		}

<<<<<<< HEAD
		// sort a map according to distance 
//		Comparator comparator = Collections.checkedSortedMap(listItems, "SiteDistance");
//		Collections.sort(listItems,comparator);
		
		listItemAdapter = new SimpleAdapter(this, listItems,
				R.layout.list_item, new String[] { "SiteName", "SiteIcon", "SiteDescription", "SiteButton" },
				new int[] { R.id.SiteName, R.id.SiteIcon, R.id.SiteDescription, R.id.SiteButton });
=======
		// sort a map according to distance 		
		Collections.sort ( listItems , new HashMapComparator2 () ) ;
		
		listItemAdapter = new SimpleAdapter(this, listItems,
				R.layout.list_item, new String[] { "SiteName","SiteDescription", "SiteIcon", "ArrowButton" },
				new int[] { R.id.SiteName, R.id.SiteDescription, R.id.SiteIcon, R.id.ArrowButton });
>>>>>>> view as list part by Chatitze
		
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
<<<<<<< HEAD

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
		
		onSearchRequested();
           
	}
	
	@Override
    	public boolean onSearchRequested() {
        	return super.onSearchRequested();
    	}
    	
	@Override
	public void onNewIntent(Intent intent) {
	    super.onNewIntent(intent);      
	    setIntent(intent);
	   // handleIntent(intent);
	}	
=======

	}
	
	
	public void onSetHome(View v){
		// Do something when the button is clicked
	    Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show();
	    
	    //set button image
	    //v.setBackgroundDrawable(getResources().getDrawable(R.drawable.highlightednodrop));
	    
	}
	public void onArrowBtn(View v){
		// Do something when the button is clicked
	    Toast.makeText(this, "Arrow", Toast.LENGTH_SHORT).show();
	    
	    //set button image
	    //v.setBackgroundDrawable(getResources().getDrawable(R.drawable.highlightednodrop));
	    
	}
	
	
	public void onSetAboutUs(View v){
		
		// Do something when the button is clicked
	    Toast.makeText(this, "About Us", Toast.LENGTH_SHORT).show();
	    
	}

	public void onSetMap(View v){
		
		startActivity(new Intent(ViewAsListActivity.this, mapActivity));
		//ViewAsListActivity.this.finish();
		
	}
	
	// Search Button Click
	public void onSearch(View v){
		onSearchRequested();
	}
	
	@Override
    public boolean onSearchRequested() {
        return super.onSearchRequested();
    }
	
	@Override
	public void onNewIntent(Intent intent) {
	    super.onNewIntent(intent);      
	    setIntent(intent);
	   // handleIntent(intent);
	}
	
	public Bitmap getBitmap(String imageUrl) {
		Bitmap mBitmap = null;
		try {
			URL url = new URL(imageUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			InputStream is = conn.getInputStream();
			mBitmap = BitmapFactory.decodeStream(is);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return mBitmap;
	}
	
	public class HashMapComparator2 implements Comparator
    {
        public int compare ( Object object1 , Object object2 )
        {
                Double obj1Value = ( Double ) ( ( HashMap ) object1 ).get ( "SiteDistance" ) ;
                Double obj2Value = ( Double ) ( ( HashMap ) object2 ).get ( "SiteDistance" ) ;

                return obj1Value.compareTo ( obj2Value ) ;
        }
    }


>>>>>>> view as list part by Chatitze
}