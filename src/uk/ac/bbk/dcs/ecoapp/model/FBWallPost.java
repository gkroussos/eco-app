package uk.ac.bbk.dcs.ecoapp.model;


import lombok.Getter;
import lombok.Setter;

import com.restfb.Facebook;



/**
 * @author William Linden
 * 
 * Custom class representing a facebook post obtained via FQL
 * Should extract interface and allow for specific implementation (Interface SocialPost / impl FBWallPost / impl TwitterPost
 *
 */
public class FBWallPost implements SocialPost {
		 
	  @Facebook @Getter @Setter
	  String created_time;
	  
	  @Facebook @Getter @Setter
	  String message;
	
	  @Facebook @Getter @Setter
	  String permalink;
	
	  @Override
	  public String toString() {
	    return String.format("%s (%s) [%s]", created_time, message, permalink);
	  }
	

}
