package com.cool.weather;

//import com.thinkland.sdk.android.JuheSDKInitializer;
import com.thinkland.juheapi.common.CommonFun;
import com.thinkland.juheapi.data.weather.WeatherData;
import android.app.Application;

public class WeatherApplication extends Application {

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		CommonFun.initialize(getApplicationContext());
//		JuheSDKInitializer.initialize(getApplicationContext());
	}

}
