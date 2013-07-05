package uk.ac.bbk.dcs.ecoapp.utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import uk.ac.bbk.dcs.ecoapp.model.FBWallPost;
import uk.ac.bbk.dcs.ecoapp.model.Site;
import uk.ac.bbk.dcs.ecoapp.model.SocialPost;


import android.util.Log;

import com.facebook.Session;
import com.facebook.Session.StatusCallback;
import com.facebook.SessionState;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.FacebookClient.AccessToken;
import com.restfb.Parameter;
import com.restfb.exception.FacebookException;
import com.restfb.types.FacebookType;
import com.restfb.types.Page;

public class FacebookAccessor {

	// Could use android's R resource, but requires passing in context so using local constants instead
	// as Facebook APP ID and APP SECRET are "hidden" in bytecode as it should really only come from a "server side" scenario.
	public static final String APP_ID = "264225457054059";
	private static final String APP_SECRET = "f852bd082338b7c6225d522ca965711d";
	private static final String INMIDTOWN_FB_ID = "292361304193814";

	// This is a stream "table" specific facebook FQL query bringing back 50 results - not all will be displayed;
	// fields are mapped to ..ecoapp.model.SocialPost fields
	protected static String FQL_SELECT_STREAM = "SELECT created_time,message,permalink FROM stream WHERE source_id=" + INMIDTOWN_FB_ID + " LIMIT 50";
	protected static final String TAG =  "EcoApp:FacebookActivity";

	private List<FBWallPost> fbWallPosts = new ArrayList<FBWallPost>();

	public List<? extends SocialPost> getFBWallPosts() {

		// Authentication, use facebook generated AppId and Secret
		try {
			
			AccessToken accessToken = 
					new DefaultFacebookClient().obtainAppAccessToken(APP_ID, APP_SECRET);


			// Leaving this commented out code in for later testing purposes, once InMidTown ID/Secret is used
			// Log.i(TAG,"My application access token: " + accessToken.getAccessToken());

			final FacebookClient facebookClient = new DefaultFacebookClient(accessToken.getAccessToken());


			// Finding FB account specific ID, use graph.facebook.com ID: https://graph.facebook.com/wrlinden --> 654937022
			// GoToInMidtown --> 292361304193814

			//String query = "SELECT created_time,message,permalink FROM stream WHERE source_id=" + INMIDTOWN_FB_ID + " LIMIT 50";
			List<FBWallPost> allFbWallPosts = facebookClient.executeFqlQuery(FQL_SELECT_STREAM, FBWallPost.class); 

			for (FBWallPost fbp : allFbWallPosts) {
				if (fbp.getMessage() != null && fbp.getMessage().length() > 0)
					fbWallPosts.add(fbp);
			}

			return fbWallPosts;

			// Something went wrong, probably a connectivity or Facebook Authentication issue, log and return null  	
		} catch (FacebookException fbe) {
			Log.e(TAG,"Error EcoApp: ", fbe);
		}

		return null;
	}


	/**
	 * 'Like' a Facebook page
	 */
	public void like( Site site) {
		try {
			// Need a personal access token from the Session for this one
			String accessToken = Session.getActiveSession().getAccessToken();

			// Leaving this commented out code in for later testing purposes, once InMidTown ID/Secret is used
			// Log.i(TAG,"My application access token: " + accessToken.getAccessToken());
			final FacebookClient facebookClient = new DefaultFacebookClient(accessToken);
			Page page = facebookClient.fetchObject(site.getFacebookNodeId(), Page.class);
			
			// FIXME Currently FB likes on pages are not supported very well by FB
			// facebookClient.publish(page.getId( )+"/likes", Boolean.class );
			
			// So we publish a simpe message instead
			// FacebookType represents any Facebook Graph Object that has an ID property.
			FacebookType publishMessageResponse =
			  facebookClient.publish("me/feed", FacebookType.class,
			    Parameter.with("message", "[Test] Just used the @[292361304193814] app to like carbon saving ideas by "+site.getName()+" @["+page.getId()+"]"));
		} catch (FacebookException fbe) {
			Log.e( TAG, "Couldn't connect to Facebook", fbe );
		}
	}
}
