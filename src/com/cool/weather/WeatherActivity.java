package com.cool.weather;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.youmi.android.AdManager;
import net.youmi.android.banner.AdSize;
import net.youmi.android.banner.AdView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.cool.weather.R;
import com.cool.weather.service.WeatherService;
import com.cool.weather.service.WeatherService.OnParserCallBack;
import com.cool.weather.service.WeatherService.WeatherServiceBinder;
import com.cool.weather.swiperefresh.PullToRefreshBase;
import com.cool.weather.swiperefresh.PullToRefreshScrollView;
import com.cool.weather.swiperefresh.PullToRefreshBase.OnRefreshListener;
import com.cool.weatherbean.FutureWeatherBean;
import com.cool.weatherbean.HoursWeatherBean;
import com.cool.weatherbean.PMBean;
import com.cool.weatherbean.WeatherBean;
import com.thinkland.juheapi.common.JsonCallBack;
import com.thinkland.juheapi.data.air.AirData;
import com.thinkland.juheapi.data.weather.WeatherData;

import android.R.integer;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class WeatherActivity extends Activity {

	private PullToRefreshScrollView mPullToRefreshScrollView;
	private ScrollView mScrollView;
	private Context mContext;
	private TextView tv_city,// 城市
			tv_release,// 发布时间
			tv_now_weather,// 天气
			tv_today_temp,// 温度
			tv_now_temp,// 当前温度
			tv_aqi,// 空气质量指数
			tv_quality,// 空气质量
			tv_next_three,// 3小时
			tv_next_six,// 6小时
			tv_next_nine,// 9小时
			tv_next_twelve,// 12小时
			tv_next_fifteen,// 15小时
			tv_next_three_temp,// 3小时温度
			tv_next_six_temp,// 6小时温度
			tv_next_nine_temp,// 9小时温度
			tv_next_twelve_temp,// 12小时温度
			tv_next_fifteen_temp,// 15小时温度
			tv_today_temp_a,// 今天温度a
			tv_today_temp_b,// 今天温度b
			tv_tommorrow,// 明天
			tv_tommorrow_temp_a,// 明天温度a
			tv_tommorrow_temp_b,// 明天温度b
			tv_thirdday,// 第三天
			tv_thirdday_temp_a,// 第三天温度a
			tv_thirdday_temp_b,// 第三天温度b
			tv_fourthday,// 第四天
			tv_fourthday_temp_a,// 第四天温度a
			tv_fourthday_temp_b,// 第四天温度b
			tv_humidity,// 湿度
			tv_wind, tv_uv_index,// 紫外线指数
			tv_dressing_index,// 穿衣指数
			tv_felt_air_temp;// 体感温度

	private ImageView iv_now_weather,// 现在
			iv_next_three,// 3小时
			iv_next_six,// 6小时
			iv_next_nine,// 9小时
			iv_next_twelve,// 12小时
			iv_next_fifteen,// 15小时
			iv_today_weather,// 今天
			iv_tommorrow_weather,// 明天
			iv_thirdday_weather,// 第三天
			iv_fourthday_weather;// 第四天

	private RelativeLayout rl_city;

	private Context context = WeatherActivity.this;
	private WeatherService mservice;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_weather);
		mContext = this;

		init();// 初始化
		binderfService();// 进行绑定

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub

		unbindService(connection);
		super.onDestroy();
	}

	private void binderfService() {
		Intent intent = new Intent(context, WeatherService.class);
		startService(intent);
		bindService(intent, connection, Context.BIND_AUTO_CREATE);

	}

	ServiceConnection connection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			mservice.removeCallBack();

		}

		@Override
		public void onServiceConnected(ComponentName arg0, IBinder arg1) {
			mservice = ((WeatherServiceBinder) arg1).getService();
			mservice.setCallBack(new OnParserCallBack() {

				@Override
				public void OnParserComplete(List<HoursWeatherBean> list,
						PMBean pmBean, WeatherBean weatherBean) {
					mPullToRefreshScrollView.onRefreshComplete();

					if (list != null && list.size() >= 5) {
						setHourViews(list);
					}
					if (pmBean != null) {
						setPMView(pmBean);
					}
					if (weatherBean != null) {
						setWeatherViews(weatherBean);
					}

				}
			});
			mservice.getCityWeather();

		}
	};

	private void setWeatherViews(WeatherBean bean) {
		tv_felt_air_temp.setText(bean.getFelt_temp());
		tv_city.setText(bean.getCity());// 城市
		tv_release.setText(bean.getRelease() + "发布");// 现在的时间
		iv_now_weather.setImageResource(getResources().getIdentifier(
				"d" + bean.getWeather_id(), "drawable", "com.cool.weather"));// 现在天气的图标
		tv_now_weather.setText(bean.getWeather_str());// 现在的天气文字说明

		String[] tempArr1 = bean.getTemp().split("~");
		Log.e("tempArr1", tempArr1[0] + tempArr1[1]);
		String temp_str_a = tempArr1[1].substring(0, tempArr1[1].indexOf("℃"));
		String temp_str_b = tempArr1[0].substring(0, tempArr1[0].indexOf("℃"));
		// 分割数据15℃~31℃-15C-31C-15-31
		tv_today_temp.setText("↑ " + temp_str_a + "°   ↓" + temp_str_b + "°");
		tv_now_temp.setText(bean.getNow_temp() + " °");
		iv_today_weather.setImageResource(getResources().getIdentifier(
				"d" + bean.getWeather_id(), "drawable", "com.cool.weather"));

		tv_today_temp_a.setText(temp_str_a + "°");
		tv_today_temp_b.setText(temp_str_b + "°");
		List<FutureWeatherBean> futureList = bean.getFuturelist();

		if (futureList != null && futureList.size() == 3) {
			setFutureViews(tv_tommorrow, iv_tommorrow_weather,
					tv_tommorrow_temp_a, tv_tommorrow_temp_b, futureList.get(0));
			setFutureViews(tv_thirdday, iv_thirdday_weather,
					tv_thirdday_temp_a, tv_thirdday_temp_b, futureList.get(1));
			setFutureViews(tv_fourthday, iv_fourthday_weather,
					tv_fourthday_temp_a, tv_fourthday_temp_b, futureList.get(2));
		}
		tv_humidity.setText(bean.getHumidity());
		tv_dressing_index.setText(bean.getDressing_index());
		tv_uv_index.setText(bean.getUv_index());
		tv_wind.setText(bean.getWind());

	}

	private void setHourViews(List<HoursWeatherBean> list) {
		setHourData(tv_next_three, iv_next_three, tv_next_three_temp,
				list.get(0));
		setHourData(tv_next_six, iv_next_six, tv_next_six_temp, list.get(1));
		setHourData(tv_next_nine, iv_next_nine, tv_next_nine_temp, list.get(2));
		setHourData(tv_next_twelve, iv_next_twelve, tv_next_twelve_temp,
				list.get(3));
		setHourData(tv_next_fifteen, iv_next_fifteen, tv_next_fifteen_temp,
				list.get(4));
	}

	private void setHourData(TextView tv_hour, ImageView iv_weather,
			TextView tv_temp, HoursWeatherBean bean) {
		String prefixStr = null;
		int time = Integer.valueOf(bean.getTime());
		if (time >= 6 && time <= 18) {
			prefixStr = "d";
		} else {
			prefixStr = "n";
		}

		tv_hour.setText(bean.getTime() + "时");
		iv_weather.setImageResource(getResources()
				.getIdentifier(prefixStr + bean.getWeatherid(), "drawable",
						"com.cool.weather"));
		tv_temp.setText(bean.getTemp() + "°");

	}

	private void setFutureViews(TextView tv_week, ImageView iv_weather,
			TextView tv_temp_a, TextView tv_temp_b, FutureWeatherBean bean) {
		tv_week.setText(bean.getWeek());
		iv_weather.setImageResource(getResources().getIdentifier(
				"d" + bean.getWeather_id(), "drawable", "com.cool.weather"));
		String[] tempArr = bean.getTemp().split("~");
		String temp_str_a = tempArr[1].substring(0, tempArr[1].indexOf("℃"));
		String temp_str_b = tempArr[0].substring(0, tempArr[0].indexOf("℃"));
		tv_temp_a.setText(temp_str_a + "°");
		tv_temp_b.setText(temp_str_b + "°");

	}

	private void setPMView(PMBean bean) {
		tv_aqi.setText(bean.getAqi());
		tv_quality.setText(bean.getQuality());
	}

	private void init() {
		mPullToRefreshScrollView = (PullToRefreshScrollView) findViewById(R.id.pull_refresh_scrollview);

		mPullToRefreshScrollView
				.setOnRefreshListener(new OnRefreshListener<ScrollView>() {

					@Override
					public void onRefresh(
							PullToRefreshBase<ScrollView> refreshView) {
						mservice.getCityWeather();

					}
				});

		mScrollView = mPullToRefreshScrollView.getRefreshableView();// 第三方控件获取到

		rl_city = (RelativeLayout) findViewById(R.id.rl_city);
		rl_city.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivityForResult(
						new Intent(mContext, CityActivity.class), 1);

			}
		});

		tv_city = (TextView) findViewById(R.id.tv_city);
		tv_release = (TextView) findViewById(R.id.tv_release);
		tv_now_weather = (TextView) findViewById(R.id.tv_now_weather);
		tv_today_temp = (TextView) findViewById(R.id.tv_today_temp);
		tv_now_temp = (TextView) findViewById(R.id.tv_now_temp);
		tv_aqi = (TextView) findViewById(R.id.tv_aqi);
		tv_quality = (TextView) findViewById(R.id.tv_quality);
		tv_next_three = (TextView) findViewById(R.id.tv_next_three);
		tv_next_six = (TextView) findViewById(R.id.tv_next_six);
		tv_next_nine = (TextView) findViewById(R.id.tv_next_nine);
		tv_next_twelve = (TextView) findViewById(R.id.tv_next_twelve);
		tv_next_fifteen = (TextView) findViewById(R.id.tv_next_fifteen);
		tv_next_three_temp = (TextView) findViewById(R.id.tv_next_three_temp);
		tv_next_six_temp = (TextView) findViewById(R.id.tv_next_six_temp);
		tv_next_nine_temp = (TextView) findViewById(R.id.tv_next_nine_temp);
		tv_next_twelve_temp = (TextView) findViewById(R.id.tv_next_twelve_temp);
		tv_next_fifteen_temp = (TextView) findViewById(R.id.tv_next_fifteen_temp);
		tv_today_temp_a = (TextView) findViewById(R.id.tv_today_temp_a);
		tv_today_temp_b = (TextView) findViewById(R.id.tv_today_temp_b);
		tv_tommorrow = (TextView) findViewById(R.id.tv_tommorrow);
		tv_tommorrow_temp_a = (TextView) findViewById(R.id.tv_tommorrow_temp_a);
		tv_tommorrow_temp_b = (TextView) findViewById(R.id.tv_tommorrow_temp_b);
		tv_thirdday = (TextView) findViewById(R.id.tv_thirdday);
		tv_thirdday_temp_a = (TextView) findViewById(R.id.tv_thirdday_temp_a);
		tv_thirdday_temp_b = (TextView) findViewById(R.id.tv_thirdday_temp_b);
		tv_fourthday = (TextView) findViewById(R.id.tv_fourthday);
		tv_fourthday_temp_a = (TextView) findViewById(R.id.tv_fourthday_temp_a);
		tv_fourthday_temp_b = (TextView) findViewById(R.id.tv_fourthday_temp_b);
		tv_humidity = (TextView) findViewById(R.id.tv_humidity);
		tv_wind = (TextView) findViewById(R.id.tv_wind);
		tv_uv_index = (TextView) findViewById(R.id.tv_uv_index);
		tv_dressing_index = (TextView) findViewById(R.id.tv_dressing_index);

		iv_now_weather = (ImageView) findViewById(R.id.iv_now_weather);
		iv_next_three = (ImageView) findViewById(R.id.iv_next_three);
		iv_next_six = (ImageView) findViewById(R.id.iv_next_six);
		iv_next_nine = (ImageView) findViewById(R.id.iv_next_nine);
		iv_next_twelve = (ImageView) findViewById(R.id.iv_next_twelve);
		iv_next_fifteen = (ImageView) findViewById(R.id.iv_next_fifteen);
		iv_today_weather = (ImageView) findViewById(R.id.iv_today_weather);
		iv_tommorrow_weather = (ImageView) findViewById(R.id.iv_tommorrow_weather);
		iv_thirdday_weather = (ImageView) findViewById(R.id.iv_thirdday_weather);
		iv_fourthday_weather = (ImageView) findViewById(R.id.iv_fourthday_weather);
		tv_felt_air_temp = (TextView) findViewById(R.id.tv_felt_air_temp);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub

		if (requestCode == 1 && resultCode == 1) {
			String city = data.getStringExtra("city");
			mservice.getCityWeather(city);
		}

	}

	private long mExitTime;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {

			if ((System.currentTimeMillis() - mExitTime) > 2000) // System.currentTimeMillis()无论何时调用，肯定大于2000
			{
				Toast.makeText(getApplicationContext(), "再按一次退出程序",
						Toast.LENGTH_SHORT).show();
				mExitTime = System.currentTimeMillis();
			} else {
				finish();
				System.exit(0);
			}

			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
