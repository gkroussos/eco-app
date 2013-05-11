package uk.ac.bbk.dcs.ecoapp.utility;

import java.util.ArrayList;
import java.util.List;

import uk.ac.bbk.dcs.ecoapp.model.FBWallPost;
import uk.ac.bbk.dcs.ecoapp.model.SocialPost;

import android.util.Log;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.FacebookClient.AccessToken;
import com.restfb.exception.FacebookException;

public class FacebookAccessor {
	protected static final String TAG =  "EcoApp:FacebookActivity";
	
	// Could use android's R resource, but requires passing in context so using local constants instead
	// as Facebook APP ID and APP SECRET are "hidden" in bytecode as it should really only come from a "server side" scenario.
	private final String APP_ID = "537394989644448";
	private final String APP_SECRET = "a86acad3f1186eab42bd1d6173833f7b";
	private final String INMIDTOWN_FB_ID = "292361304193814";
	private List<FBWallPost> fbWallPosts = new ArrayList<FBWallPost>();

	public List<? extends SocialPost> getFBWallPosts() {

		// Authentication, use facebook generated AppId and Secret
		AccessToken accessToken =
				  new DefaultFacebookClient().obtainAppAccessToken(APP_ID, APP_SECRET);
		
		// Leaving this commented out code in for later testing purposes, once InMidTown ID/Secret is used
		// Log.i(TAG,"My application access token: " + accessToken.getAccessToken());

		final FacebookClient facebookClient = new DefaultFacebookClient(accessToken.getAccessToken());
		
	
		// Finding FB account specific ID, use graph.facebook.com ID: https://graph.facebook.com/wrlinden --> 654937022
		// GoToInMidtown --> 292361304193814
		
		String query = "SELECT created_time,message,permalink FROM stream WHERE source_id=" + INMIDTOWN_FB_ID + " LIMIT 50";
		
		try {
		  List<FBWallPost> allFbWallPosts = facebookClient.executeFqlQuery(query, FBWallPost.class); 
		  
		  for (FBWallPost fbp : allFbWallPosts) {
			  if (fbp.getMessage() != null && fbp.getMessage().length() > 0)
				  fbWallPosts.add(fbp);
		  }
		  
		} catch (FacebookException e) {
			  // A problem - log and get out graciously
			Log.i(TAG,"Error EcoApp: " + e.getStackTrace());
			return null;
		}
		

		return fbWallPosts;
	}
	
	
	
	
}
