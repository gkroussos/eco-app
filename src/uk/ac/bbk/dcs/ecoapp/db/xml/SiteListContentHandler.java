package uk.ac.bbk.dcs.ecoapp.db.xml;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import uk.ac.bbk.dcs.ecoapp.model.Site;


/**
 * This class is a factory class for constructing InMidtown SiteLists. It provides static methods for 
 * parsing XML and constructing a list of Sites. 
 * The structure of a valid XML string is
 * 
 * <sites>
	<site>
    	<name>inmidtown</name>
        <description>inmidtown office</description>
        <type>BEE</type>
        <link>http://inmidtown.org/</link>
        <location>51.51765,-0.11985</location>
        <icon>http://www.dcs.bbk.ac.uk/~gr/inmidtown/icon/1.png</icon>
    </site>
   </sites>
  *
 * @author Dave
 *
 */
public class SiteListContentHandler extends  DefaultHandler {
	/* Tags for parsing */
	private static final String TAG_SITES = "sites";
	private static final String TAG_SITE = "site";
	private static final String TAG_NAME = "name";
	private static final String TAG_DESCRIPTION = "description";
	private static final String TAG_TYPE = "type";
	private static final String TAG_LINK = "link";
	private static final String TAG_LOCATION = "location";
	private static final String TAG_ICON = "icon";
	private static final String TAG_CARBON_SAVING = "carbonSaving";
	private static final List<String> RECOGNISED_ELEMENTS = Arrays.asList(TAG_NAME, TAG_DESCRIPTION, TAG_TYPE, TAG_LINK, TAG_LOCATION, TAG_ICON, TAG_CARBON_SAVING);
	
	/** True if we're in a recognised tag, otherwise false */
	private boolean 	inRecognisedElement_;
	
	/** String value of the content of the last tag parsed */
	private  StringBuffer 	elementContent_ = new StringBuffer( );
	
	/** The Site currently being read. Created at start of <site> element */
	private Site		currentSite_;
	
	/** List of sites being produced. Creates at start of <sites> element */
	private List<Site>	siteList_;
	
	
	/**
	 * Returns true if the provided String argument is the name of an element which this Handler is interested
	 * in capturing the value of.
	 * @param elementName The name of the element 
	 * @return true if the element is of interest, otherwise false
	 */
	private boolean isRecognisedElement( String elementName ) {
		return ( RECOGNISED_ELEMENTS.contains(elementName));
	}
	
	/**
	 * Set the latitude and longitude of the currentSite from a String containing both separated by a comma
	 * @param locationString String with format lat,long
	 */
	private void setCurrentSiteLocationFromString( String locationString ) {
		int commaIndex = locationString.indexOf(',');
		// If the comma splits the string into two strings of at least one character length each
		if( commaIndex > 0 && commaIndex < locationString.length() - 1) {
			
			// Get latitude from string
			String latString = locationString.substring(0, commaIndex);
			double latitude = Double.valueOf(latString);
			currentSite_.setLatitude(latitude);
			
			// And longitude
			String longString = locationString.substring(commaIndex + 1 );
			double longitude = Double.valueOf(longString);
			currentSite_.setLongitude(longitude);
		}
	}
	
	/**
	 * @return the list of Sites
	 */
	public List<Site> getSiteList( ) {
		return siteList_;
	}
	
	
	/*
	 * Implementation of DefaultHandler methods
	 */
	
	/**
	 * Receive notification of character data inside an element.
	 * If it's an element of interest, we store the String value, otherwise do nothing
	 * NOTE: We must APPEND the text here to any existing elementContent as this method may be called 
	 * a number of times for each character text:
	 * <em>"The Parser will call this method to report each chunk of character data. SAX parsers may return all 
	 * contiguous character data in a single chunk, or they may split it into several chunks ...".</em>
	 */
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		// If we're in a tag, capture the characters as the contents of that tag
		if( inRecognisedElement_ ) {
			elementContent_.append(new String( ch, start, length ));
		}
	}

	/**
	 * Handle the end of an element. 
	 * If we're at the end of a <site> element, add the currentSite object to the list
	 * and null it.
	 * Otherwise, if we're in a recognised element name, take the current element content and, 
	 * based on the element we're in, set the appropriate field in the current Site
	 * Clear the in element flag if set
	 * Otherwise, do nothing 
	 */
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		// If this is the end of a <site> element, close the Site and add to the list
		if( TAG_SITE.equals( localName ) ) {
			siteList_.add(currentSite_);
			currentSite_ = null;
		}
		// Otherwise, if it's an element we're interested in...
		else if ( inRecognisedElement_) {
			// Set the appropriate field based on the element name
			if( TAG_NAME.equals( localName) ) {
				currentSite_.setName(elementContent_.toString());
			} else if ( TAG_DESCRIPTION.equals(localName) ) {
				currentSite_.setDescription(elementContent_.toString());
			} else if ( TAG_LOCATION.equals(localName) ) {
				// Parse latitude and longitude from Location String
				setCurrentSiteLocationFromString( elementContent_ .toString());
			} else if ( TAG_TYPE.equals(localName) ) {
				currentSite_.setType(elementContent_.toString());
			} else if ( TAG_LINK.equals(localName) ) {
				currentSite_.setLink(elementContent_.toString());
			} else if ( TAG_ICON.equals(localName) ) {
				currentSite_.setIcon(elementContent_.toString());
			} else if ( TAG_CARBON_SAVING.equals(localName) ) {
				currentSite_.setCarbonSaving(  Long.valueOf(elementContent_.toString()).longValue());
			}
			
			// Clear the flag
			inRecognisedElement_ = false;
		}
		// Otherwise, do nothing
	}

	/**
	 * Handle start of element. If this is an element of interest, 
	 * we set the flag to say so.
	 * If it's the start of a new <site> we create a new Site object
	 * If it's a <sites> element, we create a new List
	 * Otherwise, we clear the recognised element flag
	 */
	@Override
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
		// If it's the start of the <sites> tag, create a new list
		if( TAG_SITES.equals(localName ) ) {
			siteList_ = new ArrayList<Site>( );
		}
		// Otherwise if it's a <site> element, create a new Site
		else if( TAG_SITE.equals(localName ) ) {
			currentSite_ = new Site( );
		}
		// Otherwise, if it's one of our recognised elements, set the flag
		else if( isRecognisedElement( localName)) {
			inRecognisedElement_ = true;
			// Clear content read for read
			elementContent_.setLength(0);
		}
		// Otherwise we're not intrerested
		else {
			inRecognisedElement_ = false;
		}
	}
}
