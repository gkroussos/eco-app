package uk.ac.dcs.bbk.ecoapp;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.sax.Element;
import android.sax.RootElement;
import android.util.Log;
import org.xml.sax.Attributes;

import uk.ac.dcs.bbk.ecoapp.db.EcoSQLiteOpenHelper;
import uk.ac.dcs.bbk.ecoapp.db.Location;

import android.sax.EndElementListener;
import android.sax.EndTextElementListener;
import android.sax.StartElementListener;
import android.widget.ProgressBar;
import android.widget.TextView;

public class EcoAppActivity extends Activity {

	private ArrayList<Location> locList = new ArrayList<Location>();
	private EcoSQLiteOpenHelper eOpenHelper;
	private SQLiteDatabase sqlDB;
	private ProgressBar updateDbBar;
	private String pubDateString = null;
	private Location location;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ecoapp);

		// the loading text
		TextView textView = (TextView) this.findViewById(R.id.textView);
		textView.setText("loading");

		// the progress bar
		updateDbBar = (ProgressBar) this.findViewById(R.id.updateDbBar);
		updateDbBar.setIndeterminate(true);

		try {

			SAXParserFactory factory = SAXParserFactory.newInstance();
			Log.i("SAXParser", "Start parsing");
			SAXParser parser = factory.newSAXParser();
			XMLReader xmlreader = parser.getXMLReader();
			URL url = new URL(
					"http://www.dcs.bbk.ac.uk/~qhuang01/locations.xml");
			InputSource is = new InputSource(url.openStream());
			xmlreader.setContentHandler(getRootElement().getContentHandler());
			xmlreader.parse(is);
			Log.i("SAXParser", "End parsing");
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {// cannot access the Internet, go to list view directly
			// TODO Auto-generated catch block
			Log.e("No network", e.getMessage());
			Intent intent = new Intent(EcoAppActivity.this,
					ViewAsListActivity.class);
			startActivity(intent);
			EcoAppActivity.this.finish();
		}

		try {
			eOpenHelper = new EcoSQLiteOpenHelper(this);
			sqlDB = eOpenHelper.getWritableDatabase();
			SimpleDateFormat formatter = new SimpleDateFormat(
					"HH:mm:ss dd/MM/yyyy");

			String localDateString = null;
			if (sqlDB.isOpen()) {
				// fetch the local pubdate
				Cursor cursor = sqlDB.query(
						EcoSQLiteOpenHelper.TABLE_LOCPUBDATE,
						new String[] { EcoSQLiteOpenHelper.PDATE }, null, null,
						null, null, EcoSQLiteOpenHelper.PDATE + " desc");
				if (cursor != null) {
					cursor.moveToFirst();
					if (!cursor.isAfterLast()) {
						do {
							localDateString = cursor.getString(0);
						} while (cursor.moveToNext());

					}
					cursor.close();
				}
			}

			Date localDate = null;
			if (localDateString != null) {
				localDate = formatter.parse(localDateString);
				Log.i("LocalDate", localDateString);
			}

			Date pubDate = null;
			if (pubDateString != null) {
				pubDate = formatter.parse(pubDateString);
				Log.i("pubDate", pubDateString);
			}

			// there are some new data fetched from the server
			if (pubDate != null
					&& ((localDate == null || localDate.before(pubDate)) && locList
							.size() > 0)) {

				updateDbBar.setIndeterminate(false);

				updateDbBar.setMax(locList.size());

				int rsPro = 0;

				eOpenHelper.onCreate(sqlDB);
				for (int i = 0; i < locList.size(); i++) {

					Location loc = locList.get(i);
					ContentValues cv = new ContentValues();
					cv.put(EcoSQLiteOpenHelper.LOC_ID, loc.getId());
					cv.put(EcoSQLiteOpenHelper.LOC_NAME, loc.getName());
					cv.put(EcoSQLiteOpenHelper.LOC_COORDINATE,
							loc.getCoordinate());
					long rs = sqlDB.insert(EcoSQLiteOpenHelper.TABLE_LOCATIONS,
							null, cv);

					if (rs != -1) {
						updateDbBar.setProgress(i);
						rsPro++;
					}

				}

				// all new data has been inserted into tables
				if (rsPro == locList.size()) {
					// update the local pubdate
					ContentValues dcv = new ContentValues();
					dcv.put(EcoSQLiteOpenHelper.PDATE, pubDateString);
					long rs1 = sqlDB.insert(
							EcoSQLiteOpenHelper.TABLE_LOCPUBDATE, null, dcv);
					// insert successfully
					if (rs1 != -1) {
						// go to the list view
						Intent intent = new Intent(EcoAppActivity.this,
								ViewAsListActivity.class);
						startActivity(intent);
						EcoAppActivity.this.finish();
					}
				}

			} else {// no new data, go to the list view directly
				Intent intent = new Intent(EcoAppActivity.this,
						ViewAsListActivity.class);
				startActivity(intent);
				EcoAppActivity.this.finish();
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (sqlDB != null && sqlDB.isOpen()) {
				sqlDB.close();
			}
		}

	}

	/**
	 * XML parser, fetch data from XML
	 * 
	 * @return the root XML element
	 */
	private RootElement getRootElement() {

		RootElement rootElement = new RootElement("locations");
		Element pdElement = rootElement.getChild("pubdate");
		pdElement.setEndTextElementListener(new EndTextElementListener() {
			@Override
			public void end(String body) {
				pubDateString = body;
			}
		});

		Element locElement = rootElement.getChild("location");
		locElement.setStartElementListener(new StartElementListener() {
			@Override
			public void start(Attributes attributes) {
				// Log.i("locElement", "start");
				location = new Location();
			}
		});
		locElement.setEndElementListener(new EndElementListener() {
			@Override
			public void end() {
				locList.add(location);
			}
		});

		Element idElement = locElement.getChild("id");
		idElement.setEndTextElementListener(new EndTextElementListener() {
			@Override
			public void end(String body) {
				location.setId(Integer.parseInt(body));
			}
		});

		Element nameElement = locElement.getChild("name");
		nameElement.setEndTextElementListener(new EndTextElementListener() {
			@Override
			public void end(String body) {
				location.setName(body);
			}
		});

		Element coorElement = locElement.getChild("coordinate");
		coorElement.setEndTextElementListener(new EndTextElementListener() {
			@Override
			public void end(String body) {
				location.setCoordinate(body);
			}
		});
		return rootElement;

	}

}