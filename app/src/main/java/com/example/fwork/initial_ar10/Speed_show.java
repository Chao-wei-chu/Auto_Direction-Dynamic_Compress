package com.example.fwork.initial_ar10;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Handler;
import android.view.View;

/**
 * 時速顯示
 *
 * @author You-Hsin, Chen(陳友信)
 *
 */
public class Speed_show  extends View
{
	private Handler HandlerTime1 = new Handler();  //重畫

	public Speed_show(Context context)
	{
		super(context);
		HandlerTime1.postDelayed(timerRun1, 1000);
	}

	public void onDraw(Canvas canvas)
	{
		int speed = (int)MainActivity.My_Speed_KM;

		super.onDraw(canvas);
		Paint p = new Paint();

		p.setColor(Color.argb(150,255,0,0));
		p.setTypeface(Typeface.DEFAULT_BOLD);
		p.setTextSize(100);

		if(speed >99)
		{
			canvas.drawText(String.valueOf(speed), 10, 100, p);
		}
		else
		{
			canvas.drawText(String.valueOf(speed), 60, 100, p);
		}

		p.setTextSize(40);
		canvas.drawText("Km/h", 200, 120, p);

		//invalidate();// 更新畫面
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
