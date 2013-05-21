package uk.ac.bbk.dcs.ecoapp.activity.helper;

import java.util.List;

import uk.ac.bbk.dcs.ecoapp.R;
import uk.ac.bbk.dcs.ecoapp.model.Site;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * This class is a custom resource adapter for the ListView of Sites
 * It maps the Site information into a list_item and tags the arrow button to the right
 * of the list_item with the Site to facilitate handling clicks.
 *  
 * @author Dave Durbin
 *
 */
public class SiteAdapter extends ArrayAdapter<Site> {
	private final String 	TAG = getClass( ).getCanonicalName();
	
	private Context			context_;
	
	private List<Site>		sites_;
	

	/**
	 * Static inner class to hold UI elements of each list item
	 * @author Dave
	 *
	 */
	public static class ViewHolder {
		/** The business name text */
		public TextView		siteNameView_;
		
		/** The type of approach adopted */
		public TextView		siteSummary_;
		
		/** Site's icon ; currently always the inmidtown bee */
		public ImageView	siteIconView_;
		
		/** Separate button linked to a detail page */
		public ImageButton	siteButton_;	
	}

	/**
	 * Construct one 
	 * @param context
	 * @param textViewResourceId
	 * @param sites
	 */
	public SiteAdapter(Context context, int textViewResourceId, List<Site> sites) {
		super(context, textViewResourceId, sites);
		sites_ = sites;
		context_ = context;
	}

	@Override
	/*
	 * (non-Javadoc)
	 * Get a populated view for the item at the given position in the list
	 * 
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		ViewHolder holder = null;
		
		// If the view is not allocated then...
		if (v == null) {
			// Create a new view...
			LayoutInflater vi = (LayoutInflater)context_.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.list_item, null);

			// ...and a new holder
			holder = new ViewHolder();
			
			// Set the elements of the holder to be the elements of the newly created View
			holder.siteNameView_= (TextView) v.findViewById(R.id.site_name);
			holder.siteSummary_ = (TextView) v.findViewById(R.id.site_description);
			holder.siteButton_ = (ImageButton) v.findViewById(R.id.arrow_button);
			holder.siteIconView_ = (ImageView) v.findViewById(R.id.site_icon);
			
			// Set the view's tag to be the holder
			v.setTag(holder);
		} else {
			// Otherwise, just recover the holder from this view
			holder = (ViewHolder)v.getTag();
		}

		// Extract the site corresponding to the position in the list
		final Site site = sites_.get(position);
		if (site != null) {
			// Populate the holder with the appropriate variables
			holder.siteNameView_.setText(site.getName());

			StringBuffer summaryText = new StringBuffer( );
			if( site.getType() != null ) {
				summaryText.append(site.getType());
			}
			if( site.getCarbonSaving() != 0 ) {
				if( summaryText.length() != 0 ) {
					summaryText.append( ", c" );
				} else {
					summaryText.append( "C");
				}
				summaryText.append( "arbon saving " );
				summaryText.append( site.getCarbonSaving() );
				summaryText.append( "kg CO2"); 
			}
			holder.siteSummary_.setText(summaryText );
			
			// Set the site icon
			Bitmap icon = BitmapFactory.decodeResource(context_.getResources(), R.drawable.site_icon);
			holder.siteIconView_.setImageBitmap(icon);
			
			// Set the arrow button tag to be the site
			holder.siteButton_.setTag(site);
		} else {
			Log.w(TAG, "Site was null for position "+position+" in list");
		}
		return v;
	}

}
