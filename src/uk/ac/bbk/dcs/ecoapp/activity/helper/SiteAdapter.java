package uk.ac.bbk.dcs.ecoapp.activity.helper;

import java.util.List;

import uk.ac.bbk.dcs.ecoapp.R;
import uk.ac.bbk.dcs.ecoapp.db.Site;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
	Context			context_;
	List<Site>		sites_;

	/**
	 * 
	 * @param context
	 * @param textViewResourceId
	 * @param sites
	 */
	public SiteAdapter(Context context, int textViewResourceId, List<Site> sites) {
		super(context, textViewResourceId, sites);
		sites_ = sites;
		context_ = context;
	}

	public static class ViewHolder {
		public TextView		siteNameView_;
		public TextView		siteDescriptionView_;
		public ImageView	siteIconView_;
		public ImageButton	siteButton_;	
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		ViewHolder holder = null;
		
		// If the view is not allocated then 
		if (v == null) {
			// Create a new view
			LayoutInflater vi = (LayoutInflater)context_.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.list_item, null);

			// And a new holder
			holder = new ViewHolder();
			
			// Set the elements of the holder to be the elements of the newly created View
			holder.siteNameView_= (TextView) v.findViewById(R.id.site_name);
			holder.siteDescriptionView_ = (TextView) v.findViewById(R.id.site_description);
			holder.siteButton_ = (ImageButton) v.findViewById(R.id.arrow_button);
			holder.siteIconView_ = (ImageView) v.findViewById(R.id.site_icon);
			holder.siteDescriptionView_ = (TextView) v.findViewById(R.id.site_description);
			
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
			holder.siteDescriptionView_.setText(site.getDescription());
			
			// Set the site icon
			Bitmap icon = BitmapFactory.decodeResource(context_.getResources(), R.drawable.site_icon);
			holder.siteIconView_.setImageBitmap(icon);
			
			// Set the arrow button tag to be the site name
			holder.siteButton_.setTag(site);
		}
		return v;
	}

}
