package com.example.fwork.initial_ar10;


		import java.io.ByteArrayOutputStream;
		import java.io.IOException;
		import java.io.InputStream;
		import java.net.HttpURLConnection;
		import java.net.MalformedURLException;
		import java.net.URL;

		import android.annotation.SuppressLint;
		import android.content.Context;
		import android.content.res.Resources;
		import android.graphics.Bitmap;
		import android.graphics.BitmapFactory;
		import android.graphics.Canvas;
		import android.graphics.Paint;
		import android.graphics.Bitmap.CompressFormat;
		import android.os.AsyncTask;
		import android.os.Handler;
		import android.os.Message;
		import android.util.Base64;
		import android.util.Log;
		import android.view.View;
		import android.widget.Toast;
/**
 *
 * 交通路標
 * 直接放置資源裡後指定(因直接從雲端下載會有延遲，系統遲遲不接受會出錯)
 *
 * @author You-Hsin, Chen(陳友信)
 *
 */
public class Traffic_signal extends View
{
	private Resources res;
	private Bitmap pic;   //直接指定
	private Handler HandlerTime1 = new Handler();  //重畫
	private boolean test;  //true才畫

	public Traffic_signal(Context context)
	{
		super(context);

		res = getResources();
		test = false;

		HandlerTime1.postDelayed(timerRun1, 1000);
	}

	public void onDraw(final Canvas canvas)
	{
		Paint ObjPaint = new Paint();
		super.onDraw(canvas);

		if(test == true)
		{
			canvas.drawBitmap(pic,0,0, ObjPaint);
		}


		//HandlerTime1.postDelayed(timerRun1, 1000);
		//invalidate();// 更新畫面
		//MainSection.getdWindow().setCanvas(canvas);
	}

	private final Runnable timerRun1 = new Runnable()
	{
		public void run()
		{
			HandlerTime1.postDelayed(this, 1000);
			test = true;

			if(MainActivity.traffic_picture.equalsIgnoreCase("v1_1"))
			{
				pic = BitmapFactory.decodeResource(res, R.drawable.v1_1);
			}
			else if(MainActivity.traffic_picture.equalsIgnoreCase("v1_2"))
			{
				pic = BitmapFactory.decodeResource(res, R.drawable.v1_2);
			}
			else if(MainActivity.traffic_picture.equalsIgnoreCase("v1_3"))
			{
				pic = BitmapFactory.decodeResource(res, R.drawable.v1_3);
			}
			else if(MainActivity.traffic_picture.equalsIgnoreCase("v1_4"))
			{
				pic = BitmapFactory.decodeResource(res, R.drawable.v1_4);
			}
			else if(MainActivity.traffic_picture.equalsIgnoreCase("v1_5"))
			{
				pic = BitmapFactory.decodeResource(res, R.drawable.v1_5);
			}
			else if(MainActivity.traffic_picture.equalsIgnoreCase("v1_6"))
			{
				pic = BitmapFactory.decodeResource(res, R.drawable.v1_6);
			}
			else if(MainActivity.traffic_picture.equalsIgnoreCase("v1_7"))
			{
				pic = BitmapFactory.decodeResource(res, R.drawable.v1_7);
			}
			else if(MainActivity.traffic_picture.equalsIgnoreCase("v1_8"))
			{
				pic = BitmapFactory.decodeResource(res, R.drawable.v1_8);
			}
			else if(MainActivity.traffic_picture.equalsIgnoreCase("v2_1"))
			{
				pic = BitmapFactory.decodeResource(res, R.drawable.v2_1);
			}
			else
			{
				test = false;
			}

			invalidate();// 更新畫面
		}
	};
}
