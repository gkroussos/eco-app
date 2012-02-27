/**
 * 
 */
package uk.ac.dcs.bbk.ecoapp.db;

/**
 * @author Giorgio
 * 
 */
public class Location {
	private int id;
	private String name;
	private String coordinate;

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

	public String getCoordinate() {
		return coordinate;
	}

	public void setCoordinate(String coordinate) {
		this.coordinate = coordinate;
	}

}
