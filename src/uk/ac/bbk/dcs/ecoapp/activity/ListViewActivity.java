package uk.ac.bbk.dcs.ecoapp.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.ac.bbk.dcs.ecoapp.R;
import uk.ac.bbk.dcs.ecoapp.db.EcoDatabaseHelper;
import uk.ac.bbk.dcs.ecoapp.db.Site;
import android.app.LauncherActivity.ListItem;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.Toast;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;


/**
 * This class presents a list view of the InMidtown Sites. Users may select the site from the list by tapping on it to 
 * open the Site's web site in a separate browser window.
 * Users may also tap on the icon to the right of the list item to see a details view for the company
 * 
 * @author Dave Durbin
 *
 */
public class ListViewActivity extends ListActivity
implements LocationListener
{
	/** List of sites from the local database */
	private List<Site> 					siteList_;

	/** List of Site data for mapping to ListItems */
	private List<Map<String, Object>>	listItems_;

	/** List adapter for the list of items */
	SimpleAdapter						listAdapter_;

	/** Last known latitude or 0.0 */
	Location							lastKnownLocation_;

	/** semaphore for controlling access to location data */
	String								locationMutex_ = "LocationMutex";

	/** Google analytics */
	GoogleAnalyticsTracker				tracker_;

	/**
	 * Init the tracker and start a session
	 */
	private void initAnalytics( ) {
		tracker_ = GoogleAnalyticsTracker.getInstance();
		tracker_.startNewSession("UA-30293248-1", this);
		tracker_.trackPageView("UserAtListPage");
		/*tracker2.trackEvent("AtListPage", // Category
				"PageView", // Action
				"AtListPage", // Label
				0); // Value */
	}




	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_view);

		// Update analytics
		initAnalytics();

		// Load sites from database
		loadSitesFromDatabase( );
		
		// Now calculate distances and sort them
		sortSitesAccordingToDistance(siteList_);

		// Create the list view
		initListView();

		// Set up location listener
		listenForLocationChanges();

	}

	/**
	 * Construct a HashMap from the Site for use with the SimpleAdapter
	 * @param site The Site
	 * @return A Map containing the key values of the site 
	 */
	private Map<String, Object> siteToMap( Site site) {
		HashMap<String, Object> map = new HashMap<String, Object>();

		map.put("SiteName", site.getName());
		map.put("SiteDescription", site.getDescription());

		// TODO: Put a lazy loaded icon in this map instead
		map.put("SiteIcon", R.drawable.icon);

		map.put("SiteDistance", Double.valueOf(site.getDistance()));
		map.put("ArrowButton", R.drawable.arrowbtn);

		return map;
	}
	
	/**
	 * Construct a List of Maps for the list adapter
	 * @param siteList The source Sites
	 * @return A List<Map<
	 */
	private List<Map<String, Object>> getListItemsFromSiteList( List<Site> siteList ) {
		// Allocate space for opne entry per Site
		List<Map<String,Object>> listItems = new ArrayList<Map<String,Object>>( siteList.size() );
		for( Site site : siteList ) {
			listItems.add(siteToMap(site));
		}
		
		return listItems;
	}

	/**
	 * For each Site in the list, work out how far the Site is from the last known location
	 * if it's set, and set this as a distance on the site
	 * @param siteList A List of Sites 
	 */
	private void sortSitesAccordingToDistance( List<Site> siteList ) {
		// Current lat and long or 0 if no known location
		double currentLatitude = 0.0;
		double currentLongitude = 0.0;

		synchronized( locationMutex_) {
			if( lastKnownLocation_ != null ) {
				currentLatitude  = lastKnownLocation_.getLatitude(); 
				currentLongitude = lastKnownLocation_.getLongitude();
			}

			// For each site, calculate the distance from here
			for (Site site : siteList) {
				double distance = 0.0;
				
				// Calculate the distance of the site from the last known location
				if( lastKnownLocation_ != null ) {
					double dx   = currentLongitude - site.getLongitude();  // horizontal difference 
					double dy   = currentLatitude  - site.getLatitude();   // vertical difference 
					distance    = Math.sqrt( dx*dx + dy*dy );				// distance using Pythagoras theorem; crude but functional
				}

				site.setDistance(distance);
			}
		}

		// Now perform a sort
		Collections.sort ( siteList_ , new Comparator<Site> (){
			/**
			 * Nearer is sorted earlier
			 */
			public int compare ( Site site1 , Site site2) {
				if( site1.getDistance() < site2.getDistance() ) return -1;
				if( site1.getDistance() > site2.getDistance() ) return 1;
				return 0;
			}
		}) ;
	}

	/**
	 * Load sites from database into local List
	 */
	private void loadSitesFromDatabase( ) {
		// DatabaseHelper
		EcoDatabaseHelper dbHelper = new EcoDatabaseHelper( this );
		siteList_ = dbHelper.getSites( );
	}

	/**
	 * Initialise the list view by reading a list oif Sites form 
	 */
	private void initListView() {
		// Construct listItems or populate from siteList
		listItems_ = getListItemsFromSiteList(siteList_);
		
		// Construct an adapter for the List  
		listAdapter_ = new SimpleAdapter(this, listItems_, R.layout.list_item, 
				new String[] { "SiteName","SiteDescription", "SiteIcon","ArrowButton" },
				new int[] { R.id.site_name, R.id.site_description, R.id.site_icon, R.id.arrow_button});


		// Create  a ViewBinder to handle ImageViews as SimpleAdapter doesn't
		// TODO: May make sense to subclass SimpleAdpater or implement a new Adapter to get
		// around maintaining two lists (one of Sites and one of 
		listAdapter_.setViewBinder(
				new ViewBinder() {

					public boolean setViewValue(View view, Object data, String textRepresentation) {
						if (view instanceof ImageView && data instanceof Bitmap) {
							((ImageView)view).setImageBitmap((Bitmap) data);
							return true;
						} else
							return false;
					}
				});
		setListAdapter(listAdapter_);

		//list item click
		ListView lv = getListView();
		lv.setTextFilterEnabled(true);

		// Enable item clicking and add listener
		lv.setClickable(true);
		lv.setOnItemClickListener(new OnItemClickListener() {


			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				String urltext = siteList_.get(position).getLink();

				tracker_.trackEvent(
						"AtListPage", // category
						"Click", // Action
						"ListItem", // Label
						position //value
						);
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(urltext));
				startActivity(browserIntent); 
			}
		});
	}


	/********************************************************************************
	 *                                                                              *
	 *          Handle user interactions                                            *
	 *                                                                              *
	 ********************************************************************************/

	public void onSetHome(View v){
		tracker_.trackEvent(
				"AtListPage", // category
				"Click", // Action
				"Home", // Label
				0 //value
				);

		// Do something when the button is clicked
		Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show();
	}

	/**
	 * Respond to ArrowButton on a list item by showing that item in the details view
	 * @param v
	 */
	public void onArrowBtn(View v){
		// v should be an arrowButton, the parent should be the list item
		ListItem li = (ListItem) v.getParent();

		String siteName = null;

		// Extract the Site name from the listItem
		tracker_.trackEvent(
				"AtListPage", // category
				"Click", // Action
				"ListItem" + siteName, // Label
				0 //value
				);

		// Now navigate to that view - passing the selected site name
		Intent intent =new Intent(this, DetailViewActivity.class);
		intent.putExtra("SITE_NAME", siteName);
		startActivity(intent);    
	}


	/** 
	 * Go to the about us view
	 * @param v
	 */
	public void onSetAboutUs(View v){
		// Update analytics
		tracker_.trackEvent(
				"AtListPage", // category
				"Click", // Action
				"AboutUs", // Label
				0 //value
				);

		// Go to AboutUs
		startActivity(new Intent(this, AboutUsActivity.class));    
	}

	/**
	 * Handle click on Map button by navigating to the Map view
	 * @param v
	 */
	public void onSetMap(View v){
		tracker_.trackEvent(
				"AtListPage", // category
				"Click", // Action
				"Map", // Label
				0 //value
				);
		startActivity(new Intent(ListViewActivity.this, MapViewActivity.class));
	}

	/**
	 * Handle click on search button by launching search
	 * @param v
	 */
	public void onSearch(View v){
		onSearchRequested();
	}


	/********************************************************************************
	 *                                                                              *
	 *          Location Related methods                                            *
	 *                                                                              *
	 ********************************************************************************/
	/**
	 * Install this Activity as a LocationListener to manage changes in location
	 * and filter the displayed list of businesses appropriately
	 */
	private void listenForLocationChanges( ) {
		try {
			// Acquire a reference to the system Location Manager
			LocationManager locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);

			// Register with the Location Manager to receive location updates
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this );
		} catch (SecurityException e) {
			// Handle exception by logging a warning. May want to use a dialog for this too to alert user
			Log.w(getClass().getCanonicalName( ), "Failed to register with LocationManager", e);
		}
	}

	/**
	 * Stash changed location details and resort Site list
	 * @param location
	 */
	public void onLocationChanged(Location location) {
		// Called when a new location is found by the network location provider.
		synchronized( locationMutex_ ) {
			lastKnownLocation_ = location;

			// Re-sort the list of sites based on new distances
			sortSitesAccordingToDistance(siteList_);

			// Regenerate the list for display
			listItems_ = getListItemsFromSiteList(siteList_);

			// Notify listeners that data has changed
			// TODO Is this necessary?
			listAdapter_.notifyDataSetChanged( );
		}

	}

	// Other methods unused
	public void onStatusChanged(String provider, int status, Bundle extras) {}
	public void onProviderEnabled(String provider) {}
	public void onProviderDisabled(String provider) {}

}