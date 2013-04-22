package uk.ac.bbk.dcs.ecoapp.activity;

import com.restfb.Connection;
import com.restfb.types.Post;


import uk.ac.bbk.dcs.ecoapp.R;
import uk.ac.bbk.dcs.ecoapp.activity.helper.SiteAdapter;
import uk.ac.bbk.dcs.ecoapp.activity.helper.SocialAdapter;
import uk.ac.bbk.dcs.ecoapp.model.FBWallPost;
import uk.ac.bbk.dcs.ecoapp.model.SocialPost;
import uk.ac.bbk.dcs.ecoapp.utility.FacebookAccessor;
import android.app.Activity;
import android.app.ListActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import java.util.List;



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
//	static List<SocialPost> socialPosts;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.social_view);	
		
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
				int count = 1;
				for (SocialPost post : socialPosts) {
					Log.i(TAG,count++ + "] "+ post.toString());
				}
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
	
	
	public void showDetails() {
		
	}
	
	
	
}
