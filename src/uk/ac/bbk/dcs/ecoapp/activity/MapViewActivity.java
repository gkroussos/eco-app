package uk.ac.bbk.dcs.ecoapp.activity;

import java.util.ArrayList;
import java.util.List;

import uk.ac.bbk.dcs.ecoapp.R;
import uk.ac.bbk.dcs.ecoapp.SiteItemizedOverlay;
import uk.ac.bbk.dcs.ecoapp.SiteOverlayItem;
import uk.ac.bbk.dcs.ecoapp.db.EcoDatabaseHelper;
import uk.ac.bbk.dcs.ecoapp.model.Site;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

/**
 * This class is an Activity representing recent Social posts (currently Facebook only) by InMidTown
 * 
 * @author Georgio
 * @author Dave Durbin
 * @author William Linden
 *
 */
public class MapViewActivity extends  MapActivity {
	/** List of known Sites */
	private List<Site> 			siteList_;


	//private List<SiteOverlayItem> siteItems;
	private MapView mapView;	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_view);
		mapView = (MapView) findViewById(R.id.map);  		
		mapView.setBuiltInZoomControls(true);
		initMapView();
		mapSite();
	}

	private void initMapView() {
		EcoDatabaseHelper dbHelper = new EcoDatabaseHelper(this); 
		siteList_ =  dbHelper.getSites();
	}


	protected void mapSite() { 
		ArrayList<SiteOverlayItem> siteItems = new ArrayList<SiteOverlayItem>();
		List<Overlay> mapOverlays = mapView.getOverlays();
		Site siteToGo; // = new Site();

		siteToGo = siteList_.get(0);

		for (Site site : siteList_) {
			Double convertedLatitude = site.getLatitude() * 1E6;
			Double convertedLongitude = site.getLongitude() * 1E6;

			GeoPoint point = new GeoPoint(convertedLatitude.intValue(),convertedLongitude.intValue());

			SiteOverlayItem siteItem = new SiteOverlayItem(point, site);
			siteItems.add(siteItem);
		}

		// TODO: Replace these with lazy loaded Site Images
		// Drawable marker = getResources().getDrawable(R.drawable.flag);
		Drawable marker = getResources().getDrawable(R.drawable.site_icon_transparent);

		marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());
		//AddressOverlay addressOverlay = new AddressOverlay(siteToGo, marker, this);
		SiteItemizedOverlay addressOverlay = new SiteItemizedOverlay(siteItems, marker, this);
		//AddressOverlay addressOverlay = new AddressOverlay(siteToGo);
		mapOverlays.add(addressOverlay);

		mapView.invalidate();
		final MapController mapController = mapView.getController();
		//mapController.animateTo(addressOverlay.getGeopoint(), new Runnable() {
		mapController.animateTo(siteItems.get(0).getPoint(), new Runnable() {
			public void run() {
				mapController.setZoom(17);
			}
		});




	}
	public void onSetHome(View v){
		Intent intent = new Intent(this, ListViewActivity.class);  
		intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT); 
		startActivity(intent);
	}

	public void onSetAboutUs(View v){
		Intent intent = new Intent(this, AboutUsActivity.class);  
		intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT); 
		startActivity(intent);
   
	}
	
	/** 
	 * Go to the Social view
	 * @param v
	 * Just dropping code in. This whole class probably requires GoogleAnalytics added into it
	 */
	public void onSocial(View v){
		Intent intent = new Intent(this, SocialActivity.class);  
		intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT); 
		startActivity(intent);
	}


	public void onSetMap(View v){
		Toast.makeText(this, "Map", Toast.LENGTH_SHORT).show();
	}

	/**
	 * Search Button Click
	 * @deprecated replaced by onRefresh(View)
	 */
	public void onSearch(View v){
		onSearchRequested();
	}
	public void onRefresh(View v){
		// Needs implementation @deprecated replaced by onRefresh(View) 
	}
	
	@Override
	public boolean onSearchRequested() {
		return super.onSearchRequested();
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	
}