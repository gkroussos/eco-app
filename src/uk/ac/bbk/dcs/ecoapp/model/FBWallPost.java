package uk.ac.bbk.dcs.ecoapp.model;

import lombok.Getter;
import lombok.Setter;

import com.restfb.Facebook;



/**
 * @author William Linden
 * 
 * Custom class representing a post obtained via FQL
 *
 */
public class FBWallPost {
	
	
	// Holds results from an "executeFqlQuery" call.
	// You need to write this class yourself!
	// Be aware that FQL fields don't always map to Graph API Object fields.
	// SELECT created_time,message,permalink FROM stream WHERE source_id=292361304193814 LIMIT 50

	 
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
