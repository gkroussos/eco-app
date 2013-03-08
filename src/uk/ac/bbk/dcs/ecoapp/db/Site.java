package uk.ac.bbk.dcs.ecoapp.db;

/**
 * @author Giorgio
 * Site Object
 */
public class Site {
	private int id;
	private String name;
	private String description;
	private String type;
	private String link;
	private double latitude;
	private double longitude;
	private String icon;
	private double distance;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}
	
	public double getDistance() {
		// DD: Corrected to return distance rather than Latitude
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}
}
