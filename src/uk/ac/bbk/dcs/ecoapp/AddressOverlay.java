package uk.ac.bbk.dcs.ecoapp;

import java.util.ArrayList;

import uk.ac.bbk.dcs.ecoapp.db.Site;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;

public class AddressOverlay extends ItemizedOverlay<OverlayItem> {

	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	
	private static final int CONTAINER_RADIUS				= 4;
	private static final int CONTAINER_SHADOW_OFFSET		= 1;
	
	private GeoPoint geopoint;
	private Drawable marker;
	private String locationName;
	private String iconLoc;
	private Context appContext;
	
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


	public AddressOverlay(Site site, Drawable setMarker, Context context) {
		super(boundCenterBottom(setMarker));
		this.marker = setMarker;
		Double convertedLongitude = (site.getLongitude()) * 1E6;
		Double convertedLatitude = site.getLatitude() * 1E6;
		iconLoc = site.getIcon();
		locationName =  site.getName();
		appContext = context;
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
		containerPaint.setTextSize(20);
		//canvas.drawText(locationName, locationPoint.x-10,containerY-10, containerPaint);
		canvas.drawBitmap(bitmap, containerX, containerY, null);

	}

	public void addOverlay(OverlayItem overlay) {
	    mOverlays.add(overlay);
	    populate();
	}
	
	public void setGeopoint(GeoPoint geopoint) {
		this.geopoint = geopoint;
	}

	public GeoPoint getGeopoint() {
		return geopoint;
	}

	@Override
	protected OverlayItem createItem(int i) {
		return mOverlays.get(i);
	}

	@Override
	public int size() {
		return mOverlays.size();
	}

	@Override
	protected boolean onTap(int index) {
	  OverlayItem item = mOverlays.get(index);
	  AlertDialog.Builder dialog = new AlertDialog.Builder(appContext);
	  dialog.setTitle(item.getTitle());
	  dialog.setMessage(item.getSnippet());
	  dialog.show();
	  return true;
	}
	
	
//	   @Override
//	    protected boolean onTap(int index) {
//		   Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"));
//		   browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		   appContext.startActivity(browserIntent);
//	       return true;
//	    }	



}
