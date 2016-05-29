package com.example.fwork.initial_ar10;


		import android.content.Context;
		import android.content.res.Resources;
		import android.graphics.Bitmap;
		import android.graphics.BitmapFactory;
		import android.graphics.Canvas;
		import android.graphics.Color;
		import android.graphics.Paint;
		import android.graphics.Path;
		import android.os.Handler;
		import android.view.View;
/**
 * 雷達顯示
 * 繪畫雷達的背景與上面的顯示點
 * @author You-Hsin, Chen(陳友信)
 *
 */
public class CompassBackground extends View
{
	MainActivity app;
	private Resources res;
	private Bitmap pic;   //直接指定
	private Handler HandlerTime1 = new Handler();  //我的Timer  重畫

	public CompassBackground(Context context)
	{
		super(context);
		app = (MainActivity) context;
		res = getResources();
		pic = BitmapFactory.decodeResource(res, R.drawable.car_m);

		HandlerTime1.postDelayed(timerRun1, 750);
	}

	public void onDraw(Canvas canvas)
	{
		float RADIUS = 150;
		float originX = app.dWindow.getWidth() - RADIUS, originY = RADIUS;

		Paint radarPaint = new Paint();
		//radarPaint.setColor(Color.argb(100, 45, 230, 155));
		radarPaint.setColor(Color.argb(100, 187, 255, 255));
		// radarPaint.setStyle(Paint.Style.FILL);
		radarPaint.setAntiAlias(true);// 消鋸齒
		canvas.drawCircle(150, 150, 150, radarPaint);


		// 雷達框線
		radarPaint.setColor(Color.argb(75, 45,230, 155));
		radarPaint.setStyle(Paint.Style.STROKE);
		canvas.drawCircle(150, 150, 150, radarPaint);
		canvas.drawCircle(150, 150, 120, radarPaint);
		canvas.drawCircle(150, 150, 90, radarPaint);
		canvas.drawCircle(150, 150, 60, radarPaint);
		canvas.drawCircle(150, 150, 30, radarPaint);


		//自己
		/*radarPaint.setColor(Color.argb(200, 0,0,200));
		radarPaint.setStyle(Paint.Style.FILL);
		Path path = new Path();
		path.moveTo(150,140);
		path.lineTo(140, 160);
		path.lineTo(150, 155);
		path.lineTo(160, 160);
		path.lineTo(150,140);
		path.close(); // 使這些點構成封閉的多邊形
		canvas.drawPath(path, radarPaint);
		path.reset();
		*/

		//自己(圖版)
		canvas.drawBitmap(pic,140,140, radarPaint);

		//點繪畫
		radarPaint.setStyle(Paint.Style.FILL);
		int car_number = MainActivity.car_number;
		double x,y;
		double  radian;  //轉換角度

		for(int i = 0; i < car_number ; i++)
		{
			/**角度、距離版本**/
			/*radian = Double.valueOf(MainSection.Near_Car_Data[i][2]) * Math.PI / 180;
			x = 150+Math.sin(radian) * Double.valueOf(MainSection.Near_Car_Data[i][1]) *10;
			y = 150-Math.cos(radian) * Double.valueOf(MainSection.Near_Car_Data[i][1]) *10;*/

			/**影片測試版本**/
			x = 150+ Double.valueOf(MainActivity.Near_Car_Data[i][1])*10;
			y = 150+ Double.valueOf(MainActivity.Near_Car_Data[i][2])*2.5;

			switch (Integer.valueOf(MainActivity.Near_Car_Data[i][3]))
			{
				case 1:  //綠
					radarPaint.setColor(Color.argb(150, 80,250,80));  //綠
					break;

				case 2:  //黃
					radarPaint.setColor(Color.argb(150, 250,250,0));  //黃
					break;

				case 3:  //紅
					radarPaint.setColor(Color.argb(150, 200,0,0));  //紅
					break;

				default: //橙  開車門or其他警告
					radarPaint.setColor(Color.argb(150, 128,0,255));
					canvas.drawCircle((float)x,(float)y, 12, radarPaint);
					break;
			};

			if(x >300 || y > 300)
			{
				//超過範圍
			}
			else
			{
				canvas.drawCircle((float)x,(float)y, 8, radarPaint);
			}


		}

		//自動重畫
		//HandlerTime1.postDelayed(timerRun1, 750);
		//invalidate();// 更新畫面
		//MainSection.getdWindow().setCanvas(canvas);
	}

	private final Runnable timerRun1 = new Runnable()
	{
		public void run()
		{
			HandlerTime1.postDelayed(this, 750);

			invalidate();// 更新畫面
		}
	};
}
