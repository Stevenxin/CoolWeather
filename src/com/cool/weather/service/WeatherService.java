package com.cool.weather.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.cool.weatherbean.FutureWeatherBean;
import com.cool.weatherbean.HoursWeatherBean;
import com.cool.weatherbean.PMBean;
import com.cool.weatherbean.WeatherBean;
import com.thinkland.juheapi.common.JsonCallBack;
import com.thinkland.juheapi.data.air.AirData;
import com.thinkland.juheapi.data.weather.WeatherData;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class WeatherService extends Service {

	private WeatherServiceBinder binder = new WeatherServiceBinder();
	private boolean isRunning = false;
	private int count = 0;
	private WeatherBean weatherBean;
	private List<HoursWeatherBean> list;
	private PMBean pmBean;
	private OnParserCallBack callBack;
	private String city;
	private static final int Repeat_msg = 0x01;

	public interface OnParserCallBack {
		public void OnParserComplete(List<HoursWeatherBean> list,
				PMBean pmBean, WeatherBean weatherBean);
	}

	public void setCallBack(OnParserCallBack callback) {
		this.callBack = callback;
	}

	public void removeCallBack() {
		callBack = null;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return binder;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		city = "�人";
		mHandler.sendEmptyMessage(Repeat_msg);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return super.onStartCommand(intent, flags, startId);
	}

	Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == Repeat_msg) {
				getCityWeather();
				sendEmptyMessageDelayed(Repeat_msg, 1800 * 1000);
			}
		};
	};

	/*
	 * serviec �ڲ���
	 */
	public class WeatherServiceBinder extends Binder {

		public WeatherService getService() {
			return WeatherService.this;

		}

	}

	/*
	 * ����json���ݵ��������
	 */
	private WeatherBean parseWeather(JSONObject json) {
		WeatherBean bean = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");// ��ʽ��ʱ��
		try {
			int code = json.getInt("resultcode");
			int error_code = json.getInt("error_code");
			Log.e("code", code + "--" + error_code);
			if (error_code == 0 && code == 200) {// code==200��ʾ���ʳɹ��ķ���ֵ
				JSONObject result = json.getJSONObject("result");
				bean = new WeatherBean();// ��װ����

				// Today�ؼ��ֽ���
				JSONObject todayJson = result.getJSONObject("today");
				String City = todayJson.getString("city");
				Log.e("parseWeather_City", City);
				bean.setCity(City);// ��������
				bean.setFelt_temp(todayJson.getString("comfort_index"));// ����¶�
				bean.setUv_index(todayJson.getString("uv_index"));// ����������ָ��
				String x = todayJson.getString("temperature");
				bean.setTemp(todayJson.getString("temperature"));
				Log.e("parseWeather_temperature", x);
				bean.setWeather_str(todayJson.getString("weather"));
				bean.setWeather_id(todayJson.getJSONObject("weather_id")
						.getString("fa"));
				Log.e("weather_id", todayJson.getJSONObject("weather_id")
						.getString("fa"));
				bean.setDressing_index(todayJson.getString("dressing_index"));// ����
				// sk�ؼ��ֽ���
				JSONObject skJson = result.getJSONObject("sk");
				bean.setWind(skJson.getString("wind_direction")
						+ skJson.getString("wind_strength"));// ���������ǿ��
				bean.setNow_temp(skJson.getString("temp"));// ���ڵ��¶�
				bean.setRelease(skJson.getString("time"));// ���ڵ�ʱ��
				bean.setHumidity(skJson.getString("humidity"));// ʪ��

				// future
				JSONArray futureArray = result.getJSONArray("future");
				Date date = new Date(System.currentTimeMillis());// ��ȡ��ǰʱ�������

				List<FutureWeatherBean> futurelist = new ArrayList<FutureWeatherBean>();

				for (int i = 0; i < futureArray.length(); i++) {
					JSONObject futureJson = futureArray.getJSONObject(i);
					FutureWeatherBean futurebean = new FutureWeatherBean();

					Date datef = sdf.parse(futureJson.getString("date"));// ��ʽ��ʱ��

					if (!datef.after(date)) {
						continue;
					}
					futurebean.setTemp(futureJson.getString("temperature"));
					futurebean.setWeek(futureJson.getString("week"));
					futurebean.setWeather_id(futureJson.getJSONObject(
							"weather_id").getString("fa"));

					futurelist.add(futurebean);
					if (futurelist.size() == 3) {
						break;
					}
				}
				bean.setFuturelist(futurelist);
			} else {
				Toast.makeText(getApplicationContext(), "WEATHER_ERROR",
						Toast.LENGTH_SHORT).show();

			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bean;

	}

	/*
	 * ����future3������
	 */
	private List<HoursWeatherBean> parseForecast3h(JSONObject json) {
		List<HoursWeatherBean> list = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
		Date date = new Date(System.currentTimeMillis());
		try {
			int code = json.getInt("resultcode");
			int error_code = json.getInt("error_code");
			Log.e("code", code + "--" + error_code);
			if (error_code == 0 && code == 200) {
				list = new ArrayList<HoursWeatherBean>();

				JSONArray resultArray = json.getJSONArray("result");

				for (int i = 0; i < resultArray.length(); i++) {
					JSONObject hourJson = resultArray.getJSONObject(i);
					Log.e("parseForecast3h", hourJson.getString("efdate"));
					Date hdate = sdf.parse(hourJson.getString("sfdate"));
					if (!hdate.after(date)) {
						continue;
					}
					HoursWeatherBean bean = new HoursWeatherBean();
					bean.setWeatherid(hourJson.getString("weatherid"));
					Log.e("setWeather_id", hourJson.getString("weatherid"));
					bean.setTemp(hourJson.getString("temp1"));

					Calendar c = Calendar.getInstance();// ��װʱ������
					c.setTime(hdate);
					bean.setTime(c.get(Calendar.HOUR_OF_DAY) + "");
					list.add(bean);
					if (list.size() == 5) {
						break;
					}

				}
			} else {
				Toast.makeText(getApplicationContext(), "HOUR_ERROR",
						Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	/*
	 * ����PMjson
	 */
	private PMBean parserPM(JSONObject json) {
		PMBean bean = null;
		try {
			int code = json.getInt("resultcode");
			int error_code = json.getInt("error_code");
			if (error_code == 0 && code == 200) {
				bean = new PMBean();
				JSONObject pmJSON1 = json.getJSONArray("result")
						.getJSONObject(0).getJSONObject("citynow");
				bean.setAqi(pmJSON1.getString("AQI"));
				bean.setQuality(pmJSON1.getString("quality"));

			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return bean;

	}
	
	public void getCityWeather(String city){
	this.city=city;
	getCityWeather();
	}
	

	/*
	 * ��ʼ���ɳ�������
	 */
	public void getCityWeather() {

		if (isRunning) {
			return;
		}
		isRunning = true;
		count = 0;
		WeatherData weatherData = WeatherData.getInstance();

		weatherData.getByCitys(city, 2, new JsonCallBack() {

			@Override
			public void jsonLoaded(JSONObject arg0) {
				// Log.e("WeatherActivity", arg0.toString());
				count++;
				weatherBean = parseWeather(arg0);

				if (weatherBean != null) {
					// setWeatherViews(bean);
				}
				if (count == 3) {
					// mPullToRefreshScrollView.onRefreshComplete();
					if (callBack != null) {
						callBack.OnParserComplete(list, pmBean, weatherBean);
					}
					isRunning = false;
				}
			}
		});

		// ÿСʱ�������
		weatherData.getForecast3h(city, new JsonCallBack() {

			@Override
			public void jsonLoaded(JSONObject arg0) {
				Log.e("HoursWeatherBean", arg0.toString());
				list = parseForecast3h(arg0);
				count++;

				if (list != null && list.size() >= 5) {
					// setHourViews(list);

				}
				if (count == 3) {
					// mPullToRefreshScrollView.onRefreshComplete();
					if (callBack != null) {
						callBack.OnParserComplete(list, pmBean, weatherBean);
					}
					isRunning = false;
				}
			}
		});

		// ��������
		AirData airData = AirData.getInstance();
		airData.cityAir(city, new JsonCallBack() {

			@Override
			public void jsonLoaded(JSONObject arg0) {
				// TODO Auto-generated method stub
				pmBean = parserPM(arg0);
				count++;
				if (pmBean != null) {
					// setPMView(pmBean);
				}
				if (count == 3) {
					if (callBack != null) {
						callBack.OnParserComplete(list, pmBean, weatherBean);
					}
					isRunning = false;
				}
			}
		});

	}

}
