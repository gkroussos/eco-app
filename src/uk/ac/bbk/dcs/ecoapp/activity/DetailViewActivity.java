package uk.ac.bbk.dcs.ecoapp.activity;

import uk.ac.bbk.dcs.ecoapp.R;
import uk.ac.bbk.dcs.ecoapp.db.Site;
import uk.ac.bbk.dcs.ecoapp.utility.AsynchImageLoader;
import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailViewActivity extends Activity {
	/** Logo */
	ImageView	siteLogo_;

	/** Site name */
	TextView 	siteNameView_;

	/** Site description*/
	TextView	siteDescriptionView_;

	/** Site address */
	TextView	siteAddressView_;

	/** Site telephone */
	TextView	siteTelephoneView_;

	/** URL */
	TextView	siteURLView_;

	/**
	 * Bind the local fields to layout components
	 */
	private void bindFields( ) {
		siteLogo_ = (ImageView) findViewById(R.id.site_logo);
		siteNameView_ = (TextView) findViewById(R.id.site_name);
		siteDescriptionView_ = (TextView) findViewById(R.id.site_description);
		siteAddressView_ = (TextView) findViewById(R.id.site_address);
		siteTelephoneView_ = (TextView) findViewById(R.id.site_telephone);
		siteURLView_ = (TextView) findViewById(R.id.site_url);
	}



	/**
	 * Set a new Site to display
	 * @param site The Site
	 */
	protected void setSite( Site site ) {
		// Handle icon
		AsynchImageLoader.getSingletonInstance().loadToImageView(site.getIcon(), siteLogo_);
		siteNameView_.setText(site.getName( ));
		siteDescriptionView_.setText( site.getDescription());
	}
	
	/**
	 * 
	 */
	public void onCreate( Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.detail_view);

		// Bind fields
		bindFields( );

		// Retrieve the current Site
		Site currentSite = (Site)savedInstanceState.get("current site");
		if( currentSite != null ) {
			setSite( currentSite );
		}
	}
}
