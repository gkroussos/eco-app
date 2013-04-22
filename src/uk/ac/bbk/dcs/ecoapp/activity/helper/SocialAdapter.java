package uk.ac.bbk.dcs.ecoapp.activity.helper;

import java.util.List;

import uk.ac.bbk.dcs.ecoapp.R;
import uk.ac.bbk.dcs.ecoapp.model.SocialPost;
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
 * This class is a custom resource adapter for the SicialView of the SocialActivity 
 * It maps social post information into a list_item and tags the arrow button to the right
 * of the list_item with a URL to facilitate Intent initialisation.
 *  
 * @author William Linden
 *
 */
public class SocialAdapter extends ArrayAdapter<SocialPost> {
	Context				context;
	List<SocialPost> 	posts;

	/**
	 * 
	 * @param context
	 * @param textViewResourceId
	 * @param posts
	 */
	public SocialAdapter(Context context, int textViewResourceId, List<SocialPost> posts) {
		super(context, textViewResourceId, posts);
		this.posts = posts;
		this.context = context;
	}

	private static class SocialViewHolder {
		public TextView		socialTypeView;
		public TextView		socialMessageView;
		public ImageView	socialIconView;
		public ImageButton	socialButton;	
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		SocialViewHolder holder = null;
		
		// If the view is not allocated then 
		if (v == null) {
			// Create a new view
			LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.social_list_item, null);

			// And a new holder
			holder = new SocialViewHolder();
			
			// Set the elements of the holder to be the elements of the newly created View
			holder.socialTypeView= (TextView) v.findViewById(R.id.social_type);
			holder.socialMessageView = (TextView) v.findViewById(R.id.social_message);
			holder.socialButton = (ImageButton) v.findViewById(R.id.social_arrow_button);
			holder.socialIconView = (ImageView) v.findViewById(R.id.social_icon);
			holder.socialMessageView = (TextView) v.findViewById(R.id.social_message);
			
			// Set the view's tag to be the holder
			v.setTag(holder);
		} else {
			// Otherwise, just recover the holder from this view
			holder = (SocialViewHolder)v.getTag();
		}

		// Extract the site corresponding to the position in the list
		final SocialPost post = posts.get(position);
		if (post != null) {
			// Populate the holder with the appropriate variables
			holder.socialTypeView.setText(post.getCreated_time());
			holder.socialMessageView.setText(post.getMessage());
			
			// Set the  icon
			Bitmap icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.fb_icon);
			holder.socialIconView.setImageBitmap(icon);
			
			// Set the arrow button tag to be the site name
			holder.socialButton.setTag(post);
		}
		return v;
	}

}
