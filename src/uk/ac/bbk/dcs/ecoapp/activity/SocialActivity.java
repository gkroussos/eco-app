package uk.ac.bbk.dcs.ecoapp.activity;

import com.restfb.Connection;
import com.restfb.types.Post;


import uk.ac.bbk.dcs.ecoapp.R;
import uk.ac.bbk.dcs.ecoapp.model.FBWallPost;
import uk.ac.bbk.dcs.ecoapp.utility.FacebookAccessor;
import android.app.Activity;
import android.app.ListActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.social_view);	
		
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
	 * Inner class to create threat running in background of main UI thread
	 * @author William Linden
	 */
	private static class SocialPostsTask extends AsyncTask {
		
		public SocialActivity socialActivity;
		private FacebookAccessor fbAccessor = new FacebookAccessor();
		/*protected Connection<Post> doInBackground(String... token) {
			final FacebookClient facebookClient = new DefaultFacebookClient(token[0]);
			return facebookClient.fetchConnection("me/feed", Post.class, Parameter.with("limit", 100));
			
		}*/

		/*
		 protected void onPostExecute(Connection<Post> feed) {
	         
			 for (List<Post> myFeedConnectionPage : feed)
			      for (Post post : myFeedConnectionPage)
			        Log.i(TAG,"Post from my feed: " + post);
			 
			 //mImageView.setImageBitmap(result);
	     }
*/
		@Override
		protected Connection<Post> doInBackground(Object... params) {
			
			
			List<FBWallPost> fbWallPosts = fbAccessor.getFBWallPosts();
			int count = 1;
			for (FBWallPost post : fbWallPosts) {
				Log.i(TAG,count++ + "] "+ post.toString());
			}
			
			return null;
		}

	
	}
	
	
	
}
