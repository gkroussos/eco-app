package uk.ac.bbk.dcs.ecoapp.activity;

import uk.ac.bbk.dcs.ecoapp.R;
import uk.ac.bbk.dcs.ecoapp.activity.helper.ActivityConstants;
import uk.ac.bbk.dcs.ecoapp.activity.helper.ParcelableSite;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailViewActivity extends Activity {
	/** Logo */
	ImageView	siteIconView_;

	/** Site name */
	TextView 	siteNameView_;

	/** Site description*/
	TextView	siteDescriptionView_;

	/** Site type */
	TextView	siteTypeView_;

	/** Site Link*/
	TextView	siteLinkView_;

	/**
	 * Bind the local fields to layout components
	 */
	private void bindFields( ) {
		siteIconView_ = (ImageView) findViewById(R.id.site_icon);
		siteNameView_ = (TextView) findViewById(R.id.site_name);
		siteDescriptionView_ = (TextView) findViewById(R.id.site_description);
		siteTypeView_ = (TextView) findViewById(R.id.site_type);
		siteLinkView_ = (TextView) findViewById(R.id.site_url);
	}



	/**
	 * Set a new Site to display
	 * @param site The Site
	 */
	protected void setSite( ParcelableSite site ) {
		
		// Handle icon
		// TODO: Implement the Asynch loader 
//		AsynchImageLoader.getSingletonInstance().loadToImageView(site.getIcon(), siteLogo_);
//		siteIconView_.setText(site.getName( ));
		siteNameView_.setText(site.getName( ));
		siteDescriptionView_.setText( site.getDescription());
		siteTypeView_.setText( site.getType());
		siteLinkView_.setText( site.getLink());
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

		// Retrieve the intent that launched this activity
		Intent intent = getIntent();
		
		// Get the current site form the intent (should be stored as an extra)
		ParcelableSite ps = (ParcelableSite) intent.getParcelableExtra(ActivityConstants.EXTRA_SITE_NAME);

		if( ps != null ) {
			setSite( ps );
		}
	}
}
