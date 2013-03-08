package uk.ac.bbk.dcs.ecoapp.activity;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import uk.ac.bbk.dcs.ecoapp.LocationLoggingService;
import uk.ac.bbk.dcs.ecoapp.R;
import uk.ac.bbk.dcs.ecoapp.db.DatabaseUpdater;
import uk.ac.bbk.dcs.ecoapp.db.DatabaseUpdaterListener;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

/**
 * This Activity is responsible for displaying a splash screen and playing a short sound to 
 * announce the applications launch
 * If there is network connectivity it will also check for an updfte to the database of known sites
 * and if there is one, will download the new database while updating a progress bar .
 * If there's no network connectivity, the app will still pause for some time before progressing to the 
 * main screen of the application which is the MapView
 * 
 * @author Unknown
 * @author Dave Durbin (updated comments and refactored database updater)
 *
 */
public class SplashScreenActivity extends Activity implements DatabaseUpdaterListener
{
	/** Progress bar - shown when database updates are being made */
	private ProgressBar 			updateDbBar_;

	/** Google Analytics */
	private GoogleAnalyticsTracker	tracker_;

	/** Thread for database updater */
	private Thread					databaseUpdaterThread_;

	/** MediaPLayer reference to stop it going out of scope while playing */
	private MediaPlayer				mediaPlayer_;

	/** Splash screen loading time in milliseconds*/
	private static final int SPLASH_TIME = 4000;

	/**
	 * Play a sound in the background
	 * @param soundResourceID The resource ID for the sound
	 */
	private void playBackgroundSound( int soundResourceID) {
		// Play background sound
		mediaPlayer_ = new MediaPlayer();
		mediaPlayer_ = MediaPlayer.create(this, soundResourceID );
		mediaPlayer_.start();
	}

	/**
	 * Track this activity with analytics
	 */
	private void startGoogleAnalytics( ) {
		// For Ray
		tracker_ = GoogleAnalyticsTracker.getInstance();
		tracker_.startNewSession("UA-30293248-1", this);
		tracker_.trackPageView("UserOpenEcoApp");
		/*
		 * tracker.trackEvent("Button", // Category "PageView", // Action
		 * "OpenApp", // Label 0); // Value
		 */
		tracker_.dispatch();
	}

	/**
	 * Check whether there is a new set of Sites to be downloaded and if so do it.
	 * Will update the progress bar if there are new sites
	 */
	private void checkForNewDatabase( ) {
		// Load the apps properties
		final Properties properties = new Properties();
		try {
			properties.load(getResources().openRawResource(R.raw.data));
		} catch ( IOException e ) {
			Log.e( getClass( ).getCanonicalName(), "Couldn't load properties file", e);
		}

		// Perform the update on a separate Thread
		databaseUpdaterThread_ = new Thread(
				new Runnable(){
					public void run( ) {
						try {
							DatabaseUpdater dbu = new DatabaseUpdater(SplashScreenActivity.this, properties);
							dbu.addListener(SplashScreenActivity.this);
							dbu.checkAndUpdate(false);
						} catch ( Exception e ) {
							Log.e( getClass( ).getCanonicalName(), "Failed to update database", e);
						}
					}
				},
				"Database updater");
		databaseUpdaterThread_.start( );
	}

	/**
	 * onCreate
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		playBackgroundSound( R.raw.startup );

		// Set the default view 
		setContentView( R.layout.splash_screen );

		// Bind the progress bar
		updateDbBar_ = (ProgressBar) this.findViewById(R.id.updateDbBar);
		updateDbBar_.setIndeterminate(true);


		// If the location logging service is not running, start it
		if( !isServiceRunning( LocationLoggingService.class.getName() ) ) {
			startService(new Intent(this, LocationLoggingService.class ) );
			Log.i("LocationLoggingService", "Start service from Loading Activity.");
		}

		startGoogleAnalytics( );

		// Check and update database
		checkForNewDatabase( );

		// If database updater not running or has finished, hide progress bar
		if( databaseUpdaterThread_ == null ) {
			updateDbBar_.setVisibility(View.INVISIBLE);
		}

		// Set up a callback to let me know when the Splash screen could be dismissed
		new Handler().postDelayed(new Runnable() {
			public void run() {
				splashScreenTimedOut();
			}
		}, SPLASH_TIME);
	}

	/**
	 * Callback to dismiss Splash Screen and move to next screen
	 */
	private void splashScreenTimedOut( ) {
		// If the database updater is at work, wait for it
		if( databaseUpdaterThread_ != null ) {
			synchronized( databaseUpdaterThread_ ) {
				try {
					databaseUpdaterThread_.join();
				} catch( InterruptedException e ) {}
			}
			databaseUpdaterThread_ = null;
		}

		// Now transition
		startActivity(new Intent(this, ListViewActivity.class));
		finish();

	}

	/**
	 * Determine whether a named service is running or not
	 * @param serviceName the name of the service to test for
	 * @return true if the service is running, otherwise false.
	 */
	private boolean isServiceRunning( String serviceName) {
		// Assume it's not running
		boolean serviceIsRunning = false;

		ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

		// Obtain list of all runnning services
		List<RunningServiceInfo> runningServices = activityManager.getRunningServices(Integer.MAX_VALUE);

		// Detect our service in the list
		for (RunningServiceInfo serviceInfo : runningServices ) {
			// If we find the name, it's running
			if (serviceName.equals(serviceInfo.service.getClassName())) {
				serviceIsRunning = true;
				break;
			}
		}
		return serviceIsRunning;
	}

	@Override
	/**
	 * Ensure we clean up the MediaPLayer
	 */
	protected void onDestroy() {
		super.onDestroy();
		if (mediaPlayer_ != null) {
			mediaPlayer_.release();
			mediaPlayer_ = null;
		}
	}

	//
	// Methods for DatabaseUpdaterListener
	//
	/**
	 * Called when the DatabaseUpdater has determined that there are a number of sites to be downloaded
	 * @param numSites The Number of sites to be downloaded
	 */
	public void willDownloadSites(final int numSites) {
		updateDbBar_.post(
				new Runnable( ) {
					public void run( ) {
						updateDbBar_.setMax( numSites );
					}
				});
	}

	/**
	 * Called when the DatabaseUpdater is about to download a new site
	 * @param siteIndex the count of the site to be downloaded next
	 */
	public void willDownloadSite(final int siteIndex) {
		updateDbBar_.post(
				new Runnable( ) {
					public void run( ) {
						updateDbBar_.setProgress(siteIndex);
					}
				});
	}

}