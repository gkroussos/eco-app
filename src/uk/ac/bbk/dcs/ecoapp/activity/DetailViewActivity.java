package uk.ac.bbk.dcs.ecoapp.activity;

import uk.ac.bbk.dcs.ecoapp.R;
import uk.ac.bbk.dcs.ecoapp.activity.helper.ActivityConstants;
import uk.ac.bbk.dcs.ecoapp.activity.helper.ParcelableSite;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public class DetailViewActivity extends Activity {
	/** Logo */
	ImageView	siteIconView_;

	/** Site name */
	TextView 	siteNameView_;

	/** Site description*/
	TextView	siteDescriptionView_;

	/** Site type */
	TextView	siteTypeView_;

	/** Site Link*/
	TextView	siteLinkView_;

	private GoogleAnalyticsTracker tracker;
	protected static final String TAG =  "EcoApp:SocialDetailViewActivity";
	
	/**
	 * Bind the local fields to layout components
	 */
	private void bindFields( ) {
		siteIconView_ = (ImageView) findViewById(R.id.site_icon);
		siteNameView_ = (TextView) findViewById(R.id.site_name);
		siteDescriptionView_ = (TextView) findViewById(R.id.site_description);
		siteTypeView_ = (TextView) findViewById(R.id.site_type);
		siteLinkView_ = (TextView) findViewById(R.id.site_url);
	}



	/**
	 * Set a new Site to display
	 * @param site The Site
	 */
	protected void setSite( ParcelableSite site ) {
		
		// Handle icon
		// TODO: Implement the Asynch loader 
//		AsynchImageLoader.getSingletonInstance().loadToImageView(site.getIcon(), siteLogo_);
//		siteIconView_.setText(site.getName( ));
		siteNameView_.setText(site.getName( ));
		siteDescriptionView_.setText( site.getDescription());
		siteTypeView_.setText( site.getType());
		siteLinkView_.setText( site.getLink());
	}
	
	/**
	 * 
	 */
	public void onCreate( Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail_view);
		
		tracker = GoogleAnalyticsTracker.getInstance();
		tracker.startNewSession("UA-30293248-1", this);
		tracker.trackPageView("UserAtSocialPage");

		// Bind fields
		bindFields( );

		// Retrieve the intent that launched this activity
		Intent intent = getIntent();
		
		// Get the current site form the intent (should be stored as an extra)
		ParcelableSite ps = (ParcelableSite) intent.getParcelableExtra(ActivityConstants.EXTRA_SITE_NAME);

		if( ps != null ) {
			setSite( ps );
		}
	}
	
	

	
	/***
	 * OnClickListeners / UI interaction
	 * */
	
	public void onSetHome(View v){
		tracker.trackEvent(
				"AtSiteDetailPage", // category
				"Click", // Action
				"Home", // Label
				0 //value
				);
		// Go to "Home" (ListViewActivity)
		Intent intent = new Intent(this, ListViewActivity.class);  
		intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT); 
		startActivity(intent);
  
	}

	/** 
	 * Go to the about us view
	 * @param v
	 */
	public void onSetAboutUs(View v){
		// Update analytics
		tracker.trackEvent(
				"AtSiteDetailPage", // category
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
		tracker.trackEvent(
				"AtSiteDetailPage", // category
				"Click", // Action
				"Social", // Label
				0 //value
				);


		// Go to Social
		Intent intent = new Intent(this, SocialActivity.class);  
		intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT); 
		startActivity(intent);		  
	}
	
	/**
	 * Handle click on Map button by navigating to the Map view
	 * @param v
	 */
	public void onSetMap(View v){
		tracker.trackEvent(
				"AtSiteDetailPage", // category
				"Click", // Action
				"Map", // Label
				0 //value
				);
		Intent intent = new Intent(this, MapViewActivity.class);  
		intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT); 
		startActivity(intent);
	}
	
	/**
	 * Handle click on URL, navigate to URL
	 * @param v
	 */
	public void onFollowLink(View v){
		tracker.trackEvent(
				"AtSiteDetailPage", // category
				"Click", // Action
				"GoToSiteURL", // Label
				0 //value
				);
		
		/**
		 *  Nasty little hack: adding scheme into URI depending on http: present or not,
		 *  should make use of URI <-> URL to add scheme when missing 
		 **/
		String url = siteLinkView_.getText().toString();
		if (!url.matches("^http.*")) { url = "http://" + url; }
		// Wrap hack in try/catch for those unexpected bad URLs
		try {
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse( url ));
			startActivity(browserIntent);
		} catch (ActivityNotFoundException ae ) {
			Log.i(TAG, "Bad URL, leading to un associated Activity for URI:" + url);
		}
	}

	
}
