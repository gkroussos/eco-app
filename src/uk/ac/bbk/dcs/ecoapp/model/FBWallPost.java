package uk.ac.bbk.dcs.ecoapp.model;


import java.util.Date;

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
		 
	  @Facebook @Setter
	  String created_time;
		
	  // FQL time stamps require "decoding"
	  @Override
		public String getCreated_time() {
		  try {
			Long timeLong = Long.parseLong(created_time);
			return new Date(timeLong * 1000).toString();
		  } catch (NumberFormatException e) { /*Should really do some logging when the date is bad, but be tolerant for now*/ }
		  return "";
			
		}
	  
	  @Facebook @Getter @Setter
	  String message;
	
	  @Facebook @Getter @Setter
	  String permalink;
	
	  @Override
	  public String toString() {
	    return String.format("%s (%s) [%s]", created_time, message, permalink);
	  }


	

}
