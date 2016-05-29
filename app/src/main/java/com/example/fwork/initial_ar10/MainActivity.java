package com.example.fwork.initial_ar10;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
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
import java.util.Locale;

//import com.google.android.maps.GeoPoint;

@SuppressLint("NewApi")
public class MainActivity extends FragmentActivity implements Runnable {
    /**KL_*車輛資訊*/
    public static int KL_Mycar_X=120534152;
    public static int KL_Mycar_Y=23696114 ;
    public static int KL_Carnumber=0;
    public static int[] KL_Othercar_X=new int[20];
    public static int[] KL_Othercar_Y=new int[20];
    /**ＫＬ_測試資訊**/
    public static int Co2,PM;
    public static String weather="";
    private String loadcondition="";

    /**鄉鎮顯示**/
    private Weather_show weather_s;

    /**天氣顯示**/
    private Local_show local_s;

    /**角背景**/
    private CornerBackground_TL cornerBG_TL;
    private CornerBackground_BL cornerBG_BL;
    private CornerBackground_BR cornerBG_BR;

    /** 網路狀態 **/
    private static boolean InternetTest;

    /**Screen**/
    private CameraSurface camScreen;
    private RoadDirections_painting Road_painting;
    private AirPollution_Painting Air_painting;
    private Color_painting Color_paing;
    private Traffic_signal traffic;  //交通號誌

    /**執行緒**/
    private Thread db_thread;

    /**羅盤雷達之指針**/
    private Compass compass;
    private CompassBackground compassBG;

    /**時速顯示**/
    private Speed_show speed;

    /**畫畫在main-view**/
    public static PaintScreen dWindow;

    /**Map**/
    static View tmpView;  //地圖
    static public float Map_Transparency = (float)0.7;  //地圖透明度

    static LatLng End_Position =  new LatLng(0, 0);
    static LatLng Test_point = new LatLng(0, 0); //測試用
    static LatLng CarAccident_point = new LatLng(23.917705, 120.546089); //車禍測試用
    private String StrDestination = "--終點--" ;  //輸入導航地址
    private Marker markerMe;
    private GoogleMap map;
    final double EARTH_RADIUS = 6378137.0;
    ArrayList<LatLng> markerPoints;
    PolylineOptions lineOptions = null;  //影片畫導航線用
    //!!!GPS資訊!!!
    static public ArrayList<LatLng> latlngpath = new ArrayList<LatLng>();  //儲存所有導航路徑
    static public ArrayList<LatLng> gps_array = new ArrayList<LatLng>();  //儲存所有路徑GPS
    static public ArrayList<String> html_str_array = new ArrayList<String>();  //儲存所有路徑名稱
    static public ArrayList<String> dis_str_array = new ArrayList<String>();  //儲存所有路徑距離
    //繪畫上方指示路用到的
    private double lengthSum = 0;  //所有路徑長度
    private double length = 0;  //所有路徑長度
    private double Angle = 0;  //算點與點之角度

    /**Sensor**/
    private SensorManager sm;
    private Sensor aSensor;
    private Sensor mSensor;
    float[] accelerometerValues = new float[3];
    float[] magneticFieldValues = new float[3];
    static public float mbearing=0;


    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    // Name of the connected device
    private String mConnectedDeviceName = null;
    // String buffer for outgoing messages
    private StringBuffer mOutStringBuffer;
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;

    /** 給Test:Map點擊的測試 **/
    public static boolean MapReturnTest = false;
    public static boolean Test_mode = false;

    /** 紀錄GPS現在資訊(經緯度、時間) **/
    private LocationManager myLocationManager;
    private Location myLocation=null;
    private String strLocationPrivider = "";

    /** Mode模式 **/
    private boolean singlemode = true;  //若有連上手機則False

    /** 繪畫AR指示路線 **/
    static public int Detection_Distance = 40;  //探測距離
    public static boolean SettingReturnTest = false;

    // Music
    private final int SOUND_COUNT = 12;  //音樂數量
    private int music1;
    private int music2;
    private int music3;
    private int music4;
    private int music5;
    private int music6;
    private int music7;
    private int music8;
    private int music9;
    private int music10;
    private int music11;
    private int music12;
    private SoundPool soundPool;
    int sound_delay = 0;  //音效播放嚴遲秒數
    //Timer
    Handler mHandlerTime = new Handler();

    //我的Timer
    private Handler MyHandlerTime = new Handler();  //Timer

    //測試用
    static public int Test_index= 0;

    //空汙資訊
    static String MyLocationCity = "";
    static String AirPollution_PublishTime = "";
    static int PSI_Level= 0;
    static int PSI_Density= 0;
    static int PM10_Density= 0;
    static int PM2_5_Level = 0;
    static int PM2_5_Density = 0;

    /**AR與Master的傳輸**/
    String AR_GetData ="";  //AR接收
    String AR_SendData = ""; //AR送出
    String TestMycity = "";  //傳給Master抓取的PM2.5測試站
    static float My_Speed_M,My_Speed_KM; //速度
    int Road_Speed_limit; //路段速限
    String Avoid_Threats_Instruction = "0";  //避險指示
    static String traffic_picture = "0";  //交通號誌
    static int Video_Test_Number = 0;  //測試影片編號

    /** Color_Painting **/
    //static int area_color[] = new int [4];  //陣列：0左 1上 2下 3右  ； 值：0無 1綠 2黃 3紅
    static int area_color[] = new int [6];  //陣列：0左上 1左下 2上 3下 4右上 5右下  ； 值：0無 1綠 2黃 3紅
    static int car_number;
    static String[][] Near_Car_Data;
    static String My_Lane;  //車道

    boolean PM_test = false;  //PM2.5警告
    //====================================================================
    static LatLng init_point = new LatLng(23.696136, 120.534142);
    static boolean direct_flag=false;
    static View DirectbuttonView;  //畫地圖用的view物件


    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //檢查網路
        InternetTest = false;
        CheckInternet();

        if(InternetTest == true)  //有網路才可開起
        {
            //KL   Car  dirction information test
            Car_dirction();
            //相機畫面
            BuildCamera();
            //KL_獲取資料
            Getconnection();
            //角背景
            BuildCorner();
            //上方指示道路
            Build_RoadDirectionsPainting();
            //羅盤雷達之指針
            BuildCompass();
            //畫畫在main-view
            setdWindow(new PaintScreen());
            //================================================================

            BuildMapView();//在左上角畫一個view物件
            DirectMapbutton();
            //上方指示道路
            Build_RoadDirectionsPainting();

            Button setdirectbutton=(Button)findViewById(R.id.mapbutton);
            setdirectbutton.setOnClickListener(new DirectMapOnclick());
            Button checkdirectbutton=(Button)findViewById(R.id.checkdirect);
            checkdirectbutton.setOnClickListener(new check_Direct());
            Button autodirectbutton=(Button)findViewById(R.id.autodirect);
            autodirectbutton.setOnClickListener(new auto_Direct());

            map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
            LatLng latLng=new LatLng(23.696136, 120.534142);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
            MarkerOptions markerOptions=new MarkerOptions();
            markerOptions.position(latLng);
            map.addMarker(markerOptions);

            //================================================================
            // Initializing
            markerPoints = new ArrayList<LatLng>();
            // Marker my_location = map.addMarker(new MarkerOptions().position(My_GPS_point).title("自己").snippet("車子資訊"));

            // Move the camera instantly to NKUT with a zoom of 16.
            //map.moveCamera(CameraUpdateFactory.newLatLngZoom(My_GPS_point, 16));

            /**
             * MixView 類別實作了 Runnable 介面
             * 仍需以 Thread 類別來建立執行緒
             **/
            db_thread = new Thread(this);
            db_thread.start();  //啟動執行緒
            //Sensor_part0-學長承接
            sm = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
            aSensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            mSensor = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            sm.registerListener(myListener, aSensor, SensorManager.SENSOR_DELAY_NORMAL);
            sm.registerListener(myListener, mSensor,SensorManager.SENSOR_DELAY_NORMAL);

            //下方威脅涂色
            Show_ColorPainting();
            //空氣汙染資訊
            Show_Air_Pollution();
            //交通號誌
            Show_Traffic_signal();
            //時速
            Show_Speed();
            //天氣
            Show_Weather();
            //鄉鎮
            Show_Local();
            //GPS追蹤紀錄資訊
            /**!!開起現在位置!!  +  onresume() **/


            //Google-Map
            updateCamera(mbearing);  //Map之轉動

            //Music初始
            this.soundPool = new SoundPool(SOUND_COUNT, AudioManager.STREAM_MUSIC,0);
            this.music1 = this.soundPool.load(this, R.raw.warn_cardoor_backcar, 1);  //開車門提醒音效
            this.music2 = this.soundPool.load(this, R.raw.warn_cardoor_frontcar, 1);  //開車門提醒音效
            this.music3 = this.soundPool.load(this, R.raw.warn_warn_sound, 1);
            this.music4 = this.soundPool.load(this, R.raw.warn_speed_limit_90, 1);
            this.music5 = this.soundPool.load(this, R.raw.warn_accident_rate, 1);
            this.music6 = this.soundPool.load(this, R.raw.warn_cardoor_backcar, 1);
            this.music7 = this.soundPool.load(this, R.raw.warn_opposite, 1);
            this.music8 = this.soundPool.load(this, R.raw.warn_clip_car, 1);
            this.music9 = this.soundPool.load(this, R.raw.start_navigation, 1);
            this.music10 = this.soundPool.load(this, R.raw.start_test, 1);
            this.music11 = this.soundPool.load(this, R.raw.end_test, 1);
            this.music12 = this.soundPool.load(this, R.raw.warn_air_pollution, 1);
            //音效通知
            mHandlerTime.postDelayed(sound_delay_timerRun, 1000);
        }
        else
        {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Please check your Internet")
                    .setMessage("Close Application")
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialog, int whichButton)
                                {
                                    System.exit(0);
                                }
                            }).show();
        }
    }
    //===================================================================

    private void BuildMapView() {
        /**MapView 地圖**/
        LayoutInflater inflater = getLayoutInflater();
        tmpView = inflater.inflate(R.layout.map_view, null);  //加入map視窗到主介面
        tmpView.setAlpha(Map_Transparency);//設定map透明度
        getWindow().addContentView(tmpView, new ViewGroup.LayoutParams(450, 450));  //可調地圖大小  調回350*350
    }

    private void DirectMapbutton() {
        /**MapButton**/
        LayoutInflater inflater = getLayoutInflater();
        DirectbuttonView = inflater.inflate(R.layout.map_button, null);//設定開啟mapdirect設定之介面
        DirectbuttonView.setAlpha((float)0.8);//設定透明度
        getWindow().addContentView(DirectbuttonView, new ViewGroup.LayoutParams(450, 80));
        DirectbuttonView = inflater.inflate(R.layout.check_direct, null);
        DirectbuttonView.setAlpha((float)0.8);//設定透明度
        getWindow().addContentView(DirectbuttonView, new  FrameLayout.LayoutParams(450, 140, Gravity.CENTER|Gravity.LEFT));
    }


    public class DirectMapOnclick implements Button.OnClickListener{
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, Navigation.class);
            startActivity(intent);
        }
    }
    public class check_Direct implements Button.OnClickListener{
        @Override
        public void onClick(View v) {
            if(direct_flag==false){
                showToast("Please Set End Point first.");
            }else{
                MaintainNavigation();
                direct_flag=false;
            }
        }
    }
    public class auto_Direct implements Button.OnClickListener{
        @Override
        public void onClick(View v) {
            if(direct_flag==false){
                showToast("Please Set End Point first.");
            }else{

                MaintainNavigation();
                Test_index = 0;
                MyHandlerTime.postDelayed(TestTimerRun, 3000);
            }
        }
    }

    //模擬導航所需之Timer設定
    private final Runnable TestTimerRun = new Runnable()
    {
        public void run()
        {
            MyHandlerTime.postDelayed(this, 3000);
            if((Test_index+1) <= latlngpath.size())
            {
                //逐步前進與測試繪畫
                init_point = new LatLng(latlngpath.get(Test_index).latitude,latlngpath.get(Test_index).longitude);
                RefreshNavigation();  //導航
                MarkerOptions markerOptions=new MarkerOptions();
                markerOptions.position(init_point);
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.car_m));
                map.addMarker(markerOptions);
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(init_point,17));

                Test_index++;
            }
            else //結束
            {
                direct_flag = false;
                MyHandlerTime.removeCallbacks(this);
                Toast.makeText(getApplicationContext(), "到達，結束模擬導航!", Toast.LENGTH_SHORT).show();
            }
        }
    };

    //自動化導航
    private void RefreshNavigation()
    {
        markerPoints.clear();
        map.clear();

        markerPoints.add(init_point);
        markerPoints.add(End_Position);

        //調整地圖zoom
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(init_point, map.getCameraPosition().zoom));

        // Creating MarkerOptions
        MarkerOptions options = new MarkerOptions();
        MarkerOptions options1 = new MarkerOptions();
        // Setting the position of the marker
        options.position(init_point);
        options1.position(End_Position);
        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)); //起點符號顏色
        options1.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)); //終點符號顏色

        map.addMarker(options1);


        //變數設定
        boolean AddAgain = true; //判斷突破100m的時刻，true代表可以再加,false則停止
        lengthSum = 0;  //初始，不然重讀會跑不出來
        ArrayList<LatLng> points = new ArrayList<LatLng>();
        lineOptions = new PolylineOptions();

        //處理沿路點用的設定
        MarkerOptions options_point = new MarkerOptions();
        options_point.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)); //沿路經過點符號顏色

        // Fetching all the points in i-th route
        for (int j = Test_index; j < latlngpath.size(); j++)
        {
            LatLng position = new LatLng(latlngpath.get(j).latitude, latlngpath.get(j).longitude);
            points.add(position);

            if(j>=1)//計算導航總長度(或直接從JSON分析)
            {
                lengthSum += (double) dis_2m(init_point.latitude, init_point.longitude,latlngpath.get(j).latitude, latlngpath.get(j).longitude);

                if(AddAgain == true)
                {
                    //新增要路過的點
                    options_point.position(position);
                    options_point.title(String.valueOf(lengthSum));
                    map.addMarker(options_point);

                    if(lengthSum > Detection_Distance)    /**更改變數(數字)：探測距離**/  //超過則關閉再加
                    {
                        AddAgain = false;
                    }
                }
            }
        }//END FOR

        // Adding all the points in the route to LineOptions
        lineOptions.addAll(points);
        lineOptions.width(5);  //導航路徑寬度
        lineOptions.color(Color.BLUE); //導航路徑顏色
        map.addPolyline(lineOptions);
    }


    public void showToast(String str){
        Toast.makeText(this,str,Toast.LENGTH_SHORT).show();
    }

    //===========================定位部分=================================
    //JSON_1
    private void MaintainNavigation()
    {
        markerPoints = new ArrayList<LatLng>(); //此處要加，不然會忘記
        markerPoints.clear();
        map.clear();
        /**JSON導航路徑**/
        // Already two locations
        // Adding new item to the ArrayList
        markerPoints.add(init_point);
        markerPoints.add(End_Position);
        //調整地圖zoom
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(init_point, map.getCameraPosition().zoom));

        // Creating MarkerOptions
        MarkerOptions options = new MarkerOptions();
        MarkerOptions options1 = new MarkerOptions();
        // Setting the position of the marker
        options.position(init_point);
        options1.position(End_Position);
        options1.title(StrDestination);
        /**
         * 起始及終點位置符號顏色
         **/

        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)); //起點符號顏色

        options1.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)); //終點符號顏色

        // Add new marker to the Google Map Android API V2
        //map.addMarker(options);//起點marker
        map.addMarker(options1);
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
        protected void onPostExecute(String result)
        {
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
            Log.d("Except download url", e.toString());
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
            lineOptions = null;

            //處理沿路點用的設定
            MarkerOptions options_point = new MarkerOptions();
            options_point.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)); //沿路經過點符號顏色

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++)
            {
                if (i == 0)
                {
                    points = new ArrayList<LatLng>();
                    lineOptions = new PolylineOptions();

                    // Fetching i-th route
                    List<HashMap<String, String>> path = result.get(i);

                    //變數設定
                    boolean AddAgain = true; //判斷突破100m的時刻，true代表可以再加,false則停止
                    lengthSum = 0;  //初始，不然重讀會跑不出來

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
                            lengthSum += (double) dis_2m(latlngpath.get(j-1).latitude, latlngpath.get(j-1).longitude,latlngpath.get(j).latitude, latlngpath.get(j).longitude);

                            if(AddAgain == true)
                            {
                                //新增要路過的點
                                options_point.position(position);
                                options_point.title(String.valueOf(lengthSum));
                                map.addMarker(options_point);

                                if(lengthSum > Detection_Distance)    /**更改變數(數字)：探測距離**/  //超過則關閉再加
                                {
                                    AddAgain = false;
                                }
                            }
                        }

                    }

                    //Toast.makeText(getApplicationContext(), "進入更新，探測距離:" + String.valueOf(Detection_Distance), Toast.LENGTH_SHORT).show();
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
    //===================================================================




    //檢查網路
    private void CheckInternet()
    {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();

		/*如果未連線的話，mNetworkInfo會等於null
		if(mNetworkInfo != null)
		{
		    //網路是否已連線(true or false)
		    mNetworkInfo.isConnected();
		    //網路連線方式名稱(WIFI or mobile)
		    mNetworkInfo.getTypeName();
		    //網路連線狀態
		    mNetworkInfo.getState();
		    //網路是否可使用
		    mNetworkInfo.isAvailable();
		    //網路是否已連接or連線中
		    mNetworkInfo.isConnectedOrConnecting();
		    //網路是否故障有問題
		    mNetworkInfo.isFailover();
		    //網路是否在漫遊模式
		    mNetworkInfo.isRoaming();
		}
		*/
        if(mNetworkInfo == null)
        {
            InternetTest = false;
        }
        else
        {
            InternetTest = true;		//有網路
        }
    }




    //下方威脅著色部分
    private void Show_ColorPainting()
    {
        //---畫畫部分---
        Color_paing =  new Color_painting(this);

        //通知view組件重繪
        getWindow().addContentView(Color_paing, new FrameLayout.LayoutParams(400, 400, Gravity.CENTER|Gravity.BOTTOM));
        //---畫畫部分---
    }

    private void Show_Air_Pollution()
    {
        Air_painting=new AirPollution_Painting(this);

        //通知view組件重繪
        getWindow().addContentView(Air_painting,new FrameLayout.LayoutParams(660, 200, Gravity.TOP|Gravity.RIGHT));
    }

    private void Show_Traffic_signal()
    {
        traffic = new Traffic_signal(this);

        //通知view組件重繪
        getWindow().addContentView(traffic,new FrameLayout.LayoutParams(1100, 250, Gravity.TOP|Gravity.CENTER));
    }

    private void Show_Speed()
    {
        speed = new Speed_show(this);

        //通知view組件重繪
        getWindow().addContentView(speed,new FrameLayout.LayoutParams(400, 140, Gravity.LEFT|Gravity.BOTTOM));
    }

    /**上方道路著色部分**/
    //private void Build_RoadDirectionsPainting(double x1,double y1,double x2,double y2)
    private void Build_RoadDirectionsPainting()
    {
        //ViewGroup parent = (ViewGroup) Road_painting.getParent();
        //parent.removeView(Road_painting);

        //---畫畫部分---
        //Road_painting=new RoadDirections_painting(this,(float)x1,(float)y1,(float)x2,(float)y2);
        Road_painting=new RoadDirections_painting(this);

        //通知view組件重繪
        getWindow().addContentView(Road_painting,new FrameLayout.LayoutParams(400, 200, Gravity.CENTER|Gravity.TOP));

    }

    //相機畫面
    private void BuildCamera()
    {
        camScreen = new CameraSurface(this);
        setContentView(camScreen);
        //附上權限，加上CameraSurface、Compatibility兩個class
}

    //羅盤雷達之指針
    private void BuildCompass()
    {
        compassBG = new CompassBackground(this);
        getWindow().addContentView(compassBG, new FrameLayout.LayoutParams(300,300, Gravity.END));
        //加上背景

        compass = new Compass(this);
        addContentView(compass, new FrameLayout.LayoutParams(300, 300, Gravity.END));
        //加上Compass一個class，成果在右上角顯示(單純四指針無圓盤)，無轉動
    }


    //----------畫畫在main-view用----------
    static PaintScreen getdWindow() {
        return dWindow;
    }

    static void setdWindow(PaintScreen dWindow) {
        MainActivity.dWindow = dWindow;
    }
    //----------畫畫在main-view用----------

    /**Rotate MapView**/
    public void updateCamera(float bearing)
    {
        CameraPosition currentPlace = new CameraPosition.Builder()
                .target(init_point)
                .bearing(bearing)
                .tilt(65.5f)
                .zoom(map.getCameraPosition().zoom)
                .build();
        //added a tilt[.tilt(65.5f)] value so the map will rotate in 3D.
        map.moveCamera(CameraUpdateFactory.newCameraPosition(currentPlace));

    }



    //Sensor_part1-學長承接
    final SensorEventListener myListener = new SensorEventListener()
    {
        public void onSensorChanged(SensorEvent sensorEvent)
        {

            if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
                magneticFieldValues = sensorEvent.values.clone();
            if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
                accelerometerValues = sensorEvent.values.clone();

            calculateOrientation();

            //誤差忽略
            mbearing = (float) ((int)mbearing  / 5);
            mbearing = (float) (mbearing  * 5);


            //設定度數
            compass.setDirection(-(int)mbearing);
            updateCamera(mbearing);
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy)
        {}

    };

    //Sensor_part2-學長承接
    private  void calculateOrientation()
    {
        float[] values = new float[3];
        float[] R_in = new float[9];
        float[] R_out = new float[9];
        SensorManager.getRotationMatrix(R_in, null, accelerometerValues, magneticFieldValues);
        SensorManager.remapCoordinateSystem(R_in, SensorManager.AXIS_X, SensorManager.AXIS_Z, R_out);
        SensorManager.getOrientation(R_out, values);

        //mbearing = (float) (Math.toDegrees(values[0])+360)%360;
            //換算Z軸角度
            mbearing = (float) (Math.toDegrees(values[0])+360)%360;
    }


    //音效延遲
    private final Runnable sound_delay_timerRun = new Runnable()
    {
        public void run()
        {
            mHandlerTime.postDelayed(this, 1000);
            if(sound_delay == 0 && (!Avoid_Threats_Instruction.equals("0")))
            {
                sound_delay = 8;
                if(Avoid_Threats_Instruction.equals("warn_warn_sound"))
                {
                    soundPool.play(music3 , 1, 1, 0, 0, 1); //播放第一種音效
                }
                else if(Avoid_Threats_Instruction.equals("warn_speed_limit_90"))
                {
                    soundPool.play(music4 , 1, 1, 0, 0, 1);  //播放第一種音效
                }
                else if(Avoid_Threats_Instruction.equals("warn_accident_rate"))
                {
                    soundPool.play(music5 , 1, 1, 0, 0, 1);  //播放第一種音效
                }
                else if(Avoid_Threats_Instruction.equals("warn_cardoor_backcar"))
                {
                    soundPool.play(music6 , 1, 1, 0, 0, 1);  //播放第一種音效
                }
                else if(Avoid_Threats_Instruction.equals("warn_opposite"))
                {
                    soundPool.play(music7 , 1, 1, 0, 0, 1);  //播放第一種音效
                }
                else if(Avoid_Threats_Instruction.equals("warn_clip_car"))
                {
                    soundPool.play(music8 , 1, 1, 0, 0, 1);  //播放第一種音效
                }
            }
            else if(sound_delay > 0)
            {
                sound_delay--;
            }
        }
    };



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        int base = Menu.FIRST;

        MenuItem item1 =menu.add(base, base, base, "1.導航");
        MenuItem item2 =menu.add(base, base+1, base+1,"2.尋找藍芽配對");
        MenuItem item3 =menu.add(base, base+2, base+2,"3.開放藍芽被搜尋");
        MenuItem item4 =menu.add(base, base+3, base+3,"4經緯度導航Demo_數值輸入");
        MenuItem item5 =menu.add(base, base+4, base+4,"5.經緯度導航Demo_地圖點擊");
        MenuItem item6 =menu.add(base, base+5, base+5,"6.AR設定");
        MenuItem item7 =menu.add(base, base+6, base+6,"7.退出測試模式");

        return true;
    }

    //case_3
    private EditText edit_1,edit_2,edit_3,edit_4;
    static LatLng TestPoint_1 ;
    static LatLng TestPoint_2 ;



    @Override
    public void run()
    {
        // TODO Auto-generated method stub
        while(true)
        {
            try
            {
                Thread.sleep(1000);  //睡一秒鐘，不然會一直跑
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
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
    private double computeAzimuth(double lat1, double lon1, double lat2,
                                  double lon2) {
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
    private  void BuildCorner()
    {
        //cornerBG_TL=new CornerBackground_TL(this);
        //getWindow().addContentView(cornerBG_TL,new FrameLayout.LayoutParams(400,200,Gravity.LEFT|Gravity.TOP));
        cornerBG_BL=new CornerBackground_BL(this);
        getWindow().addContentView(cornerBG_BL,new FrameLayout.LayoutParams(400,200,Gravity.LEFT|Gravity.BOTTOM));
        cornerBG_BR=new CornerBackground_BR(this);
        getWindow().addContentView(cornerBG_BR, new FrameLayout.LayoutParams(400, 200, Gravity.RIGHT | Gravity.BOTTOM));
    }
    private void Getvalue(String output,String local)
    {
        int start=0;
        String  out[]={"","","",""};
        for(int i=0;i<4;i++)
        {
            int local1=output.indexOf(local,start);
            int local2=output.indexOf(":",local1+2);//第1個":"
            int local3=output.indexOf(":",local2+1);//第2個":"
            int local4=output.indexOf(":",local3+1);//第3個":"
            out[i]=output.substring(local3+1,local4);
            // Log.d("111",out[i]);
            if(i==0)
            {
                start=local1+(local4-local1)+5;//5包含<br/>
            }
            else
            {
                start = start + (local4 - local1) + 5;//5包含<br/>
            }

        }
        Co2=Integer.valueOf(out[0]);
        PM=Integer.valueOf(out[1]);
        weather=out[2];
        loadcondition=out[3];
        Log.d("111", Co2 + "," + PM + "," + weather + "," + loadcondition);
    }
    private void Getconnection()
    {
        new Thread(new Runnable()
        {
            public void run()
            {
                try
                {
//API串接的uri路徑 (小黑人目前範例先取用YouTube API)
                    Log.d("111", "1");
                    String uri = "http://140.125.45.200:2226/hbase/TestTableGetPost1.jsp";
                    HttpClient mHttpClient = new DefaultHttpClient();
                    Log.d("111","2");
                    HttpPost mHttpGet = new HttpPost(uri);
                    Log.d("111","3");
                    HttpResponse mHttpResponse= null;
                    try {
                        mHttpResponse = mHttpClient.execute(mHttpGet);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    Log.d("111","4");
// HttpURLConnection.HTTP_OK為200，200代表串接溝通成功
                    Log.d("111","4-2"+mHttpResponse.getStatusLine().getStatusCode()+"");
                    if(mHttpResponse.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK )
                    {
                        Log.d("111",mHttpResponse.getStatusLine().getStatusCode()+"");
//API的回傳文字，格式為json格式
                        Log.d("111",mHttpResponse.getEntity().getContentType()+"");
                        // Log.d("111",mHttpResponse.getEntity().getContent()+"");
                        Log.d("111", mHttpResponse.getEntity().toString() + "");
                        // HttpEntity entity = mHttpResponse.getEntity();
                        Log.d("111","4-3");
                        String result = EntityUtils.toString(mHttpResponse.getEntity());
                        int body1=result.indexOf("body", 0);
                        int body2=result.indexOf("body",body1+4);
                        Log.d("111", body1+"");
                        Log.d("111",body2+"");
                        String output=result.substring(body1+13,body2-17);
                        // Log.d("111",output);
                        // tv1.setText(output);
                        // String mJsonText = EntityUtils.toString(mHttpResponse.getEntity());
                        //String a=mJsonText.toString();
                        // Log.d("111","5"+mJsonText);
//將json格式解開並取出Title名稱

                        //String mTitle = new JSONObject(new JSONObject(output).getString("A")).toString();
                        // JSONObject jo=new JSONObject(output);
                        Getvalue(output,"斗六");
                        //tv1.setText(output);
                        // Log.d("get",mTitle);


                    }
                    else
                    {
                        // Log.d("StatusCode",mHttpResponse.getStatusLine().getStatusCode()+"");
                    }
                }
                catch(Exception e)
                {
                    Log.d("111","失敗");
                }
            }
        }).start();
    }
    private void Show_Weather() {
        weather_s = new Weather_show(this);

        //通知view組件重繪
        getWindow().addContentView(weather_s, new FrameLayout.LayoutParams(400, 140, Gravity.RIGHT | Gravity.BOTTOM));
    }
    private void Show_Local() {
        local_s = new Local_show(this);

        //通知view組件重繪
        getWindow().addContentView(local_s,new FrameLayout.LayoutParams(400, 140, Gravity.RIGHT|Gravity.BOTTOM));
    }

    private void Car_dirction()
    {
        new Thread(new Runnable()
        {
            public void run()
            {
                try
                {
                    String uri = "http://140.125.45.200:2226/hbase/KZ_web/CarPointReturn_none_car_info.jsp";
                    HttpClient mHttpClient = new DefaultHttpClient();
                    HttpPost mHttpGet = new HttpPost(uri);
                    HttpResponse mHttpResponse= null;
                    try {
                        mHttpResponse = mHttpClient.execute(mHttpGet);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    if(mHttpResponse.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK )// HttpURLConnection.HTTP_OK為200，200代表串接溝通成功
                    {
                        String result = EntityUtils.toString(mHttpResponse.getEntity());
                        int body1=result.indexOf("body", 0);
                        int body2=result.indexOf("body",body1+4);
                        String output=result.substring(body1+13,body2-17);
                        Log.d("222","sc");
                        GetCarDirection(output);
                    }
                    else
                    {
                        // Log.d("StatusCode",mHttpResponse.getStatusLine().getStatusCode()+"");
                    }
                }
                catch(Exception e)
                {
                    Log.d("111","失敗");
                }
            }
        }).start();
    }
    private void GetCarDirection(String output)
    {
        int number=0;
        int start=0;
        int local=0;
        int t=0;
        while(t==0){

            local = output.indexOf("<br/>", start+1);
            if(local<start)
            {
                t=1;
            }
            else {
                start = local;
                number++;
                Log.d("222", "sc" + number);
            }
        }
        number=(number+1)/2;
        KL_Carnumber=number;
        Log.d("222", "number=" + KL_Carnumber);
        KL_Othercar_Y[0]=(int)((Double.valueOf(output.substring(1, 10))) * 1000000);
        KL_Othercar_X[0]=(int)((Double.valueOf(output.substring(17, 28))) * 1000000);
        KL_Mycar_Y=(int)((Double.valueOf(output.substring(1 + 5 * 33, 10 + 5 * 33)))*1000000);
        KL_Mycar_X=(int)((Double.valueOf(output.substring(17 + 5 * 33, 28 + 5 * 33)))*1000000);
        for(int i=6;i<KL_Carnumber;i++)
        {
            KL_Othercar_Y[i]=(int)((Double.valueOf(output.substring(1 + i * 33, 10 + i * 33))) * 1000000)-KL_Mycar_Y;
            KL_Othercar_X[i]=(int)((Double.valueOf(output.substring(17 + i * 33, 28 + i * 33))) * 1000000)-KL_Mycar_X;
            Log.d("222", "X " + KL_Othercar_X[i]);
            Log.d("222", "Y " + KL_Othercar_Y[i]);

        }

    }
}
