package com.example.fwork.initial_ar10;


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
		import android.view.View;
/**
 * 六方位Coloring顯示
 *
 * @author You-Hsin, Chen(陳友信)
 *
 */
public class Color_painting extends View
{
	private int area_color[] = new int [6];  //陣列：0左上 1左下 2上 3下 4右上 5右下   ； 值：0無 1綠 2黃 3紅
	private String my_lane = "0";  //自車時速,座標xy
	private Handler HandlerTime1 = new Handler();  //我的Timer  重畫

	//利用圖片切換，解決重畫不穩定狀態(有時候會亂畫)
	private Resources res;
	private Bitmap pic;   //直接指定

	public Color_painting(Context context)
	{
		super(context);
		res = getResources();
		HandlerTime1.postDelayed(timerRun1, 1000);
	}

	protected void onDraw(Canvas canvas)
	{

		super.onDraw(canvas);

		//將模擬程式需要的資料傳來這個class使用方便判斷
		for(int i = 0 ; i < 6 ; i++)
		{
			area_color[i] =MainActivity.area_color[i];
		}

		my_lane = MainActivity.My_Lane;

		// 建立初始畫布
		Paint p = new Paint();                       // 創建畫筆
		p.setAntiAlias(true);                        // 設置畫筆的鋸齒效果。 true是去除。
		Path path = new Path();

		/**Lane之判別**/
		/**直接畫版，實測用**/
		//判別著色   陣列：0左上 1左下 2上 3下 4右上 5右下   ； 值：0無 1綠 2黃 3紅
		/*p.setStyle(Paint.Style.FILL);

		for(int i = 0 ; i < 6 ; i++)
		{
			switch (area_color[i])
			{
				case 1:  //綠
				   p.setColor(Color.argb(200,0,255,0));
				   break;
				case 2:  //黃
				   p.setColor(Color.argb(200,255,255,0));
				   break;
				case 3:  //紅
				   p.setColor(Color.argb(200,255,0,0));
				   break;
				default:  //表0，不用畫色
				   break;
			};

			if(area_color[i] > 0)  //大於0才畫
			{
				switch (i)
				{
					case 0:  //左上
						path.moveTo(160,230);
						path.lineTo(110,230);
						path.lineTo(70,180);
						path.lineTo(120,180);
						path.close();
						canvas.drawPath(path, p);
						path.reset();
						break;
					case 1:  //左下
						path.moveTo(160,270);
						path.lineTo(110,270);
						path.lineTo(70,320);
						path.lineTo(120,320);
						path.close();
						canvas.drawPath(path, p);
						path.reset();
						break;
					case 2:  //上
						path.moveTo(200,120);
						path.lineTo(160,150);
						path.lineTo(200,250);
						path.lineTo(240,150);
						path.close();
						canvas.drawPath(path, p);
						path.reset();
						break;
					case 3:  //下
						path.moveTo(200,260);
						path.lineTo(170,330);
						path.lineTo(200,350);
						path.lineTo(230,330);
						path.close();
						canvas.drawPath(path, p);
						path.reset();
						break;
					case 4:  //右上
						path.moveTo(240,230);
						path.lineTo(290,230);
						path.lineTo(330,180);
						path.lineTo(280,180);
						path.close();
						canvas.drawPath(path, p);
						path.reset();
						break;
					case 5:  //右下
						path.moveTo(240,270);
						path.lineTo(290,270);
						path.lineTo(330,320);
						path.lineTo(280,320);
						path.close();
						canvas.drawPath(path, p);
						path.reset();
						break;

					default:  //表0，不用畫色
					   break;
				};
			}
		}*/


		//框繪畫
		//p.setColor(Color.BLACK);
		//p.setStyle(Paint.Style.STROKE);

		//-----------------------------------------------

		//HandlerTime1.postDelayed(timerRun1, 1000);
		//invalidate();// 更新畫面
		//MainSection.getdWindow().setCanvas(canvas);

		/**切換圖版，測試影片用**/
		for(int i = 0 ; i < 6 ; i++)
		{
			if(area_color[i] > 0)  //大於0才畫
			{
				switch (i)
				{
					case 0:  //左上
						switch (area_color[i])
						{
							case 1:  //綠
								pic = BitmapFactory.decodeResource(res, R.drawable.coloring_1_1);
								break;
							case 2:  //黃
								pic = BitmapFactory.decodeResource(res, R.drawable.coloring_1_2);
								break;
							case 3:  //紅
								pic = BitmapFactory.decodeResource(res, R.drawable.coloring_1_3);
								break;
						};
						canvas.drawBitmap(pic,70,180,p);
						break;
					case 1:  //左下
						switch (area_color[i])
						{
							case 1:  //綠
								pic = BitmapFactory.decodeResource(res, R.drawable.coloring_2_1);
								break;
							case 2:  //黃
								pic = BitmapFactory.decodeResource(res, R.drawable.coloring_2_2);
								break;
							case 3:  //紅
								pic = BitmapFactory.decodeResource(res, R.drawable.coloring_2_3);
								break;
						};
						canvas.drawBitmap(pic,70,270,p);
						break;
					case 2:  //上
						switch (area_color[i])
						{
							case 1:  //綠
								pic = BitmapFactory.decodeResource(res, R.drawable.coloring_3_1);
								break;
							case 2:  //黃
								pic = BitmapFactory.decodeResource(res, R.drawable.coloring_3_2);
								break;
							case 3:  //紅
								pic = BitmapFactory.decodeResource(res, R.drawable.coloring_3_3);
								break;
						};
						canvas.drawBitmap(pic,160,120,p);
						break;
					case 3:  //下
						switch (area_color[i])
						{
							case 1:  //綠
								pic = BitmapFactory.decodeResource(res, R.drawable.coloring_4_1);
								break;
							case 2:  //黃
								pic = BitmapFactory.decodeResource(res, R.drawable.coloring_4_2);
								break;
							case 3:  //紅
								pic = BitmapFactory.decodeResource(res, R.drawable.coloring_4_3);
								break;
						};
						canvas.drawBitmap(pic,170,260,p);
						break;
					case 4:  //右上
						switch (area_color[i])
						{
							case 1:  //綠
								pic = BitmapFactory.decodeResource(res, R.drawable.coloring_2_1);
								break;
							case 2:  //黃
								pic = BitmapFactory.decodeResource(res, R.drawable.coloring_2_2);
								break;
							case 3:  //紅
								pic = BitmapFactory.decodeResource(res, R.drawable.coloring_2_3);
								break;
						};
						canvas.drawBitmap(pic,240,180,p);
						break;
					case 5:  //右下
						switch (area_color[i])
						{
							case 1:  //綠
								pic = BitmapFactory.decodeResource(res, R.drawable.coloring_1_1);
								break;
							case 2:  //黃
								pic = BitmapFactory.decodeResource(res, R.drawable.coloring_1_2);
								break;
							case 3:  //紅
								pic = BitmapFactory.decodeResource(res, R.drawable.coloring_1_3);
								break;
						};
						canvas.drawBitmap(pic,240,270,p);
						break;

					default:  //表0，不用畫色
						break;
				};
			}
		}

	}

	private final Runnable timerRun1 = new Runnable()
	{
		public void run()
		{
			HandlerTime1.postDelayed(this, 1000);
			invalidate();// 更新畫面

		}
	};
}

