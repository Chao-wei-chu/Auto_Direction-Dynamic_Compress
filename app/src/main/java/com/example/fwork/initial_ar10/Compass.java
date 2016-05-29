package com.example.fwork.initial_ar10;


        import android.content.Context;
        import android.content.res.Resources;
        import android.graphics.Bitmap;
        import android.graphics.BitmapFactory;
        import android.graphics.Canvas;
        import android.graphics.Color;
        import android.graphics.Paint;
        import android.graphics.Paint.Style;
        import android.util.Log;
        import android.widget.ImageView;

/**
 * 提供會根據sensor轉動雷達上指南針功能。
 * 只有放圖片指針!
 **/

public class Compass extends ImageView {

  MainActivity app;

  int direction = 0;
  public Compass(Context context) {
    super(context);
    this.setImageResource(R.drawable.compass);  //圖片
  }

  @Override
  public void onDraw(Canvas canvas)
  {
    int height = this.getHeight();
    int width = this.getWidth();
    //放四指針之圖片
    canvas.rotate(direction, width / 2, height / 2);
      Paint radarPaint = new Paint();
      radarPaint.setStyle(Paint.Style.FILL);
      for(int i=0;i<6;i++){MainActivity.area_color[i]=0;}//初始化下方方位儀
      for(int i=6;i<MainActivity.KL_Carnumber-5;i++)
      {
          double t=Math.pow(Math.pow(MainActivity.KL_Othercar_X[i],2)+Math.pow(MainActivity.KL_Othercar_Y[i],2),0.5);
          if(t<=30)
          {
              double ag=Angle((double) MainActivity.KL_Othercar_X[i], (double) MainActivity.KL_Othercar_Y[i]);//計算角度
              Set_6_direction(ag,3);
              Log.d("222","ag"+ag );
              radarPaint.setColor(Color.argb(150, 200, 0, 0));  //紅
          }
          else if(t<=60)
          {
              double ag=Angle((double) MainActivity.KL_Othercar_X[i], (double) MainActivity.KL_Othercar_Y[i]);//計算角度
              Set_6_direction(ag,2);
              radarPaint.setColor(Color.argb(150, 250,250,0));  //黃
          }
          else
          {
              double ag=Angle((double) MainActivity.KL_Othercar_X[i], (double) MainActivity.KL_Othercar_Y[i]);//計算角度
              Set_6_direction(ag,1);
              radarPaint.setColor(Color.argb(150, 80,250,80));  //綠
          }
          canvas.drawCircle(150 + (float) MainActivity.KL_Othercar_X[i], 150 - (float) MainActivity.KL_Othercar_Y[i], 12, radarPaint);
      }
    super.onDraw(canvas);
  }

  public void setDirection(int direction) {
    this.direction = direction;
    this.invalidate();
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
    private double Angle(double a,double b)
    {
        double ag=computeAzimuth(0.0,0.0,a,b);
        ag=ag+MainActivity.mbearing;
        if(ag>=360.0){ag=ag-360;}
        if(ag<=-360){ag=ag+360;}
        return ag;
    }
    private void Set_6_direction(double a,int b)
    {
        if(a>=0 && a<=60)//右前
        {
            if(MainActivity.area_color[4]>b){}
            else {
                MainActivity.area_color[4] = b;
            }
        }
        else if(a>60 && a<120)//正前
        {
            if(MainActivity.area_color[2]>b){}
            else {
                MainActivity.area_color[2] = b;
            }
        }
        else if(a>=120 && a<=180)//左前
        {
            if(MainActivity.area_color[0]>b){}
            else {
                MainActivity.area_color[0] = b;
            }
        }
        else if(a>180 && a<=240)//左後
        {
            if(MainActivity.area_color[1]>b){}
            else {
                MainActivity.area_color[1] = b;
            }
        }
        else if(a>240 && a<300)//正後
        {
            if(MainActivity.area_color[3]>b){}
            else {
                MainActivity.area_color[3] = b;
            }
        }
        else if(a>=300 && a<360)//右後
        {
            if(MainActivity.area_color[5]>b){}
            else {
                MainActivity.area_color[5] = b;
            }
        }
    }

}

