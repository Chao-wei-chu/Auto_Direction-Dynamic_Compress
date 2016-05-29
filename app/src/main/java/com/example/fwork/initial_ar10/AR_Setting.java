package com.example.fwork.initial_ar10;


		import android.app.Activity;
		import android.os.Bundle;
		import android.view.View;
		import android.widget.Button;
		import android.widget.SeekBar;
		import android.widget.TextView;
/**
 * 設定資訊
 *
 * @author You-Hsin, Chen(陳友信)
 *
 */
public class AR_Setting  extends Activity
{
	private SeekBar seekBar1,seekBar2;
	private TextView seekBarValue1,seekBarValue2;
	private Button seekbtn1,seekbtn2;

	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ar_setting);


		/**AR探測距離**/
		seekBar1 = (SeekBar)findViewById(R.id.seekBar1);
		seekBarValue1 = (TextView)findViewById(R.id.seekbar_text1);
		seekbtn1 = (Button) findViewById(R.id.seek_button1);

		seekBar1.setMax(9);  //20~200m
		seekBar1.setProgress(MainActivity.Detection_Distance/20);
		seekBarValue1.setText("AR探測距離:" + String.valueOf(MainActivity.Detection_Distance)+ "公尺");

		seekBar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
		{
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
			{
				seekBarValue1.setText("AR探測距離:" + String.valueOf((progress+1)*20) + "公尺");
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar)
			{}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar)
			{}
		});

		//開始
		seekbtn1.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				MainActivity.Detection_Distance = (seekBar1.getProgress()+1) *20;
				MainActivity.SettingReturnTest = true;
			}
		});

		/**地圖透明度**/
		seekBar2 = (SeekBar)findViewById(R.id.seekBar2);
		seekBarValue2 = (TextView)findViewById(R.id.seekbar_text2);
		seekbtn2 = (Button) findViewById(R.id.seek_button2);

		seekBar2.setMax(10);  //0.0~1.0
		seekBar2.setProgress( (int)(MainActivity.Map_Transparency * 10) );
		seekBarValue2.setText("地圖透明度:" + String.valueOf(MainActivity.Map_Transparency));

		seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
		{
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
			{
				seekBarValue2.setText("地圖透明度:" + String.valueOf((float)(progress)/10));
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {}
		});

		//開始
		seekbtn2.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				MainActivity.Map_Transparency = (float)((float)seekBar2.getProgress()/10);
				MainActivity.SettingReturnTest = true;
			}
		});
	}

}
