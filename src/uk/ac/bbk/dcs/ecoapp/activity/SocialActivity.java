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
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;


/**
 * This class is an Activity representing recent Social posts (currently Facebook only) by InMidTown
 * 
 * @author William Linden
 *
 */
public class SocialActivity  extends ListActivity  {

	protected static final String TAG =  "EcoApp:SocialActivity";
	//private SocialPostsTask socialPostsTask; 
	private SocialAdapter socialAdapter;
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
		
		setListAdapter(socialAdapter);

		//list item click
		//ListView lv = getListView();
		//lv.setTextFilterEnabled(true);
		
		
		//new SocialPosts().execute("BAACEdEose0cBALlUe79sJLWISCZCr132MkMuMBWYbjBYFOvoffaBh21m8ETb0uMtQi2YIKEiz4AEjwJouEUxz9RE75zWwljRwVs1oxAMVciH7erEyqkJR9TvLK7b17eCVFs15DENCZBiup9HXREMvi2dZAXczPxNRUWXVqHluK0EXZCy1gBORZA2OtgxUEANkcARTYGziKwZDZD");
		//new SocialPostsTask().execute();
		
		 ListView lv = getListView();
	        //lv.setClickable(true);
	        /* // Not registering clisk!!! 
			lv.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
					String urltext = currentSocialPosts.get(position).getPermalink();
					Log.i(TAG,"Entering onItemClick!!!!!!");
					tracker.trackEvent(
							"AtSocialPage", // category
							"Click", // Action
							"ListItem", // Label
							position //value
							);
					Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(urltext));
					startActivity(browserIntent); 
				}
			});
			*/  
			new SocialPostsTask(this).execute();
		
		// This bit to avoid activity / view problems with multi threading and re-orientation of device (Especially when dialog showing)
		// Method is deprecated, should be using ActivityFragment | Leave for now 
		 /*socialPostsTask = (SocialPostsTask) getLastNonConfigurationInstance();
	        if(socialPostsTask == null) {
	            socialPostsTask = new SocialPostsTask();
	        }
	        socialPostsTask.socialActivity = this;
	        if(socialPostsTask.getStatus() == AsyncTask.Status.PENDING) {
	            socialPostsTask.execute();
	        }
	    */
	        
	        	}
/* // Not registering clicks... 
	@Override
	protected void onListItemClick (ListView lv, View view, int position, long id){
		super.onListItemClick(lv, view, position, id);
		 int place=position;
	        Log.i(TAG,"Position:" +position+"");
		//Toast.makeText(getApplicationContext(), ((TextView) view).getText(), Toast.LENGTH_SHORT).show();
    }
	*/

	/*
    @Override
    public Object onRetainNonConfigurationInstance() {
        return socialPostsTask;
    }
	*/
	/**
	 * Inner class to create thread running in background of main UI thread
	 * @author William Linden
	 */
	private class SocialPostsTask extends AsyncTask <Void, Void, List<SocialPost>> {
		
		public SocialPostsTask(SocialActivity socialActivity) {
			this.socialActivity = socialActivity;
		}
		
		private SocialActivity socialActivity;

		private FacebookAccessor fbAccessor = new FacebookAccessor();
		/*protected Connection<Post> doInBackground(String... token) {
			final FacebookClient facebookClient = new DefaultFacebookClient(token[0]);
			return facebookClient.fetchConnection("me/feed", Post.class, Parameter.with("limit", 100));
			
		}*/

		
		// Fork a thread off, hit social sites asynchronously to build 
		// Build in progress indicator (spinning circle?)
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
			
			/* // ALSO Not registering clicks... 
			lv.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
					String urltext = currentSocialPosts.get(position).getPermalink();
					Log.i(TAG,"Entering onItemClick!!!!!!");
					tracker.trackEvent(
							"AtSocialPage", // category
							"Click", // Action
							"ListItem", // Label
							position //value
							);
					Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(urltext));
					startActivity(browserIntent); 
				}
			})*/;  
			
			
			
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
		// Get the post associated with this button
		final SocialPost post = (SocialPost) view.getTag();

		// Extract the permalink and use for analytics 
		String link = (post != null) ? post.getPermalink() : "unknown";
		
		// Log it
		tracker.trackEvent(
				"AtSocialPage", // category
				"Click", // Action
				"ListItem(" + link + ")", // Label
				0 //value
				);
		
		// No need for an entire Activity/View here. Simply use a Dialog
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(post.getMessage())
		       .setTitle(post.getCreated_time())
		       .setPositiveButton(R.string.closeBtnText, null) // don't require an actionListner 
		       .setNegativeButton(R.string.openFacebookText, new DialogInterface.OnClickListener() {
		    	   public void onClick(DialogInterface dialog, int id) {
		    		   Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(post.getPermalink()));
		    			startActivity(browserIntent); 
		    	   }
        });
		AlertDialog dialog = builder.create();
		dialog.show();
		
		
		
		
		// Now navigate to the detail view - passing the selected site name
		/*Intent intent =new Intent(this, DetailViewActivity.class);
		ParcelableSite ps = new ParcelableSite( post );
		intent.putExtra(ActivityConstants.EXTRA_SITE_NAME, ps);
		startActivity(intent);*/    
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
		// Go to "home" ie ListViewActivity
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

		// Go to AboutUs
		startActivity(new Intent(this, AboutUsActivity.class));

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

		// Flash a Toast notice
		Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show();
		  
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
