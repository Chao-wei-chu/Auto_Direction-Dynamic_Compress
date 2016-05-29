package com.example.fwork.initial_ar10;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Handler;
import android.view.View;

/**
 * 天氣顯示
 *
 * @author YKL(葉冠麟)
 *
 */
public class Weather_show  extends View
{
    private Handler HandlerTime1 = new Handler();  //重畫

    public Weather_show(Context context)
    {
        super(context);
        HandlerTime1.postDelayed(timerRun1, 1000);
    }

    public void onDraw(Canvas canvas)
    {
        String weather = MainActivity.weather;

        super.onDraw(canvas);
        Paint p = new Paint();

        p.setColor(Color.BLUE);
        p.setTypeface(Typeface.DEFAULT_BOLD);
        p.setTextSize(100);


        p.setTextSize(45);
        canvas.drawText(weather, 190, 120, p);

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
