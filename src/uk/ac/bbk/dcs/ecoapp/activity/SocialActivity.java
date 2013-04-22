package uk.ac.bbk.dcs.ecoapp.activity;

import com.restfb.Connection;
import com.restfb.types.Post;


import uk.ac.bbk.dcs.ecoapp.R;
import uk.ac.bbk.dcs.ecoapp.activity.helper.ActivityConstants;
import uk.ac.bbk.dcs.ecoapp.activity.helper.ParcelableSite;
import uk.ac.bbk.dcs.ecoapp.activity.helper.SiteAdapter;
import uk.ac.bbk.dcs.ecoapp.activity.helper.SocialAdapter;
import uk.ac.bbk.dcs.ecoapp.model.FBWallPost;
import uk.ac.bbk.dcs.ecoapp.model.Site;
import uk.ac.bbk.dcs.ecoapp.model.SocialPost;
import uk.ac.bbk.dcs.ecoapp.utility.FacebookAccessor;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import java.util.List;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;


/**
 * This class is an Activity representing recent Social posts (currently Facebook only) by InMidTown
 * 
 * 
 * @author William Linden
 *
 */
public class SocialActivity  extends ListActivity  {

	protected static final String TAG =  "EcoApp:SocialActivity";
	private SocialPostsTask socialPostsTask; 
	private static SocialAdapter socialAdapter;
	private static List<SocialPost> currentSocialPosts; // social posts shown on UI
	GoogleAnalyticsTracker tracker;
	
	
//	static List<SocialPost> socialPosts;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.social_view);	
		tracker = GoogleAnalyticsTracker.getInstance();
		tracker.startNewSession("UA-30293248-1", this);
		tracker.trackPageView("UserAtSocialPage");
		
		// Construct an adapter for the List  
		//socialAdapter = new SocialAdapter(this, android.R.id.list, socialPosts);
		
		//setListAdapter(socialAdapter);

		//list item click
		//ListView lv = getListView();
		//lv.setTextFilterEnabled(true);
		
		
		//new SocialPosts().execute("BAACEdEose0cBALlUe79sJLWISCZCr132MkMuMBWYbjBYFOvoffaBh21m8ETb0uMtQi2YIKEiz4AEjwJouEUxz9RE75zWwljRwVs1oxAMVciH7erEyqkJR9TvLK7b17eCVFs15DENCZBiup9HXREMvi2dZAXczPxNRUWXVqHluK0EXZCy1gBORZA2OtgxUEANkcARTYGziKwZDZD");
		//new SocialPostsTask().execute();
		
		// This bit to avoid activity / view problems with multi threading and re-orientation of device
		// Method is deprecated, should be using ActivityFragment | Leave for now 
		 socialPostsTask = (SocialPostsTask) getLastNonConfigurationInstance();
	        if(socialPostsTask == null) {
	            socialPostsTask = new SocialPostsTask();
	        }
	        socialPostsTask.socialActivity = this;
	        if(socialPostsTask.getStatus() == AsyncTask.Status.PENDING) {
	            socialPostsTask.execute();
	        }
	    
	}

    @Override
    public Object onRetainNonConfigurationInstance() {
        return socialPostsTask;
    }
	
	/**
	 * Inner class to create thread running in background of main UI thread
	 * @author William Linden
	 */
	private static class SocialPostsTask extends AsyncTask <Void, Void, List<SocialPost>> {
		
		public SocialActivity socialActivity;
		private FacebookAccessor fbAccessor = new FacebookAccessor();
		/*protected Connection<Post> doInBackground(String... token) {
			final FacebookClient facebookClient = new DefaultFacebookClient(token[0]);
			return facebookClient.fetchConnection("me/feed", Post.class, Parameter.with("limit", 100));
			
		}*/

		
		@Override
		protected List<SocialPost> doInBackground(Void... params) {
			
			if (currentSocialPosts == null || currentSocialPosts.size() <= 0) {
			
				List<SocialPost> socialPosts =  (List<SocialPost>) fbAccessor.getFBWallPosts();
				/*int count = 1;
				for (SocialPost post : socialPosts) {
					Log.i(TAG,count++ + "] "+ post.toString());
				}*/
				return socialPosts;
			}
			Log.i(TAG,"Skipping currentSocialPosts are already present");
			return null;
		}

		@Override
        protected void onPostExecute(List<SocialPost> socialPosts) {
			// Construct an adapter for the List  only update if in bound List<SocialPost> has been updated ie not null
			if (socialPosts != null) currentSocialPosts = socialPosts;
			socialAdapter = new SocialAdapter(socialActivity, android.R.id.list, currentSocialPosts);
			//
			socialActivity.setListAdapter(socialAdapter);

			//list item click
			ListView lv = socialActivity.getListView();
			lv.setTextFilterEnabled(true);
        }
		
		
		
		/*
		 protected void onPostExecute(Connection<Post> feed) {
	         
			 for (List<Post> myFeedConnectionPage : feed)
			      for (Post post : myFeedConnectionPage)
			        Log.i(TAG,"Post from my feed: " + post);
			 
			 //mImageView.setImageBitmap(result);
	     }
*/
	
	}
	
	/* Action Listeners / UI callbacks */
	
	/**
	 * Respond to ArrowButton click on a list item by showing that item in a details view
	 * @param view
	 */
	public void onArrowBtn(View view){
		// Get the site associated with this button
		SocialPost post = (SocialPost) view.getTag();
		Log.i(TAG," onArrowBtn fired!!!! "+ post.toString());
		// Extract the Site name from the listItem
		String link = "unknown";
		if( post != null ) {
			link = post.getPermalink();
		}
		
		// Log it
		tracker.trackEvent(
				"AtSocialPage", // category
				"Click", // Action
				"ListItem(" + link + ")", // Label
				0 //value
				);
		// No need for an entire Activity here. Simply use a Dialog
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(post.getMessage())
		       .setTitle(post.getCreated_time())
		       .setPositiveButton("close", null); // don't require an actionListner 
		AlertDialog dialog = builder.create();
		dialog.show();
		
		
		// Now navigate to the detail view - passing the selected site name
		/*Intent intent =new Intent(this, DetailViewActivity.class);
		ParcelableSite ps = new ParcelableSite( post );
		intent.putExtra(ActivityConstants.EXTRA_SITE_NAME, ps);
		startActivity(intent);*/    
	}

	
	
}
