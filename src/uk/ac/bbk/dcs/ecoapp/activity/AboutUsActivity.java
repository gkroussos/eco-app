package uk.ac.bbk.dcs.ecoapp.activity;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

import uk.ac.bbk.dcs.ecoapp.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

/**
 * This Activity is simply an about screen displaying information about InMidtown
 * @author George Roussos - And Others from Birkbeck UK
 * @author William Linden
 * 
 */
public class AboutUsActivity extends Activity  {

	private GoogleAnalyticsTracker tracker;
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_us);
		tracker = GoogleAnalyticsTracker.getInstance();
		tracker.startNewSession("UA-30293248-1", this);
		tracker.trackPageView("UserAtSocialPage");
    }
	
	
	

	
	/***
	 * OnClickListeners / UI interaction
	 * */
	
	public void onSetHome(View v){
		tracker.trackEvent(
				"AtSocialPage", // category
				"Click", // Action
				"Home", // Label
				0 //value
				);
		// Go to "Home" (ListViewActivity)
		startActivity(new Intent(this, ListViewActivity.class));  
	}

	/** 
	 * Go to the about us view
	 * @param v
	 */
	public void onSetAboutUs(View v){
		// Update analytics
		tracker.trackEvent(
				"AtSocialPage", // category
				"Click", // Action
				"AboutUs", // Label
				0 //value
				);

		// Make a Hello World Joke
		Toast.makeText(this, "Toasted", Toast.LENGTH_SHORT).show();
	}

	/** 
	 * Go to the Social view
	 * @param v
	 */
	public void onSocial(View v){
		// Update analytics
		tracker.trackEvent(
				"AtSocialPage", // category
				"Click", // Action
				"Social", // Label
				0 //value
				);


		// Go to Social
		startActivity(new Intent(this, SocialActivity.class));
		  
	}
	
	/**
	 * Handle click on Map button by navigating to the Map view
	 * @param v
	 */
	public void onSetMap(View v){
		tracker.trackEvent(
				"AtSocialPage", // category
				"Click", // Action
				"Map", // Label
				0 //value
				);
		startActivity(new Intent(this, MapViewActivity.class));
	}

	/**
	 * Handle click on search button by launching search
	 * @param v
	 */
	public void onSearch(View v){
		onSearchRequested();
	}

	

	
}
