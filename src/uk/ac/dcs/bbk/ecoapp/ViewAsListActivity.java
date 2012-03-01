package uk.ac.dcs.bbk.ecoapp;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import uk.ac.dcs.bbk.ecoapp.db.EcoSQLiteOpenHelper;
import uk.ac.dcs.bbk.ecoapp.db.Site;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.AlertDialog.Builder;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;

public class ViewAsListActivity extends ListActivity {

	private EcoSQLiteOpenHelper eOpenHelper;
	private SQLiteDatabase sqlDB;
	private ArrayList<Site> sitesList;
	private ArrayList<HashMap<String, Object>> listItems;
	private SimpleAdapter listItemAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.viewaslist);

		initListView();
		this.setListAdapter(listItemAdapter);

	}

	private void initListView() {

		sitesList = new ArrayList<Site>();
		listItems = new ArrayList<HashMap<String, Object>>();

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
						sitesList.add(site);
					} while (cursor.moveToNext());
				}
				cursor.close();
			}
			sqlDB.close();
		}

		for (int i = 0; i < sitesList.size(); i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("SiteName", sitesList.get(i).getName());
			String imgUrl = sitesList.get(i).getIcon();
			// imgUrl = "http://www.dcs.bbk.ac.uk/~qhuang01/video_chat.png";
			if (imgUrl.startsWith("http://")) {
				map.put("SiteIcon", getBitmap(imgUrl));
			} else {
				map.put("SiteIcon", R.drawable.default_logo);
			}
			listItems.add(map);
		}

		listItemAdapter = new SimpleAdapter(this, listItems,
				R.layout.list_item, new String[] { "SiteName", "SiteIcon" },
				new int[] { R.id.SiteName, R.id.SiteIcon });

		listItemAdapter.setViewBinder(new ViewBinder() {
			public boolean setViewValue(View view, Object data,
					String textRepresentation) {
				if (view instanceof ImageView && data instanceof Bitmap) {
					ImageView iv = (ImageView) view;
					iv.setImageBitmap((Bitmap) data);
					return true;
				} else
					return false;
			}
		});
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		Site st = sitesList.get(position);

		// add a dialog for the detail
		Builder builder = new AlertDialog.Builder(this);

		builder.setMessage(
				"Type: " + st.getType() + "\n\nDescription: "
						+ st.getDescription() + "\n\nLatitude: "
						+ st.getLatitude() + "\n\nLongitude: "
						+ st.getLongitude()).setCancelable(true);
		AlertDialog alert = builder.create();
		alert.show();
	}

	public Bitmap getBitmap(String imageUrl) {
		Bitmap mBitmap = null;
		try {
			URL url = new URL(imageUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			InputStream is = conn.getInputStream();
			mBitmap = BitmapFactory.decodeStream(is);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return mBitmap;
	}
}