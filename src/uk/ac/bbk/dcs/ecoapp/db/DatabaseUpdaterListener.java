package uk.ac.bbk.dcs.ecoapp.db;

/**
 * This interface receives callbacks from a DatabaseUpdater class when it starts
 * to perform a database update.
 * The DatabaseUpdater will call willDownloadSites with the number of Sites which are to
 * be downloaded. Then it will call willDownloadSite( ) once for each site
 *  
 * @author Dave Durbin muc@ddurbin.org
 *
 */
public interface DatabaseUpdaterListener {
	/**
	 * The DatabaseUpdater is about to download some sites
	 * @param numSites The number of sites to be downloaded
	 */
	public void willDownloadSites( int numSites );

	/**
	 * The DatabaseUpdater is about to download the nth site
	 * @param siteIndex The index of the site to be downloaded
	 */
	public void willDownloadSite( int siteIndex );
}