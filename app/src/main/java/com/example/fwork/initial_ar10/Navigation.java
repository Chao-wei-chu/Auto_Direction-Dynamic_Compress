package com.example.fwork.initial_ar10;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressLint("NewApi")
public class Navigation extends Activity
{
	final double EARTH_RADIUS = 6378137.0;
	private GoogleMap map;
	static public ArrayList<LatLng> latlngpath = null;  //儲存所有導航路徑
	static public ArrayList<LatLng> gps_array = null;  //儲存所有路徑GPS
	static public ArrayList<String> html_str_array = null;  //儲存所有路徑名稱
	static public ArrayList<String> dis_str_array = null;  //儲存所有路徑距離
	private float lengthSum = 0;  //所有路徑長度
	private float Angle = 0;  //算點與點之角度
	ArrayList<LatLng> markerPoints;

	private Button Search_btn;
	private Button Analysis_btn;
	private Button Navigation_bth;
	private EditText Search_text;

	static LatLng Start_point ;
	static LatLng End_point ;

	private ArrayAdapter<String> NewWayData;
	private ListView listView;

	@SuppressLint("NewApi")
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.navigation);

		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map3)).getMap();
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(MainActivity.init_point, 16));

		Search_btn = (Button) findViewById(R.id.button1);
		Analysis_btn = (Button) findViewById(R.id.button2);
		Navigation_bth = (Button) findViewById(R.id.button3);
		Search_text = (EditText) findViewById(R.id.editText1);

		// Initializing
		markerPoints = new ArrayList<LatLng>();
		Start_point = MainActivity.init_point;

		Search_btn.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				Geocoder geocoder = new Geocoder(Navigation.this);
				List<Address> gotAddresses = null;
				map.clear();  //清地圖

				try
				{
					gotAddresses = geocoder.getFromLocationName(Search_text.getText().toString(), 1);
					Address address = (Address) gotAddresses.get(0);
					LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
					String properAddress = String.format("%s %s",address.getCountryName() , address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "");

					End_point = latLng;  //將終點設為搜尋之點

					Toast.makeText(getApplicationContext(),"已找到" + Search_text.getText().toString(), Toast.LENGTH_SHORT).show();

					map.addMarker(new MarkerOptions()
							.position(new LatLng(address.getLatitude(), address.getLongitude())).draggable(true)
							.title(properAddress)
							.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

					map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(address.getLatitude(), address.getLongitude()), 16));
				}
				catch (IOException e)
				{
					Toast.makeText(getApplicationContext(),"找不到", Toast.LENGTH_SHORT).show();
					Search_text.setText("");;
				}

			}
		});//Search_btn

		Analysis_btn.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				maintainNavigation();
			}
		});//Analysis_btn

		Navigation_bth.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				//傳送經緯度
				MainActivity.End_Position = End_point;
				//設定可以導航旗標
				MainActivity.direct_flag=true;
				//關閉畫面
				Navigation.this.finish();
			}
		});//Navigation_bth

		map.setOnMapClickListener(new OnMapClickListener()   //點擊map產生一個點，測試解析用
		{
			@Override
			public void onMapClick(LatLng latLng)
			{
				//點擊map後會觸發一個座標點，Android會以上方自訂變數latLng將座標傳進來
				MarkerOptions markerOptions = new MarkerOptions();  // Creating a marker
				markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
				markerOptions.position(latLng);  // Setting the position for the marker
				markerOptions.title(latLng.latitude + " : " + latLng.longitude);  // Setting the title for the marker. This will be displayed on taping the marker

				End_point = latLng;  //將終點設為搜尋之點

				maintainNavigation();
				map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latLng.latitude, latLng.longitude), 16));

				map.addMarker(markerOptions);  // Placing a marker on the touched position

			}
		}); //map

		//更新畫面右邊的解析表
		NewWayData = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
		listView = (ListView) findViewById(R.id.listview_data);
		listView.setAdapter(NewWayData);
	}

	//JSON_1
	private void maintainNavigation()
	{
		map.clear();  //先清地圖

		/**JSON導航路徑**/
		// Already two locations
		if (markerPoints.size() > 1) {
			markerPoints.clear();
			map.clear();
		}
		// Adding new item to the ArrayList
		markerPoints.add(Start_point);
		markerPoints.add(End_point);

		//調整地圖zoom
		//map.moveCamera(CameraUpdateFactory.newLatLngZoom(Start_point, 16));

		// Creating MarkerOptions
		MarkerOptions options_start = new MarkerOptions();
		MarkerOptions options_end = new MarkerOptions();

		// Setting the position of the marker
		options_start.position(Start_point);
		options_end.position(End_point);
		options_end.title("終點");
		/**
		 * 起始及終點位置符號顏色
		 **/

		options_start.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)); //起點符號顏色
		options_end.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)); //終點符號顏色


		// Add new marker to the Google Map Android API V2
		//map.addMarker(options_start);  //起點marker
		//map.addMarker(options_end);  //終點marker
		// Checks, whether start and end locations are captured
		if (markerPoints.size() >= 2) {
			LatLng origin = markerPoints.get(0);
			LatLng dest = markerPoints.get(1);

			latlngpath = new ArrayList<LatLng>();
			html_str_array = new ArrayList<String>();
			dis_str_array = new ArrayList<String>();
			gps_array = new ArrayList<LatLng>();
			// Getting URL to the Google Directions API
			String url = getDirectionsUrl(origin, dest);

			DownloadTask downloadTask = new DownloadTask();

			// Start downloading json data from Google Directions
			// API
			downloadTask.execute(url);
		}
	}

	//JSON_2
	private String getDirectionsUrl(LatLng origin, LatLng dest)
	{

		// Origin of route
		String str_origin = "origin=" + origin.latitude + ","
				+ origin.longitude;

		// Destination of route
		String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

		// Sensor enabled
		String sensor = "sensor=false";

		// Building the parameters to the web service
		String parameters = str_origin + "&" + str_dest + "&" + sensor+"&language=zh-TW";

		// Output format
		String output = "json";

		// Building the url to the web service
		String url = "https://maps.googleapis.com/maps/api/directions/"
				+ output + "?" + parameters;

		return url;
	}

	//JSON_3
	//Fetches data from url passed
	private class DownloadTask extends AsyncTask<String, Void, String>
	{

		// Downloading data in non-ui thread
		@Override
		protected String doInBackground(String... url) {

			// For storing data from web service
			String data = "";

			try {
				// Fetching the data from web service
				data = downloadUrl(url[0]);
			} catch (Exception e) {
				Log.d("Background Task", e.toString());
			}
			return data;
		}

		// Executes in UI thread, after the execution of
		// doInBackground()
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			ParserTask parserTask = new ParserTask();

			// Invokes the thread for parsing the JSON data
			parserTask.execute(result);

		}
	}

	//JSON_4
	/**從URL下載JSON資料的方法**/
	private String downloadUrl(String strUrl) throws IOException
	{
		String data = "";
		InputStream iStream = null;
		HttpURLConnection urlConnection = null;
		try {
			URL url = new URL(strUrl);

			// Creating an http connection to communicate with url
			urlConnection = (HttpURLConnection) url.openConnection();

			// Connecting to url
			urlConnection.connect();

			// Reading data from url
			iStream = urlConnection.getInputStream();

			BufferedReader br = new BufferedReader(new InputStreamReader(
					iStream));

			StringBuffer sb = new StringBuffer();

			String line = "";
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

			data = sb.toString();

			br.close();

		} catch (Exception e) {
			Log.d("Except in download url", e.toString());
		} finally {
			iStream.close();
			urlConnection.disconnect();
		}
		return data;
	}

	//JSON_5
	/** 解析JSON格式 **/
	private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>>
	{
		// Parsing the data in non-ui thread
		@Override
		protected List<List<HashMap<String, String>>> doInBackground(String... jsonData)
		{
			JSONObject jObject;
			List<List<HashMap<String, String>>> routes = null;

			try
			{
				jObject = new JSONObject(jsonData[0]);
				DirectionsJSONParser parser = new DirectionsJSONParser();

				// Starts parsing data
				routes = parser.parse(jObject);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			return routes;
		}

		// Executes in UI thread, after the parsing process
		@Override
		protected void onPostExecute(List<List<HashMap<String, String>>> result)
		{
			ArrayList<LatLng> points = null;
			PolylineOptions lineOptions = null;

			//處理沿路點用的設定
			MarkerOptions options_point = new MarkerOptions();
			options_point.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)); //沿路經過點符號顏色

			//處理轉折點用的設定
			MarkerOptions options_turn = new MarkerOptions();
			options_turn.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)); //沿路經過點符號顏色

			NewWayData.clear();

			// Traversing through all the routes
			for (int i = 0; i < result.size(); i++)
			{
				if (i == 0)
				{
					points = new ArrayList<LatLng>();
					lineOptions = new PolylineOptions();
					//new add route path array

					// Fetching i-th route
					List<HashMap<String, String>> path = result.get(i);
					lengthSum = 0; //初始，不然重讀會跑不出來

					// Fetching all the points in i-th route
					for (int j = 0; j < path.size(); j++)
					{
						HashMap<String, String> point = path.get(j);

						double lat = Double.parseDouble(point.get("lat"));
						double lng = Double.parseDouble(point.get("lng"));
						LatLng position = new LatLng(lat, lng);

						//儲存導航座標
						latlngpath.add(position);
						points.add(position);


						if(j>=1)//計算導航總長度(或直接從JSON分析)
						{
							lengthSum += (float) dis_2m(latlngpath.get(j-1).latitude, latlngpath.get(j-1).longitude,latlngpath.get(j).latitude, latlngpath.get(j).longitude);
						}


						//將沿路的點放在地圖上
						options_point.position(position);
						map.addMarker(options_point);

					}

					NewWayData.add("總共距離：" + String.valueOf(lengthSum));
					NewWayData.add("latlngpath: "+latlngpath);
					Log.i("Saving LatLng", String.valueOf(lengthSum) +",latlngpath: "+latlngpath+",i="+i);
				}

				/** 存html 文字 **/
				if (i == 1)
				{
					List<HashMap<String, String>> html_list = result.get(i);

					for (int k = 0; k < html_list.size(); k++)
					{
						HashMap<String, String> html_map = html_list.get(k);
						String html_str = html_map.get("html");
						//字串分離
						String fin_str = "";
						boolean str_flag = true;
						for(int m = 0;m<html_str.length();m++ )
						{
							Character tem_char =html_str.charAt(m);

							if(tem_char.equals('<'))
							{
								str_flag = false;
							}
							if(tem_char.equals('>'))
							{
								str_flag = true;
							}
							if(str_flag&&!tem_char.equals('>'))
							{
								fin_str+=tem_char;
							}
						}
						html_str_array.add(fin_str);

						NewWayData.add(fin_str);
						Log.i("Saving HTML", fin_str);
					}
				}

				/** 存每個path距離 **/
				if(i == 2)
				{
					List<HashMap<String, String>> dis_list = result.get(i);

					for (int k = 0; k < dis_list.size(); k++)
					{
						HashMap<String, String> dis_map = dis_list.get(k);
						String dis_str = dis_map.get("dis");
						dis_str_array.add(dis_str);

						NewWayData.add(dis_str);
						Log.i("Saving Distance",dis_str);
					}
				}

				/** 存每個path gps **/
				if(i == 3)
				{
					List<HashMap<String, String>> gps_list = result.get(i);

					for (int l = 0; l < gps_list.size(); l++)
					{
						try
						{
							HashMap<String, String> gps_map = gps_list.get(l);
							double lat = Double.parseDouble(gps_map.get("gps_lat"));
							double lng = Double.parseDouble(gps_map.get("gps_lng"));
							LatLng position = new LatLng(lat, lng);
							//儲存path座標
							gps_array.add(position);

							//將沿路的轉折點放在地圖上
  	  		            	   /*options_turn.position(position);
  	  		                   map.addMarker(options_turn);*/

							NewWayData.add(position.toString());
							Log.i("Saving gps",position+"");
						}
						catch (Exception e)
						{
							// TODO: handle exception
							Log.i("eerrt",e.toString());
						}
					}
				}
			}//END FOR

			// Adding all the points in the route to LineOptions
			lineOptions.addAll(points);
			lineOptions.width(5);  //導航路徑寬度
			lineOptions.color(Color.BLUE); //導航路徑顏色
			// Drawing polyline in the Google Map for the i-th route
			map.addPolyline(lineOptions);
		}
	}


	/**計算兩點座標距離(公尺)**/
	private double dis_2m(double lat_a, double lng_a, double lat_b, double lng_b)
	{
		double radLat1 = (lat_a * Math.PI / 180.0);
		double radLat2 = (lat_b * Math.PI / 180.0);
		double a = radLat1 - radLat2;
		double b = (lng_a - lng_b) * Math.PI / 180.0;
		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
				+ Math.cos(radLat1) * Math.cos(radLat2)
				* Math.pow(Math.sin(b / 2), 2)));
		s = s * EARTH_RADIUS;
		s = Math.round(s * 10000) / 10000;
		return s;
	}

	/**  計算方位角pab **/
	private double gps2d(double lat_a, double lng_a, double lat_b, double lng_b) {
		double d = 0;
		lat_a = lat_a * Math.PI / 180;
		lng_a = lng_a * Math.PI / 180;
		lat_b = lat_b * Math.PI / 180;
		lng_b = lng_b * Math.PI / 180;

		d = Math.sin(lat_a) * Math.sin(lat_b) + Math.cos(lat_a)
				* Math.cos(lat_b) * Math.cos(lng_b - lng_a);
		d = Math.sqrt(1 - d * d);
		d = Math.cos(lat_b) * Math.sin(lng_b - lng_a) / d;
		d = Math.asin(d) * 180 / Math.PI;

		// d = Math.round(d*10000);
		return d;
	}

	/** 計算兩點角度 **/
	private double computeAzimuth(double lat1, double lon1, double lat2,double lon2)
	{
		double result = 0.0;

		int ilat1 = (int) (0.50 + lat1 * 360000.0);
		int ilat2 = (int) (0.50 + lat2 * 360000.0);
		int ilon1 = (int) (0.50 + lon1 * 360000.0);
		int ilon2 = (int) (0.50 + lon2 * 360000.0);

		lat1 = Math.toRadians(lat1);
		lon1 = Math.toRadians(lon1);
		lat2 = Math.toRadians(lat2);
		lon2 = Math.toRadians(lon2);

		if ((ilat1 == ilat2) && (ilon1 == ilon2)) {
			return result;
		} else if (ilon1 == ilon2) {
			if (ilat1 > ilat2)
				result = 180.0;
		} else {
			double c = Math
					.acos(Math.sin(lat2) * Math.sin(lat1) + Math.cos(lat2)
							* Math.cos(lat1) * Math.cos((lon2 - lon1)));
			double A = Math.asin(Math.cos(lat2) * Math.sin((lon2 - lon1))
					/ Math.sin(c));
			result = Math.toDegrees(A);
			if ((ilat2 > ilat1) && (ilon2 > ilon1)) {
			} else if ((ilat2 < ilat1) && (ilon2 < ilon1)) {
				result = 180.0 - result;
			} else if ((ilat2 < ilat1) && (ilon2 > ilon1)) {
				result = 180.0 - result;
			} else if ((ilat2 > ilat1) && (ilon2 < ilon1)) {
				result += 360.0;
			}
		}
		return result;
	}
}
