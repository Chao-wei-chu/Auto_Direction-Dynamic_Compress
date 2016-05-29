package com.example.fwork.initial_ar10;



		import com.google.android.gms.maps.CameraUpdateFactory;
		import com.google.android.gms.maps.GoogleMap;
		import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
		import com.google.android.gms.maps.MapFragment;
		import com.google.android.gms.maps.model.BitmapDescriptorFactory;
		import com.google.android.gms.maps.model.LatLng;
		import com.google.android.gms.maps.model.Marker;
		import com.google.android.gms.maps.model.MarkerOptions;

		import android.annotation.SuppressLint;
		import android.app.Activity;
		import android.os.Bundle;
		import android.view.View;
		import android.widget.Button;
		import android.widget.TextView;
/**
 *
 * 地圖直接點擊與測試
 * 回傳後可以模擬行進，可配合測試上方指示路線
 *
 * @author You-Hsin, Chen(陳友信)
 *
 */
@SuppressLint("NewApi")
public class Test_MapClick extends Activity
{
	final double EARTH_RADIUS = 6378137.0;
	private GoogleMap map2;
	private TextView DataTextView1;
	private TextView DataTextView2;
	private Button btn1,btn2,btn3,btn4;

	static  LatLng Start_point = new LatLng(0, 0);
	static  LatLng End_point = new LatLng(0, 0);

	private boolean test = false;  //false:設置起點  true:設置終點

	@SuppressLint("NewApi")
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.value_test2);

		DataTextView1 = (TextView) findViewById(R.id.textView1);
		DataTextView2 = (TextView) findViewById(R.id.textView2);
		btn1 = (Button) findViewById(R.id.button1);
		btn2 = (Button) findViewById(R.id.button2);
		btn3 = (Button) findViewById(R.id.button3);
		btn4 = (Button) findViewById(R.id.button4);

		map2 = ((MapFragment) getFragmentManager().findFragmentById(R.id.map2)).getMap();
		map2.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(23.696124, 120.534168), 16));

		//起點
		btn1.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				test = false;
			}
		});

		//終點
		btn2.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				test = true;
			}
		});

		//確定
		btn3.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				//傳送回去導航
				MainActivity.init_point = Start_point;
				MainActivity.End_Position = End_point;

				MainActivity.MapReturnTest = true;
				Test_MapClick.this.finish();//關閉
			}
		});

		//取消
		btn4.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				//直接關閉
				Test_MapClick.this.finish();
			}
		});

		map2.setOnMapClickListener(new OnMapClickListener()
		{
			@Override
			public void onMapClick(LatLng latLng)
			{
				map2.clear();// Clears the previously touched position

				MarkerOptions markerOptions = new MarkerOptions();  // Creating a marker

				if(test == false)  //起點
				{
					markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.car_m));
					markerOptions.position(latLng);  // Setting the position for the marker
					markerOptions.title(latLng.latitude + " : " + latLng.longitude);  // Setting the title for the marker. This will be displayed on taping the marker
					map2.animateCamera(CameraUpdateFactory.newLatLng(latLng));// Animating to the touched position
					map2.addMarker(markerOptions);  // Placing a marker on the touched position
					Start_point = latLng;

					if(End_point.latitude != 0)  //代表已有終點
					{
						markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
						markerOptions.position(End_point);
						markerOptions.title(End_point.latitude + " : " + End_point.longitude);
						map2.addMarker(markerOptions);
					}

				}
				else if (test == true)//終點
				{
					markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
					markerOptions.position(latLng);
					markerOptions.title(latLng.latitude + " : " + latLng.longitude);
					map2.animateCamera(CameraUpdateFactory.newLatLng(latLng));
					map2.addMarker(markerOptions);
					End_point = latLng;

					if(Start_point.latitude != 0)  //代表已有起點
					{
						markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.car_m));
						markerOptions.position(Start_point);
						markerOptions.title(Start_point.latitude + " : " + Start_point.longitude);
						map2.addMarker(markerOptions);
					}
				}

				if(Start_point.latitude != 0  && End_point.latitude != 0)  //兩點皆有，算出距離與角度
				{
					DataTextView2.setText( "距離 : " + String.valueOf(dis_2m(Start_point.latitude , Start_point.longitude , End_point.latitude , End_point.longitude))  + "公尺\n" +
							"方位角pab : " + String.valueOf(gps2d(Start_point.latitude , Start_point.longitude , End_point.latitude , End_point.longitude)) +"\n" +
							"方位角 : " + String.valueOf(computeAzimuth(Start_point.latitude , Start_point.longitude , End_point.latitude , End_point.longitude)) );
				}

				DataTextView1.setText("起點 : <"+String.valueOf(Start_point.latitude) + " , " + String.valueOf(Start_point.longitude) + ">\n" +
						"終點 : <"+String.valueOf(End_point.latitude) + " , " + String.valueOf(End_point.longitude) + ">\n");

			}
		});

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
