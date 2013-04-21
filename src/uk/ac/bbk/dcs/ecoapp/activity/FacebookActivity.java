package uk.ac.bbk.dcs.ecoapp.activity;

import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.FacebookClient.AccessToken;
import com.restfb.Parameter;
import com.restfb.exception.FacebookException;
import com.restfb.types.Page;
import com.restfb.types.Post;
import com.restfb.types.User;

import uk.ac.bbk.dcs.ecoapp.R;
import uk.ac.bbk.dcs.ecoapp.model.FBWallPost;
import uk.ac.bbk.dcs.ecoapp.utility.FacebookAccessor;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import java.util.List;

/*
import com.facebook.AccessToken;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
*/



/**
 * This class is an Activity representing recent Facebook wall posts by InMidTown
 * 
 * @author William Linden
 *
 */
public class FacebookActivity  extends Activity  {

	
	protected static final String TAG =  "EcoApp:FacebookActivity";
	FacebookAccessor fbAccessor = new FacebookAccessor();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.facebook_view);
		
		/*
		  final FacebookClient facebookClient = new DefaultFacebookClient("BAACEdEose0cBAJCv3ZAHqcAqFDZA3bochFlj92WBhkZBFST4iR1QMQv7bUTzSnwhEwGwumBQRDJfri0HK1mev54iihOMjFDx1eL1Em9V9UZCLtiusYHJ");
		
		  Connection<Post> myFeed = facebookClient.fetchConnection("me/feed", Post.class, Parameter.with("limit", 100));
		  for (List<Post> myFeedConnectionPage : myFeed)
		      for (Post post : myFeedConnectionPage)
		        Log.i(TAG,"Post from my feed: " + post);
		  */
		  
		
		/*
		//Object queryButton;
		//queryButton.setOnClickListener(new View.OnClickListener() {
		    //@Override
		    //public void onClick(View v) {
				//AccessToken token = new AccessToken("BAACEdEose0cBAJYtK6iCJ6ZBXZCXhW0SR6iyYaUjZCO4D3NsEGHB89xUqGK4A88ZBoDuIPcRrrdEuiZAkzt4bo0RGqQ7QZCC9cYDYnSVulKAff1LZBYRVr7");
		        String fqlQuery = "SELECT name FROM user WHERE uid IN " +
		                "(SELECT uid2 FROM friend WHERE uid1 = me()LIMIT 2)";
		        Bundle params = new Bundle();
		        params.putString("q", fqlQuery);
		        Session session = Session.getActiveSession();
		        //session.
		        Request request = new Request(session,
		            "/fql",                         
		            params,                         
		            HttpMethod.GET,                 
		            new Request.Callback(){         
		                public void onCompleted(Response response) {
		                    Log.i(TAG, "Result: " + response.toString());
		                }                  
		        }); 
		        Request.executeBatchAsync(request);                 
		    //}
		//});
		*/
		
		new faceBookPosts().execute("BAACEdEose0cBALlUe79sJLWISCZCr132MkMuMBWYbjBYFOvoffaBh21m8ETb0uMtQi2YIKEiz4AEjwJouEUxz9RE75zWwljRwVs1oxAMVciH7erEyqkJR9TvLK7b17eCVFs15DENCZBiup9HXREMvi2dZAXczPxNRUWXVqHluK0EXZCy1gBORZA2OtgxUEANkcARTYGziKwZDZD");
	}
	
	private class faceBookPosts extends AsyncTask {
		
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
		//@Override
		protected Connection<Post> doInBackground(Object... params) {
			
			
			List<FBWallPost> fbWallPosts = fbAccessor.getFBWallPosts();
			int count = 1;
			for (FBWallPost post : fbWallPosts) {
				Log.i(TAG,count++ + "] "+ post.toString());
			}
			/*String APP_ID = "537394989644448";
			String APP_SECRET = "a86acad3f1186eab42bd1d6173833f7b";
			AccessToken accessToken =
					  new DefaultFacebookClient().obtainAppAccessToken(APP_ID, APP_SECRET);
			
			Log.i(TAG,"My application access token: " + accessToken.getAccessToken());
					
			//final FacebookClient facebookClient = new DefaultFacebookClient(params[0].toString());
			final FacebookClient facebookClient = new DefaultFacebookClient(accessToken.getAccessToken());
			

			
			//Connection<Post> feed = facebookClient.fetchConnection("me/feed", Post.class, Parameter.with("limit", 15));
			//Log.i(TAG,"Filtered feed count: " + feed.getData().size());
			
			*/
			/*List<Post> myFeedConnectionPage = feed.getData();
	        for (Post post : myFeedConnectionPage)
	        	Log.i(TAG,"Post from my feed: " + post.getMessage());
	        */
			
			/*
			int count = 0;
			
			Page page = facebookClient.fetchObject("GoToMidtown", Page.class);
			// ID: 292361304193814
			
			Log.i(TAG,"Page likes: " + page.getLikes());
			*/
			//page.
			// Do a custom stream fql .. map to class using:
			// SELECT created_time,message,permalink FROM stream WHERE source_id=292361304193814 LIMIT 50

			
			/*
		 * 			String query = "SELECT uid, name FROM user WHERE uid=220439 or uid=7901103";
					List<FqlUser> users = facebookClient.executeFqlQuery(query, FqlUser.class);
					
					out.println("Users: " + users);
					
					...
					
					// Holds results from an "executeFqlQuery" call.
					// You need to write this class yourself!
					// Be aware that FQL fields don't always map to Graph API Object fields.
					
					public class FqlUser {
					  @Facebook
					  String uid;
					  
					  @Facebook
					  String name;
					
					  @Override
					  public String toString() {
					    return String.format("%s (%s)", name, uid);
					  }
					}
			 * 
			 * ***/
			
			/*
			String query = "SELECT name FROM user WHERE uid=220439 or uid=7901103";

			try {
			  List<User> users = facebookClient.executeFqlQuery(query, User.class); 
			} catch (FacebookException e) {
				  // Looks like this API method didn't really return a list of users
				Log.i(TAG,"Error EcoApp: " + e.getStackTrace());
			}
			*/
			/***
			 * Working for single logged in user... 
			 */
			/*
			for (List<Post> myFeedConnectionPage : feed)
			{
				// llll
			      for (Post post : myFeedConnectionPage)
			    	  if (post.getMessage() != null)
			    		  Log.i(TAG,"Post from my feed [" + count + "] :" + post.getMessage() + "\n" + post.getLink() + "\n" + post.getType());
			      if (count++ > 0) break;
			}
			*/
			//return feed;
			return null;
		}

	
	}
	
	
	
}
