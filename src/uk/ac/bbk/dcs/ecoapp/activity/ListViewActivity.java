package uk.ac.bbk.dcs.ecoapp.activity;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import uk.ac.bbk.dcs.ecoapp.R;
import uk.ac.bbk.dcs.ecoapp.activity.helper.ActivityConstants;
import uk.ac.bbk.dcs.ecoapp.activity.helper.ParcelableSite;
import uk.ac.bbk.dcs.ecoapp.activity.helper.SiteAdapter;
import uk.ac.bbk.dcs.ecoapp.db.EcoDatabaseHelper;
import uk.ac.bbk.dcs.ecoapp.model.Site;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;


/**
 * This class presents a list view of the InMidtown Sites. Users may select the site from the list by tapping on it to 
 * open the Site's web site in a separate browser window.
 * Users may also tap on the icon to the right of the list item to see a details view for the company
 * 
 * @author Dave Durbin
 * @author William Linden
 *
 */
public class ListViewActivity extends ListActivity
implements LocationListener
{
	/** TAG for logging */
	private final String TAG = getClass( ).getCanonicalName();

	/** List of sites from the local database */
	private List<Site> 					siteList_;

	/** List adapter for the list of items */
	private SiteAdapter					listAdapter_;

	/** Last known latitude or 0.0 */
	private Location					lastKnownLocation_;

	/** semaphore for controlling access to location data */
	private String						locationMutex_ = "LocationMutex";

	/** Google analytics */
	private GoogleAnalyticsTracker		tracker_;

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
	 * Sort the provided list of sites based on distance from the current location updating the
	 * distance field of the sites as a side effect.
	 * 
	 * @param siteList A List of Sites 
	 * @return true if the sort order of the list changed, otherwise false
	 */
	private boolean sortSitesAccordingToDistance( List<Site> siteList ) {
		// First update the distances for each site in the list
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


		// Make a note of the existing sort order
		int[] siteIds = new int[siteList_.size()];
		int i=0;
		for( Site site : siteList_) {
			siteIds[i] = site.getId();
			++i;
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

		// Finally, check whether the sort order has changed
		boolean sortOrderChanged = false;
		i=0;
		for( Site site : siteList_) {
			if( siteIds[i] != site.getId() ) {
				sortOrderChanged = true;
				break;
			}
			++i;
		}

		return sortOrderChanged;
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
	 * Initialise the list view by reading a list of Sites form 
	 */
	private void initListView() {
		// Construct an adapter for the List  
		listAdapter_ = new SiteAdapter(this, android.R.id.list, siteList_);

		setListAdapter(listAdapter_);
//		getListView().setItemsCanFocus(false);
	}

	/*
	 * 
	 */
	@Override
	protected void onListItemClick(ListView lv, View view,int position, long id) {
		Log.d( TAG, "Item clicked");
		Site site = listAdapter_.getItem(position);
		String urltext = site.getLink();
		
		// URL text turns out to be unpredictable. It may or may not include a scheme
		// If not, make one that does and assume it's http://
		if( ! urltext.toLowerCase(Locale.US).startsWith("http")) {
			urltext = "http://"+urltext;
		}
		
		Uri siteUri = Uri.parse(urltext);
		tracker_.trackEvent(
				"AtListPage", // category
				"Click", // Action
				"ListItem", // Label
				position //value
				);
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, siteUri);
		startActivity(browserIntent); 
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
		// Get the site associated with this button
		Site site = (Site) v.getTag();

		// Extract the Site name from the listItem
		String siteName = "unknown";
		if( site != null ) {
			siteName = site.getName( );
		}

		// Log it
		tracker_.trackEvent(
				"AtListPage", // category
				"Click", // Action
				"ListItem(" + siteName + ")", // Label
				0 //value
				);

		// Now navigate to the detail view - passing the selected site name
		Intent intent =new Intent(this, DetailViewActivity.class);
		ParcelableSite ps = new ParcelableSite( site );
		intent.putExtra(ActivityConstants.EXTRA_SITE_NAME, ps);
		intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
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
		Intent intent = new Intent(this, AboutUsActivity.class);  
		intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT); 
		startActivity(intent);
	}

	/** 
	 * Go to the Social view
	 * @param v
	 */
	public void onSocial(View v){
		// Update analytics
		tracker_.trackEvent(
				"AtListPage", // category
				"Click", // Action
				"Social", // Label
				0 //value
				);

		// Go to Social view / Activity
		Intent intent = new Intent(this, SocialActivity.class);  
		intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT); 
		startActivity(intent);

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

		Intent intent = new Intent(ListViewActivity.this, MapViewActivity.class);  
		intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT); 
		startActivity(intent);
	}

	/**
	 * Handle click on search button by launching search
	 * @deprecated replaced by onRefresh(View)
	 */
	public void onSearch(View v){
		onSearchRequested();
	}
	public void onRefresh(View v){
		// Needs implementation  
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
			// Every minute or 100 metres
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 120000l, 100.0f, this );
		} catch (SecurityException e) {
			// Handle exception by logging a warning. May want to use a dialog for this too to alert user
			Log.w(getClass().getCanonicalName( ), "Failed to register with LocationManager", e);
		}
	}

	/**
	 * Respond to change in location by re-sorting the list of Sites according to 
	 * distance from the new Location. If the sort order has changed then we redraw the list
	 * @param newLocation The new location
	 */
	public void onLocationChanged(Location newLocation) {
		// Lock on the lacation mutex to ensure we're not trying to access the location while we change it
		synchronized( locationMutex_ ) {
			lastKnownLocation_ = newLocation;

			// Re-sort the list of sites based on new distances
			boolean sortOrderChanged = sortSitesAccordingToDistance(siteList_);

			// Notify listeners that data has changed (only if it did)
			if( sortOrderChanged ) {
				// This will force a redraw
				listAdapter_.notifyDataSetChanged( );
			}
		}

	}

	// Other methods unused
	public void onStatusChanged(String provider, int status, Bundle extras) {}
	public void onProviderEnabled(String provider) {}
	public void onProviderDisabled(String provider) {}

}