/* NOT WORKING AND NOT BEING USED
 * 
 * Activity Starter to encapsulate GoogleAnalytics into a generalized ActityStarting utility 
 * 
 */

/*
package uk.ac.bbk.dcs.ecoapp.activity.helper;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

import uk.ac.bbk.dcs.ecoapp.activity.SocialActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;

public class ActivityStarter {
	
 private GoogleAnalyticsTracker tracker;
 private static ActivityStarter activityStarter;
 
 	// Block default Construct
 	private ActivityStarter() {}
 
 	public ActivityStarter(Context context) {
 		// 'Factory' style - share activityStarter between Activities
 		if (activityStarter == null) {
 			activityStarter = new ActivityStarter(context);
 		}
 		if (tracker == null) {
 			tracker = GoogleAnalyticsTracker.getInstance();
			tracker.startNewSession(ActivityConstants.GOOGLE_ANALYTICS, context);
			//tracker.trackPageView("UserAtListPage");
		}
 	}
 
 
	public <T> void startAnalyticActivity(Activity activity, Class<T> newActivityClass, Context context, String category, String label) {
		// Update analytics
				tracker.trackEvent(
						category, // category
						"Click", // Action
						label, // Label
						0 //value
						);

				// Go to New Social view / Activity
				activity.startActivity(new Intent(context, newActivityClass ));  
	}
	
}
*/