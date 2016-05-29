package com.example.fwork.initial_ar10;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

/**
 * 空汙資訊"畫"出來
 * 有 1."按"的事件 2.詳細版與精簡版
 *
 * @author You-Hsin, Chen(陳友信)
 *
 */
public class AirPollution_Painting  extends View
{
	//我的Timer
	private Handler HandlerTime1 = new Handler();  //重畫
	private boolean paint_mode = false;  //true:完整資訊  false:縮減資訊

	public AirPollution_Painting(Context context)
	{
		super(context);
		HandlerTime1.postDelayed(timerRun1, 1000);  //Timer
	}

	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);

		Paint p = new Paint();                       // 創建畫筆
		p.setAntiAlias(true);                       	  // 設置畫筆的鋸齒效果。 true是去除。

		if(MainActivity.MyLocationCity.equals(""))  //無監測站
		{
			p.setStyle(Paint.Style.FILL);
			p.setColor(Color.argb(120, 0, 0, 0));
			canvas.drawRect(100, 0, 330, 40, p);

			p.setTextSize(30);  // 設置文字大小
			p.setColor(Color.argb(240, 200, 0, 0));
			p.setTypeface(Typeface.DEFAULT_BOLD);
			//KL_註解       canvas.drawText("目前無空汙資料",107,30, p);
			canvas.drawText("PM2.5  "+MainActivity.PM,107,30,p);
		}
		else  //有資料
		{
			p.setStyle(Paint.Style.FILL);

			//PSI
			switch(MainActivity.PSI_Level)
			{
				case 1:
					p.setColor(Color.argb(120,0,255,0));
					break;
				case 2:
					p.setColor(Color.argb(120,255,255,0));
					break;
				case 3:
					p.setColor(Color.argb(120,255,0,0));
					break;
				case 4:
					p.setColor(Color.argb(120,128,0,128));
					break;
				case 5:
					p.setColor(Color.argb(120,99,51,0));
					break;
			}

			if (paint_mode == true)  //完整版
			{
				canvas.drawRect(0, 0, 350, 120, p);
			}
			else if  (paint_mode == false)  //縮減版
			{
				canvas.drawRect(100, 0, 300,70, p);
			}

			//PSI_PM2.5
			switch(MainActivity.PM2_5_Level)
			{
				case 1:
					p.setColor(Color.argb(120,156,255,156));
					break;
				case 2:
					p.setColor(Color.argb(120,49,255,0));
					break;
				case 3:
					p.setColor(Color.argb(120,49,207,0));
					break;
				case 4:
					p.setColor(Color.argb(120,255,255,0));
					break;
				case 5:
					p.setColor(Color.argb(120,255,207,0));
					break;
				case 6:
					p.setColor(Color.argb(120,255,154,0));
					break;
				case 7:
					p.setColor(Color.argb(120,255,100,100));
					break;
				case 8:
					p.setColor(Color.argb(120,255,0,0));
					break;
				case 9:
					p.setColor(Color.argb(120,153,0,0));
					break;
				case 10:
					p.setColor(Color.argb(120,206,48,255));
					break;
			}


			if (paint_mode == true)  //完整版
			{
				canvas.drawRect(0, 120, 350, 200, p);

				//文字資訊(完整)
				p.setColor(Color.argb(240, 0, 150, 150));
				p.setTypeface(Typeface.MONOSPACE);
				p.setTextSize(30);  // 設置文字大小
				canvas.drawText(MainActivity.MyLocationCity,10,30, p);  //地點

				p.setTextSize(20);  // 設置文字大小
				canvas.drawText("發佈時間:" + MainActivity.AirPollution_PublishTime,10,55, p);  //發佈時間
				canvas.drawText("PSI等級:" + String.valueOf(MainActivity.PSI_Level),10,75, p);  //PSI等級
				canvas.drawText("PSI:" + String.valueOf(MainActivity.PSI_Density),10,95, p);  //PSI指標
				canvas.drawText("PM10濃度:" + String.valueOf(MainActivity.PM10_Density),10,115, p);  //PM10濃度

				canvas.drawText("PM2.5等級:" + String.valueOf(MainActivity.PM2_5_Level),10,150, p);  //PM2.5等級
				canvas.drawText("PM2.5濃度:" + String.valueOf(MainActivity.PM2_5_Density),10,180, p);  //PM2.5濃度

				p.setColor(Color.argb(240, 200, 0, 0));
				p.setTypeface(Typeface.DEFAULT_BOLD);
				p.setTextSize(45);  // 設置文字大小
				canvas.drawText(String.valueOf(MainActivity.PSI_Level) + "/5",220,110, p);
				canvas.drawText(String.valueOf(MainActivity.PM2_5_Level) + "/10",220,170, p);
			}
			else if  (paint_mode == false)  //縮減版
			{
				canvas.drawRect(100, 70, 300,105, p);

				p.setColor(Color.argb(240, 0, 150, 150));
				p.setTypeface(Typeface.MONOSPACE);
				p.setTextSize(30);  // 設置文字大小
				canvas.drawText(MainActivity.MyLocationCity,110,30, p);  //地點


				p.setTextSize(30);  // 設置文字大小
				p.setTypeface(Typeface.DEFAULT_BOLD);
				canvas.drawText("PSI等級:",110,65, p);
				canvas.drawText("PM2.5等級:",110,100, p);

				p.setColor(Color.argb(240, 200, 0, 0));
				p.setTextSize(30);  // 設置文字大小
				p.setTypeface(Typeface.DEFAULT_BOLD);
				canvas.drawText(String.valueOf(MainActivity.PSI_Level),230,65, p);  //PSI等級
				canvas.drawText(String.valueOf(MainActivity.PM2_5_Level),270,100, p);  //PM2.5等級
			}
		}
	}

	//Timer
	private final Runnable timerRun1 = new Runnable()
	{
		public void run()
		{
			HandlerTime1.postDelayed(this, 1000); 	//每秒更新
			invalidate();// 更新(重畫)畫面
		}
	};

	//觸碰事件，兩種版本切換
	public boolean onTouchEvent (MotionEvent event)
	{
		//float X=event.getX();
		//float Y=event.getY();
		switch (event.getAction())
		{
			case MotionEvent.ACTION_DOWN:  //展現資訊(完整)

				if (paint_mode == true)
				{
					paint_mode = false;
				}
				else if  (paint_mode == false)
				{
					paint_mode = true;
				}

				invalidate();// 更新畫面
				break;
			/*case MotionEvent.ACTION_MOVE:
			
		    	invalidate();
				break;
			case MotionEvent.ACTION_UP:
				invalidate();
				break;*/
		}
		return true;
	}

}
