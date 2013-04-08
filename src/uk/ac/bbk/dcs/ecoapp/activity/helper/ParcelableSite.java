package uk.ac.bbk.dcs.ecoapp.activity.helper;

import uk.ac.bbk.dcs.ecoapp.model.Site;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * A ParcelableSite is a Parcelable wrapper for a Site object which enables it to be
 * passed as part of an Intent. This allows the ListView and MapView to pass Sites
 * to the DetailView for detail rendering
 * 
 * @author Dave
 */
public class ParcelableSite implements Parcelable {
	/** The name of the site */
	private String 	name_;

	/** The description of the Site */ 
	private String description_;

	/** The type of Site */
	private String type_;

	/** Link to a URL for the Site */
	private String link_;

	/** Link to a URL for he Site's icon */
	private String icon_;

	/**
	 * Convenience constructor will copy those fields required
	 * froma Site object. in particular it won't copy ID, or location based fields
	 * @pram site The Site from which to construct this ParcelableSite
	 */
	public ParcelableSite( Site site ) {
		name_ = site.getName();
		description_ = site.getDescription();
		type_ = site.getType();
		link_ = site.getLink();
		icon_ = site.getIcon();
	}
	
	/**
	 * ParcelableSite can be constructed from a Parcel. The ctor is private
	 * to prevent it being called from anything other than the creator defined below.
	 * Note the read order of the fields is important and must be identical to the
	 * write order defined below
	 * 
	 * @param in The Parcel from which to load the ParcelableSite
	 */
	private ParcelableSite(Parcel in) {
		name_ = in.readString();
		description_ = in.readString( );
		type_ = in.readString( );
		link_ = in.readString( );
		icon_ = in.readString( );
	}

	/**
	 * Required but since this is not a type known to android, just return 0
	 */
	public int describeContents() {
		return 0;
	}

	/**
	 * Write the data to the Parcel provided. Fields must be written in the same 
	 * order that we read them.
	 */
	public void writeToParcel(Parcel out, int flags) {
		out.writeString(name_);
		out.writeString( description_);
		out.writeString(type_);
		out.writeString(link_);
		out.writeString(icon_);
	}

	/**
	 * From docs, All Parcelables must have a CREATOR that implements these two methods
	 */
	public static final Parcelable.Creator<ParcelableSite> CREATOR = new Parcelable.Creator<ParcelableSite>() {
		/**
		 * Create a single instance from a Parcel
		 */
		public ParcelableSite createFromParcel(Parcel in) {
			return new ParcelableSite(in);
		}

		/**
		 * Create an array of instances
		 */
		public ParcelableSite[] newArray(int size) {
			return new ParcelableSite[size];
		}
	};

	/**
	 * @return the name
	 */
	public String getName() {
		return name_;
	}


	/**
	 * @return the description
	 */
	public String getDescription() {
		return description_;
	}


	/**
	 * @return the type
	 */
	public String getType() {
		return type_;
	}


	/**
	 * @return the link
	 */
	public String getLink() {
		return link_;
	}


	/**
	 * @return the icon
	 */
	public String getIcon() {
		return icon_;
	}
}
