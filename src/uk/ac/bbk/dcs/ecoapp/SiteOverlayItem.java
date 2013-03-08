package uk.ac.bbk.dcs.ecoapp;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

import uk.ac.bbk.dcs.ecoapp.db.Site;

public class SiteOverlayItem extends OverlayItem {
    public final GeoPoint point;
    public final Site siteData;

    public SiteOverlayItem(final GeoPoint point, final Site siteData) {
        super(point, siteData.getName(), siteData.getLink());
        this.point = point;
        this.siteData = siteData;
    }
}
