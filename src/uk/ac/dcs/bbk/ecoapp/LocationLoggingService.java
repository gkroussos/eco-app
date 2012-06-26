package uk.ac.dcs.bbk.ecoapp;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import java.util.Random;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources.NotFoundException;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.sax.Element;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.text.format.DateFormat;
import android.util.Log;

public class LocationLoggingService extends Service {

	private final static float minDistance = 10;
	private final static int CONNECT_TIMEOUT = 3000;
	private boolean networkAvailable = true;
	private boolean isThreadDisabled;
	private boolean isXMLParsed = false;
	private long randomId;
	private LocationManager locationManager;
	private Location location;
	private String provider;
	private String llUrl;
	private String loggingType;
	private int loggingFrequencySecs = 600;
	private int loggingUpdateMins = 60;
	private String loggingURI;
	private Criteria criteria;

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {

		super.onCreate();

		SharedPreferences sp = getSharedPreferences("LocationLogging", Context.MODE_PRIVATE);
		randomId = sp.getLong("RandomId", 0);
		if (randomId == 0) {
			Random random = new Random();
			long randomLong = random.nextLong();
			randomId = Math.abs(randomLong);
			Editor editor = sp.edit();
			editor.putLong("RandomId", randomId);
			editor.commit();
		}

		Properties p = new Properties();
		try {
			p.load(this.getResources().openRawResource(R.raw.data));
			llUrl = p.getProperty("url.locationLogConf");
		} catch (NotFoundException e1) {
			// TODO Auto-generated catch block
			Log.e("Property file not found", e1.getMessage());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			Log.e("Property file IO error", e1.getMessage());
		}

		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

		criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setPowerRequirement(Criteria.NO_REQUIREMENT);

		if (locationManager != null) {
			provider = locationManager.getBestProvider(criteria, true);
		}
		if (provider != null) {
			locationManager.requestLocationUpdates(provider, loggingFrequencySecs * 1000, minDistance, locationListener);
		}

		new Thread(new Runnable() {
			@Override
			public void run() {
				while (!isThreadDisabled) {
					networkAvailable = checkNetworkAvailability(LocationLoggingService.this);
					if (networkAvailable) {
						xmlParser(llUrl);
						isXMLParsed = true;
					}
					try {
						Thread.sleep(loggingUpdateMins * 60 * 1000);
						Log.d("LocationLoggingConf Sleep", loggingUpdateMins + " mins");
					} catch (InterruptedException e) {
						Log.e("LocationLoggingConf", e.getMessage());
					}
				}
			}
		}).start();

		new Thread(new Runnable() {
			@Override
			public void run() {
				while (!isThreadDisabled) {
					networkAvailable = checkNetworkAvailability(LocationLoggingService.this);
					if (networkAvailable && isXMLParsed && locationManager != null && provider != null && locationManager.isProviderEnabled(provider)) {
						location = locationManager.getLastKnownLocation(provider);
						updateLocation(location, provider);
					}
					try {
						Thread.sleep(loggingFrequencySecs * 1000);
						Log.d("LocationLogging Sleep", loggingFrequencySecs + " secs");
					} catch (InterruptedException e) {
						Log.e("LocationLoggingUp", e.getMessage());
					}
				}
			}
		}).start();

	}

	private final LocationListener locationListener = new LocationListener() {

		@Override
		public void onProviderDisabled(String provider) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onLocationChanged(Location loc) {
			if (loc != null) {
				location = loc;
			}
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

	};

	/**
	 * parse XML file
	 * 
	 * @param urlString
	 *            - URLs of the XML files
	 */
	private void xmlParser(String urlString) {
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			Log.i("SAXParser1", "Start parsing");
			SAXParser parser = factory.newSAXParser();
			XMLReader xmlreader = parser.getXMLReader();
			URL url = new URL(urlString);
			URLConnection con = url.openConnection();
			con.setConnectTimeout(CONNECT_TIMEOUT);
			InputStream in = con.getInputStream();
			InputSource is = new InputSource(in);
			xmlreader.setContentHandler(getDataRootElement().getContentHandler());
			xmlreader.parse(is);
			Log.i("SAXParser1", "End parsing");
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			Log.e("ParserConfigurationException", e.getMessage());
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			Log.e("SAXException", e.getMessage());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			Log.e("MalformedURLException", e.getMessage());
		} catch (IOException e) {// cannot access the Internet
			// TODO Auto-generated catch block
			Log.e("XML file is not accessable", e.getMessage());
		}
	}

	/**
	 * data XML parser, fetch data from XML
	 * 
	 * @return a root XML element
	 */
	private RootElement getDataRootElement() {

		RootElement rootElement = new RootElement("logging");

		Element clientElement = rootElement.getChild("client");

		Element typeElement = clientElement.getChild("type");
		typeElement.setEndTextElementListener(new EndTextElementListener() {
			@Override
			public void end(String body) {
				loggingType = body;
			}
		});

		Element frequency_secsElement = clientElement.getChild("frequency_secs");
		frequency_secsElement.setEndTextElementListener(new EndTextElementListener() {
			@Override
			public void end(String body) {
				loggingFrequencySecs = Integer.parseInt(body);
				Log.i("frequency_secs", "" + loggingFrequencySecs);
			}
		});

		Element update_minsElement = clientElement.getChild("update_mins");
		update_minsElement.setEndTextElementListener(new EndTextElementListener() {
			@Override
			public void end(String body) {
				loggingUpdateMins = Integer.parseInt(body);
				Log.i("update_mins", "" + loggingUpdateMins);
			}
		});

		Element URIElement = clientElement.getChild("URI");
		URIElement.setEndTextElementListener(new EndTextElementListener() {
			@Override
			public void end(String body) {
				loggingURI = body;
				Log.i("URI", loggingURI);
			}
		});

		return rootElement;

	}

	private void updateLocation(Location location, String lprovider) {
		String jString;
		if (location != null) {

			String currentTime = DateFormat.format("yyyyMMddkkmmss", new Date()).toString();
			double lat = location.getLatitude();
			double lng = location.getLongitude();
			ArrayList<Double> loc = new ArrayList<Double>();
			loc.add(lat);
			loc.add(lng);

			JSONObject jObject = new JSONObject();

			try {
				jObject.put("loc", loc);
				jObject.put("src", lprovider);
				jObject.put("time", currentTime);
				jObject.put("ID", "" + randomId);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				Log.e("jsonError", e.getMessage());
			}

			jString = jObject.toString();

			HttpPost httpost = new HttpPost(loggingURI);
			HttpParams httpParameters = new BasicHttpParams();
			int timeoutConnection = 3000;
			try {
				HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
				HttpConnectionParams.setSoTimeout(httpParameters, timeoutConnection);
				DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);

				httpost.setEntity(new StringEntity(jString));
				httpost.setHeader("Accept", "application/json");
				httpost.setHeader("Content-Type", "application/json");
				BasicHttpResponse response = (BasicHttpResponse) httpClient.execute(httpost);
				Log.i("updateLocation", "response: " + response.getStatusLine().getStatusCode());
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				Log.e("UnsupportedEncodingException", e.getMessage());
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				Log.e("ClientProtocolException", e.getMessage());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Log.e("updateLocation IOException", e.getMessage());
			}

		} else {
			jString = "no location, didn't post";
		}

		Log.i("updateLocation", "post: " + jString + "; url: " + loggingURI);

	}

	/**
	 * check network availability
	 * 
	 * @param service
	 *            - current activity
	 * @return boolean - true: network available; false: no network
	 */
	public boolean checkNetworkAvailability(Service service) {

		ConnectivityManager cManager = (ConnectivityManager) service.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo nInfo = cManager.getActiveNetworkInfo();
		if (nInfo == null || !nInfo.isConnected()) {
			return false;
		} else {
			return true;
		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		this.isThreadDisabled = true;
	}
}
