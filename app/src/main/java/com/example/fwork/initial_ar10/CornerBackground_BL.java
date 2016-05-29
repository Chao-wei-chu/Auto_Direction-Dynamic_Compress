package com.example.fwork.initial_ar10;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Handler;
import android.view.View;

public class CornerBackground_BL extends View
{
    MainActivity app;
    private Resources res;
    //private Bitmap pic;   //直接指定
    private Handler HandlerTime1 = new Handler();  //我的Timer  重畫

    public CornerBackground_BL(Context context)
    {
        super(context);
        app = (MainActivity) context;
        res = getResources();
        // pic = BitmapFactory.decodeResource(res, R.drawable.car_m);

        HandlerTime1.postDelayed(timerRun1, 750);
    }

    public void onDraw(Canvas canvas)
    {
        float RADIUS = 150;
        float originX = app.dWindow.getWidth() - RADIUS, originY = RADIUS;

        Paint radarPaint = new Paint();
        //radarPaint.setColor(Color.argb(100, 45, 230, 155));
        radarPaint.setColor(Color.argb(100, 131, 131, 255));
        // radarPaint.setStyle(Paint.Style.FILL);
        radarPaint.setAntiAlias(true);// 消鋸齒
        Path path1=new Path();
        path1.moveTo(0, 200);
        for(int i=200;i>=0;i--) {
            path1.lineTo(400- ((i * i) / 100),200- i);
        }
        path1.close();//封闭
        canvas.drawPath(path1, radarPaint);


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