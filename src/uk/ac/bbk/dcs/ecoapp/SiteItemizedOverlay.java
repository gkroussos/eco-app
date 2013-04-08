package uk.ac.bbk.dcs.ecoapp;

import java.util.List;

import uk.ac.bbk.dcs.ecoapp.model.Site;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.Projection;

public class SiteItemizedOverlay extends ItemizedOverlay<SiteOverlayItem> {
	
    private static final String CLASSTAG = SiteItemizedOverlay.class.getSimpleName();

	private GeoPoint geopoint;
	private Drawable marker;
	private String locationName;
	private String iconLoc;
	private Context appContext;

    private final List<SiteOverlayItem> items;
    private final Context context;

    public SiteItemizedOverlay(final List<SiteOverlayItem> items, final Drawable defaultMarker, final Context context) {
        super(defaultMarker);
        this.items = items;
        this.context = context;
        
		this.marker = defaultMarker;
//		Double convertedLongitude = (site.getLongitude()) * 1E6;
//		Double convertedLatitude = site.getLatitude() * 1E6;
//		iconLoc = items.get.getIcon();
//		locationName =  site.getName();
//		appContext = context;
//		setGeopoint(new GeoPoint(
//				convertedLatitude.intValue(),
//				convertedLongitude.intValue()));
		Log.v("DEBUG", items.get(0).point.toString());
		setGeopoint(items.get(0).point);
        // after the list is ready, you have to call populate (before draw is automatically invoked)
        populate();
    }

    @Override
    public SiteOverlayItem createItem(final int i) {
        return this.items.get(i);
    }

    @Override
    public boolean onTrackballEvent(MotionEvent e, MapView v) {
        // /Toast.makeText(this.context, "trackballEvent", Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    protected boolean onTap(final int i) {
        //Log.d(Constants.LOGTAG, SiteItemizedOverlay.CLASSTAG + " item with index " + i + " tapped");
        final Site bd = this.items.get(i).siteData;
        //Log.d(Constants.LOGTAG, BuoyItemizedOverlay.CLASSTAG + " selected buoyData - " + bd);

        LayoutInflater inflater = LayoutInflater.from(this.context);
        View bView = inflater.inflate(R.layout.detail_view, null);
        TextView title = (TextView) bView.findViewById(R.id.site_name);
        title.setText(bd.getName());

        TextView atView = (TextView) bView.findViewById(R.id.site_description);
        atView.setText(bd.getDescription());
        TextView wtView = (TextView) bView.findViewById(R.id.site_url);
        wtView.setText(bd.getLink());

        new AlertDialog.Builder(this.context).setView(bView).setPositiveButton("Go to website",
            new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface di, int what) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(bd.getLink()));
                    // quick and dirty hack to set data on another activity
                    // (not really ideal, but don't need a Parcelable here either, and don't want to
                    // pass in Bundle separately, etc)
                    //BuoyDetailActivity.buoyData = bd;
                    context.startActivity(intent);
                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface di, int what) {
                di.dismiss();
            }
        }).show();

        return false;
    }

    @Override
    public int size() {
        return this.items.size();
    }

    @Override
    public void draw(Canvas canvas, MapView mapView, boolean b) {
        super.draw(canvas, mapView, false);
        // example of manual drawing it, here we are letting ItemizedOverlay handle it
        // Bitmap buoy = BitmapFactory.decodeResource(mapView.getResources(), R.drawable.buoy_30);
        // bitmap, x, y, Paint
        // canvas.drawBitmap(buoy, 50, 50, null);  
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
    
	public GeoPoint getGeopoint() {
		return geopoint;
	}
	
	public void setGeopoint(GeoPoint geopoint) {
		this.geopoint = geopoint;
	}
}
