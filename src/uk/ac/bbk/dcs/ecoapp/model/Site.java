package uk.ac.bbk.dcs.ecoapp.model;

/**
 * Model for an InMidtown Site.
 * 
 * @author Giorgio
 * @author Dave Durbin
 * Site Object
 */
public class Site {
	/** Unique Site ID */
	private int 	id_;
	
	/** Site name */
	private String 	name_;
	
	/** Type of activity */
	private String type_;
	
	/** Description of the eco activity at that site */
	private String description_;
	
	/** URL link to a website for the Site */
	private String	link_;
	
	/** Location lattitude */
	private double	latitude_;
	
	/** Location longitude */
	private double longitude_;
	
	/** URL pointing to an ico for that Site */
	private String	icon_;
	
	/** Distance of the Site from current location */
	private double	distance_;
	
	/** Carbon savings in last full quarter kg CO_2e */
	private float	carbonSaving_;

	/**
	 * @return the id
	 */
	public int getId() {
		return id_;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		id_ = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name_;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		name_ = name;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description_;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		description_ = description;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type_;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		type_ = type;
	}

	/**
	 * @return the link
	 */
	public String getLink() {
		return link_;
	}

	/**
	 * @param link the link to set
	 */
	public void setLink(String link) {
		link_ = link;
	}

	/**
	 * @return the latitude
	 */
	public double getLatitude() {
		return latitude_;
	}

	/**
	 * @param latitude the latitude to set
	 */
	public void setLatitude(double latitude) {
		latitude_ = latitude;
	}

	/**
	 * @return the longitude
	 */
	public double getLongitude() {
		return longitude_;
	}

	/**
	 * @param longitude the longitude to set
	 */
	public void setLongitude(double longitude) {
		longitude_ = longitude;
	}

	/**
	 * @return the icon
	 */
	public String getIcon() {
		return icon_;
	}

	/**
	 * @param icon the icon to set
	 */
	public void setIcon(String icon) {
		icon_ = icon;
	}

	/**
	 * @return the distance
	 */
	public double getDistance() {
		return distance_;
	}

	/**
	 * @param distance the distance to set
	 */
	public void setDistance(double distance) {
		distance_ = distance;
	}

	/**
	 * @return the carbonSaving
	 */
	public float getCarbonSaving() {
		return carbonSaving_;
	}

	/**
	 * @param carbonSaving the carbonSaving to set
	 */
	public void setCarbonSaving(float carbonSaving) {
		carbonSaving_ = carbonSaving;
	}
}
