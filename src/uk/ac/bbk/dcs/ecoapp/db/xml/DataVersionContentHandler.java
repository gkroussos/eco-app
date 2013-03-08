package uk.ac.bbk.dcs.ecoapp.db.xml;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * This class is a factory class for handling the Inmidtown database version XML 
 * The structure of a valid XML string is
 * 
 	<ecoapp>
 		<dataversion>0.3</dataversion>
 	</ecoapp>
 *
 * @author Dave
 */
public class DataVersionContentHandler extends DefaultHandler {
	/* Tags for parsing */
	private static final String TAG_DATA_VERSION = "dataversion";

	/** True if we're in a recognised tag, otherwise false */
	private boolean 	inRecognisedElement_;

	/** The Site currently being read. Created at start of <site> element */
	private double		databaseVersion_;


	/**
	 * @return the version of the database as a double
	 */
	public double getVersion( ) {
		return databaseVersion_;
	}

	/*
	 * Implementation of DefaultHandler methods
	 */

	/**
	 * Receive notification of character data inside an element.
	 * If it's an element of interest, we store the String value, otherwise do nothing
	 */
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		// If we're in a tag, capture the characters as the contents of that tag
		if( inRecognisedElement_ ) {
			String versionString = new String( ch, start, length );
			databaseVersion_ = Double.valueOf(versionString);
		}
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
		if( TAG_DATA_VERSION.equals(localName ) ) {
			inRecognisedElement_ = true;
		}
		// Otherwise we're not intrerested
		else {
			inRecognisedElement_ = false;
		}
	}

	/** 
	 * Handle end of element by resetting flag if set 
	 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		// If it's the start of the <sites> tag, create a new list
		if( TAG_DATA_VERSION.equals(localName ) ) {
			inRecognisedElement_ = false;
		}
	}
}
