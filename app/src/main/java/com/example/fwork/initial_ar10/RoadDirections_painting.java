package com.example.fwork.initial_ar10;


		import java.util.HashMap;

		import com.google.android.gms.maps.model.LatLng;

		import android.content.Context;
		import android.content.res.Resources;
		import android.graphics.Bitmap;
		import android.graphics.BitmapFactory;
		import android.graphics.Canvas;
		import android.graphics.Color;
		import android.graphics.Paint;
		import android.graphics.Path;
		import android.graphics.RectF;
		import android.os.Handler;
		import android.text.format.Time;
		import android.util.Log;
		import android.view.SurfaceHolder;
		import android.view.View;
/**
 * 上方導航指示路線
 * 依然有會亂畫不穩定的問題
 *
 * @author You-Hsin, Chen(陳友信)
 *
 */
public class RoadDirections_painting extends View
{
	//我的Timer
	private Handler HandlerTime1 = new Handler();  //重畫
	private Resources res;
	private Bitmap pic;   //直接指定

	final double EARTH_RADIUS = 6378137.0;

	float Start_X,Start_Y,End_X,End_Y;  //繪畫"中間線"起點與終點
	float In_point1_X,In_point2_X,In_point3_X,In_point4_X;  //Y用傳進來的距離即可，不另外宣告, 1左上2右上3左下4右下
	float Leftout_point1_X,Leftout_point2_X;  //表外面深色的點，1左上2左下，與In_point1_X和In_point3_X搭配繪畫
	float Rightout_point1_X,Rightout_point2_X;  //表外面深色的點，1右上2右下，與In_point2_X和In_point4_X搭配繪畫

	private double lengthSum = 0;  //所有路徑長度
	private double length = 0;  //所有路徑長度
	private double Angle = 0;  //算點與點之角度

	private Canvas Mycanvas;

	public RoadDirections_painting(Context context)
	{
		super(context);

		//變數設定
		boolean AddAgain = true; //判斷突破100m的時刻，true代表可以再加,false則停止
		double paint_x = 0, paint_y = 0;
		lengthSum = 0;  //初始，不然重讀會跑不出來

		res = getResources();
		HandlerTime1.postDelayed(timerRun1, 500);
	}

	private void Painting_Road(Canvas canvas)  //畫出塗色路
	{
		Paint p = new Paint();                       // 創建畫筆
		p.setAntiAlias(true);                        // 設置畫筆的鋸齒效果。 true是去除。

		Path path = new Path();

		p.setStyle(Paint.Style.STROKE);
		//canvas.drawText(String.valueOf(MainSection.i), 20, 20, p);  //數字

		//中間線
		p.setColor(Color.argb(255,255,0,0));
		p.setStyle(Paint.Style.FILL);
		//canvas.drawLine(Start_X, Start_Y, End_X, End_Y, p);	//中間多一條分析紅線

		p.setColor(Color.argb(150,0,200,255));
		path.moveTo(In_point1_X,Start_Y);// 此點為多邊形的起點
		path.lineTo(In_point3_X,End_Y);
		path.lineTo(In_point4_X,End_Y);
		path.lineTo(In_point2_X,Start_Y);
		path.close(); // 使這些點構成封閉的多邊形
		canvas.drawPath(path, p);
		path.reset();

		//旁邊兩條深色區域
		p.setColor(Color.argb(150,0,100,150));

		path.moveTo(Leftout_point1_X,Start_Y);  //左
		path.lineTo(Leftout_point2_X,End_Y);
		path.lineTo(In_point3_X,End_Y);
		path.lineTo(In_point1_X,Start_Y);
		path.close();
		canvas.drawPath(path, p);
		path.reset();

		path.moveTo(In_point2_X,Start_Y);  //右
		path.lineTo(In_point4_X,End_Y);
		path.lineTo(Rightout_point2_X,End_Y);
		path.lineTo(Rightout_point1_X,Start_Y);
		path.close();
		canvas.drawPath(path, p);
		path.reset();
	}

	private void Painting_TestNodes(Canvas canvas)  //畫出分析點
	{
		Paint p = new Paint();                       // 創建畫筆
		p.setAntiAlias(true);                        // 設置畫筆的鋸齒效果。 true是去除。
		Path path = new Path();

		//測試用-起點標記
		/*p.setColor(Color.argb(150,0,255,0));
		canvas.drawRect(Start_X-5, Start_Y-5, Start_X+5, Start_Y+5, p);  //起點

		p.setColor(Color.argb(255,255,0,0));
		canvas.drawRect(End_X-5, End_Y-5, End_X+5, End_Y+5, p);  //終點

		p.setColor(Color.argb(255,255,255,0));
		canvas.drawRect(In_point1_X-5, Start_Y-5, In_point1_X+5, Start_Y+5, p);
		canvas.drawRect(In_point2_X-5, Start_Y-5, In_point2_X+5, Start_Y+5, p);
		canvas.drawRect(In_point3_X-5, End_Y-5, In_point3_X+5, End_Y+5, p);
		canvas.drawRect(In_point4_X-5, End_Y-5, In_point4_X+5, End_Y+5, p);

		canvas.drawRect(Rightout_point1_X-5,Start_Y-5,Rightout_point1_X+5,Start_Y+5,p);
		canvas.drawRect(Rightout_point2_X-5,End_Y-5,Rightout_point2_X+5,End_Y+5,p);

		canvas.drawRect(Leftout_point1_X-5,Start_Y-5,Leftout_point1_X+5,Start_Y+5, p);
		canvas.drawRect(Leftout_point2_X-5,End_Y-5,Leftout_point2_X+5,End_Y+5, p);
		*/
	}

	private void Painting_MySign(Canvas canvas)  //標示自己
	{
		Paint p = new Paint();                       // 創建畫筆
		p.setAntiAlias(true);                        // 設置畫筆的鋸齒效果。 true是去除。
		Path path = new Path();

		p.setColor(Color.argb(150,200,0,0));
		p.setStyle(Paint.Style.FILL);

		//弧線
		path.moveTo(40,0);// 此點為多邊形的起點
		path.quadTo(200, 50, 360, 0);
		path.lineTo(350,0);
		path.quadTo(200,40, 50, 0);
		path.lineTo(40,0);
		path.close(); // 使這些點構成封閉的多邊形
		canvas.drawPath(path, p);
		path.reset();

		path.moveTo(180,30);// 此點為多邊形的起點
		path.lineTo(200,60);
		path.lineTo(220,30);
		path.quadTo(200,40, 180, 30);
		path.close(); // 使這些點構成封閉的多邊形
		canvas.drawPath(path, p);
		path.reset();
	}

	private void RoadData_Analysis()
	{
		boolean AddAgain = true; //判斷突破100m的時刻，true代表可以再加,false則停止
		double paint_x = 0, paint_y = 0;
		lengthSum = 0;  //初始，不然重讀會跑不出來

		for (int j = MainActivity.Test_index; j < MainActivity.latlngpath.size(); j++)
		{

			if(j >= 1)
			{
				lengthSum += (double) dis_2m(MainActivity.latlngpath.get(j-1).latitude, MainActivity.latlngpath.get(j-1).longitude, MainActivity.latlngpath.get(j).latitude, MainActivity.latlngpath.get(j).longitude);
				length = (double) dis_2m(MainActivity.init_point.latitude, MainActivity.init_point.longitude, MainActivity.latlngpath.get(j).latitude, MainActivity.latlngpath.get(j).longitude);
				Angle = (double) computeAzimuth(MainActivity.init_point.latitude, MainActivity.init_point.longitude, MainActivity.latlngpath.get(j).latitude, MainActivity.latlngpath.get(j).longitude);

				if(AddAgain == true)
				{
					double X,Y,A,D;  //兩點比較後的座標X與Y，角度A與距離D
					A = 360 -(MainActivity.mbearing - Angle);
					if(A > 360)  //角度正規化
					{
						A = A - 360;
					}
					if(A > 180)  //四象限轉負度數
					{
						A = A - 360;
					}

					D = length;
					X = A*4;  /**更改變數：X**/
					//X = Math.abs((int)(My_GPS_point.latitude * 100000 - latlngpath.get(j).latitude * 100000));  求相對座標X用
					//Y = Math.abs((int)(My_GPS_point.longitude * 100000- latlngpath.get(j).longitude * 100000));  求相對座標Y用

					//進入繪畫部分，上方道路指示涂色
					RoadData_assign(paint_x , paint_y , X*(200/MainActivity.Detection_Distance) , D*(200/MainActivity.Detection_Distance),Mycanvas);  /**更改變數(數字)：需與下方lengthSum數字部分相乘為200**/
					paint_x = X*(200/MainActivity.Detection_Distance);  paint_y = D*(200/MainActivity.Detection_Distance);  //紀錄上一點的終點，做為下次繪畫的起點

					if(lengthSum > MainActivity.Detection_Distance)    /**更改變數(數字)：探測距離**/  //超過則關閉再加
					{
						AddAgain = false;
					}
				}
			}

		}
	}

	private void RoadData_assign(double x1,double y1,double x2,double y2,Canvas canvas)  //變數分配
	{
		Start_X = (float)x1+200;
		Start_Y = (float)y1;
		End_X = (float)x2+200;
		End_Y = (float)y2;

		//-----------若要有截至點加入這段

		if(Start_Y >= 200)
		{
			Start_Y = 200;
		}

		if(End_Y >= 200)
		{
			End_Y = 200;
		}

		if(Start_X >= 400)
		{
			Start_X= 400;
		}

		if(End_X >= 400)
		{
			End_X = 400;
		}


		if (Start_Y <= 0)
		{
			Start_Y = 0;
		}
		if (End_Y <= 0)
		{
			End_Y = 0;
		}
		if (Start_X <= 0)
		{
			Start_X = 0;
		}
		if (End_X <= 0)
		{
			End_X = 0;
		}

		//---------------

		In_point1_X = Start_X - (100 * (200 - Start_Y) / 200);
		In_point2_X = Start_X + (100 * (200 - Start_Y) / 200);
		In_point3_X = End_X - (100 * (200 - End_Y) / 200);
		In_point4_X = End_X + (100 * (200 - End_Y) / 200);

		Leftout_point1_X = In_point1_X - (50 * (200 - Start_Y) / 200);
		Leftout_point2_X = In_point3_X - (50 * (200 - End_Y) / 200);
		Rightout_point1_X = In_point2_X + (50 * (200 - Start_Y) / 200);
		Rightout_point2_X = In_point4_X + (50 * (200 - End_Y) / 200);

		Painting_Road(canvas);
		//Painting_TestNodes(canvas);  //測試分析點用
	}

	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		Mycanvas = canvas;

		if(MainActivity.latlngpath.size() > 1 && MainActivity.Video_Test_Number == 0)
		{
			/**新的**/
			RoadData_Analysis();  //分析、分配後才塗色繪畫
			Painting_MySign(canvas);  //標示自己
		}
		else if(MainActivity.Video_Test_Number == 1 || MainActivity.Video_Test_Number == 2)  //Demo直接貼圖不閃爍
		{
			Paint ObjPaint = new Paint();
			pic = BitmapFactory.decodeResource(res, R.drawable.test_road_1);//直線
			canvas.drawBitmap(pic,0,0, ObjPaint);
		}
		else if(MainActivity.Video_Test_Number == 3)  //Demo直接貼圖不閃爍
		{
			Paint ObjPaint = new Paint();
			pic = BitmapFactory.decodeResource(res, R.drawable.test_road_2);//曲線
			canvas.drawBitmap(pic,0,0, ObjPaint);
		}

		/*Paint p = new Paint();                       // 創建畫筆
		p.setAntiAlias(true);                        // 設置畫筆的鋸齒效果。 true是去除。

		Path path = new Path();

		p.setStyle(Paint.Style.STROKE);
		canvas.drawText(String.valueOf(MainSection.i), 20, 20, p);

		//中間線
		p.setColor(Color.argb(255,255,0,0));
		p.setStyle(Paint.Style.FILL);
		canvas.drawLine(Start_X, Start_Y, End_X, End_Y, p);

		p.setColor(Color.argb(150,0,0,255));
		path.moveTo(In_point1_X,Start_Y);// 此點為多邊形的起點
		path.lineTo(In_point3_X,End_Y);
		path.lineTo(In_point4_X,End_Y);
		path.lineTo(In_point2_X,Start_Y);
		path.close(); // 使這些點構成封閉的多邊形
		canvas.drawPath(path, p);
		path.reset();

		//旁邊兩條深色區域
		p.setColor(Color.argb(150,0,0,150));

		path.moveTo(Leftout_point1_X,Start_Y);  //左
		path.lineTo(Leftout_point2_X,End_Y);
		path.lineTo(In_point3_X,End_Y);
		path.lineTo(In_point1_X,Start_Y);
		path.close();
		canvas.drawPath(path, p);
		path.reset();

		path.moveTo(In_point2_X,Start_Y);  //右
		path.lineTo(In_point4_X,End_Y);
		path.lineTo(Rightout_point2_X,End_Y);
		path.lineTo(Rightout_point1_X,Start_Y);
		path.close();
		canvas.drawPath(path, p);
		path.reset();
        */

		//測試用-起點標記
		/*p.setColor(Color.argb(150,0,255,0));
		canvas.drawRect(Start_X-5, Start_Y-5, Start_X+5, Start_Y+5, p);  //起點

		p.setColor(Color.argb(255,255,0,0));
		canvas.drawRect(End_X-5, End_Y-5, End_X+5, End_Y+5, p);  //終點

		p.setColor(Color.argb(255,255,255,0));
		canvas.drawRect(In_point1_X-5, Start_Y-5, In_point1_X+5, Start_Y+5, p);
		canvas.drawRect(In_point2_X-5, Start_Y-5, In_point2_X+5, Start_Y+5, p);
		canvas.drawRect(In_point3_X-5, End_Y-5, In_point3_X+5, End_Y+5, p);
		canvas.drawRect(In_point4_X-5, End_Y-5, In_point4_X+5, End_Y+5, p);

		canvas.drawRect(Rightout_point1_X-5,Start_Y-5,Rightout_point1_X+5,Start_Y+5,p);
		canvas.drawRect(Rightout_point2_X-5,End_Y-5,Rightout_point2_X+5,End_Y+5,p);

		canvas.drawRect(Leftout_point1_X-5,Start_Y-5,Leftout_point1_X+5,Start_Y+5, p);
		canvas.drawRect(Leftout_point2_X-5,End_Y-5,Leftout_point2_X+5,End_Y+5, p);
		*/

		//invalidate();// 更新畫面
		//MainSection.getdWindow().setCanvas(canvas);
	}

	private final Runnable timerRun1 = new Runnable()
	{
		public void run()
		{
			HandlerTime1.postDelayed(this, 500);
			invalidate();// 更新畫面
		}
	};

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

}
