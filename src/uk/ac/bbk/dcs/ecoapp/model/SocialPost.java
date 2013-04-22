package uk.ac.bbk.dcs.ecoapp.model;

/**
 * Interface to define generic social posts. Implement platform specific implementation. 
 * @author William Linden
 *
 */
public interface SocialPost {
	 
	  public String getCreated_time();
	  public void setCreated_time(String created_time);

	  public String getMessage();
	  public void setMessage(String message);
	
	  public String getPermalink();
	  public void setPermalink(String permalink);
	
	  public String toString(); 
	
}