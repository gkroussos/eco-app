package uk.ac.dcs.bbk.ecoapp;

import java.util.ArrayList;
import java.util.List;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import android.graphics.drawable.Drawable;
import uk.ac.dcs.bbk.ecoapp.db.EcoSQLiteOpenHelper;
import uk.ac.dcs.bbk.ecoapp.db.Site;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;



public class ViewAsMapActivity extends  MapActivity {
    /** Called when the activity is first created. */
	private EcoSQLiteOpenHelper eOpenHelper;
	private SQLiteDatabase sqlDB;
	private ArrayList<Site> siteList;
	private MapView mapView;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);
		mapView = (MapView) findViewById(R.id.map);  		
		mapView.setBuiltInZoomControls(true);
	    initMapView();
	    mapSite();
    }

	private void initMapView() {
		// TODO Auto-generated method stub

		siteList = new ArrayList<Site>();
		eOpenHelper = new EcoSQLiteOpenHelper(this);
		sqlDB = eOpenHelper.getWritableDatabase();

		if (sqlDB.isOpen()) {
			// query the sites data
			Cursor cursor = sqlDB.query(EcoSQLiteOpenHelper.TABLE_SITES,
					new String[] { EcoSQLiteOpenHelper.SITE_NAME,
							EcoSQLiteOpenHelper.SITE_ICON,
							EcoSQLiteOpenHelper.SITE_DESCRIPTION,
							EcoSQLiteOpenHelper.SITE_TYPE,
							EcoSQLiteOpenHelper.SITE_LATITUDE,
							EcoSQLiteOpenHelper.SITE_LONGITUDE }, null, null,
					null, null, null);
			if (cursor != null) {
				cursor.moveToFirst();
				if (!cursor.isAfterLast()) {
					do {
						Site site = new Site();
						site.setName(cursor.getString(0));
						site.setIcon(cursor.getString(1));
						site.setDescription(cursor.getString(2));
						site.setType(cursor.getString(3));
						site.setLatitude(cursor.getDouble(4));
						site.setLongitude(cursor.getDouble(5));
						siteList.add(site);
						//test
					} while (cursor.moveToNext());
				}
				cursor.close();
			}
			sqlDB.close();
		}
       
		
	}
	
	
	protected void mapSite() { 
	List<Overlay> mapOverlays = mapView.getOverlays();
		Site siteToGo = new Site();
		siteToGo = siteList.get(0);
		Drawable marker = this.getResources().getDrawable(R.drawable.flag);
		marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());
		AddressOverlay addressOverlay = new AddressOverlay(siteToGo, marker, this);
		//AddressOverlay addressOverlay = new AddressOverlay(siteToGo);
		mapOverlays.add(addressOverlay);
     
		mapView.invalidate();
		final MapController mapController = mapView.getController();
		mapController.animateTo(addressOverlay.getGeopoint(), new Runnable() {
		public void run() {
		mapController.setZoom(17);
		}
		});


		

}
	public void onSetHome(View v){
		// Do something when the button is clicked
	    Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show();
	    startActivity(new Intent(ViewAsMapActivity.this, ViewAsListActivity.class));
	    //set button image
	    //v.setBackgroundDrawable(getResources().getDrawable(R.drawable.highlightednodrop));
	    
	}

	public void onSetAboutUs(View v){
		startActivity(new Intent(ViewAsMapActivity.this, ViewAboutUsActivity.class));    
	}

	public void onSetMap(View v){
	    Toast.makeText(this, "Map", Toast.LENGTH_SHORT).show();
	}
	
	// Search Button Click
	public void onSearch(View v){
		onSearchRequested();
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