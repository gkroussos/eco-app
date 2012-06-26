package uk.ac.dcs.bbk.ecoapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.Toast;
import uk.ac.dcs.bbk.ecoapp.db.Site;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;

public class AddressOverlay extends ItemizedOverlay<OverlayItem> {

	private static final int CONTAINER_RADIUS				= 4;
	private static final int CONTAINER_SHADOW_OFFSET		= 1;
	
	private GeoPoint geopoint;
	private Drawable marker;
	private String locationName;
	private String iconLoc;
	public AddressOverlay(Site site) {
		super(null);
		Double convertedLongitude = (site.getLongitude()) * 1E6;
		Double convertedLatitude = site.getLatitude() * 1E6;
		setGeopoint(new GeoPoint(
				convertedLatitude.intValue(),
				convertedLongitude.intValue()));
	}
	
	
	public AddressOverlay(Double lat, Double longd) {
		super(null);
		Double convertedLongitude = longd * 1E6;
		Double convertedLatitude = lat * 1E6;
		setGeopoint(new GeoPoint(
				convertedLatitude.intValue(),
				convertedLongitude.intValue()));
		populate();
	}


	public AddressOverlay(Site site, Drawable marker, Context context) {
		super(marker);
		this.marker = marker;
		Double convertedLongitude = (site.getLongitude()) * 1E6;
		Double convertedLatitude = site.getLatitude() * 1E6;
		iconLoc = site.getIcon();
		locationName =  site.getName();
		setGeopoint(new GeoPoint(
				convertedLatitude.intValue(),
				convertedLongitude.intValue()));
		populate();
	}
	
	
	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		super.draw(canvas, mapView, shadow);
		Point locationPoint = new Point();
    	Projection projection = mapView.getProjection();
    	projection.toPixels(getGeopoint(), locationPoint);
    	Paint containerPaint = new Paint();
		containerPaint.setAntiAlias(true);
    	int containerX = locationPoint.x;
    	int containerY = locationPoint.y;
			Bitmap bitmap = ((BitmapDrawable)marker).getBitmap();
			containerPaint.setTextSize(30);
			canvas.drawText(locationName, locationPoint.x,containerY, containerPaint);
			canvas.drawBitmap(bitmap, containerX, containerY, null);

	}
     

	
	public void setGeopoint(GeoPoint geopoint) {
		this.geopoint = geopoint;
	}

	public GeoPoint getGeopoint() {
		return geopoint;
	}

	@Override
	protected OverlayItem createItem(int i) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}




}
