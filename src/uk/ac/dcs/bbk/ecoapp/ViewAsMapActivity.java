package uk.ac.dcs.bbk.ecoapp;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

import android.app.Activity;
import android.os.Bundle;

public class ViewAsMapActivity extends Activity {
    /** Called when the activity is first created. */
	private GoogleAnalyticsTracker tracker3;
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	tracker3 = GoogleAnalyticsTracker.getInstance();
		tracker3.startNewSession("UA-30293248-1", this);
		tracker3.trackPageView("UserAtMapPage");
		/*tracker3.trackEvent("AtMapPage", // Category
				"PageView", // Action
				"AtMapPage", // Label
				0); // Value */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
}