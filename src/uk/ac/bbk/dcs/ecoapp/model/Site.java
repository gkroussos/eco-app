package uk.ac.bbk.dcs.ecoapp.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Model for an InMidtown Site.
 * 
 * @author Giorgio
 * @author Dave Durbin
 * @author William Linden
 * Site Object
 */
public class Site {
	/** Unique Site ID */
	@Getter @Setter
	private int 	id;
	
	/** Site name */
	@Getter @Setter
	private String 	name;
	
	/** Type of activity */
	@Getter @Setter
	private String type;
	
	/** Description of the eco activity at that site */
	@Getter @Setter
	private String description;
	
	/** URL link to a website for the Site */
	@Getter @Setter
	private String	link;
	
	/** Location lattitude */
	@Getter @Setter
	private double	latitude;
	
	/** Location longitude */
	@Getter @Setter
	private double longitude;
	
	/** URL pointing to an icon for that Site */
	@Getter @Setter
	private String	icon;
	
	/** Distance of the Site from current location */
	@Getter @Setter
	private double	distance;
	
	/** Carbon savings in last full quarter kg CO2e */
	@Getter @Setter
	private long	carbonSaving;
	
	/** Facebook Open Graph node ID */
	@Getter @Setter
	private String	facebookNodeId;
}
