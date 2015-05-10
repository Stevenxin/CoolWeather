package com.cool.weather;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.cool.weather.R;

import com.cool.weather.adapter.MyAdapter;
import com.thinkland.juheapi.common.JsonCallBack;
import com.thinkland.juheapi.data.weather.WeatherData;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

public class CityActivity extends Activity {
	private ListView lv_city;
	private List<String> list;
	private Context context = CityActivity.this;
	private SearchView srv1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_city);

		initViews();
		getCities();

	}

	private void initViews() {
		findViewById(R.id.iv_back).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		lv_city = (ListView) findViewById(R.id.lv_city);
		srv1 = (SearchView) findViewById(R.id.srv1);
	}

	private void getCities() {
		WeatherData data = WeatherData.getInstance();
		list = new ArrayList<String>();
		data.getCities(new JsonCallBack() {

			@Override
			public void jsonLoaded(JSONObject json) {
				// TODO Auto-generated method stub
				try {
					int code = json.getInt("resultcode");
					int error_code = json.getInt("error_code");
					if (error_code == 0 && code == 200) {
						JSONArray resultArray = json.getJSONArray("result");
						Set<String> citySet = new HashSet<String>();
						for (int i = 0; i < resultArray.length(); i++) {
							String city = resultArray.getJSONObject(i)
									.getString("city");
							citySet.add(city);
						}
						list.addAll(citySet);

						MyAdapter adapter = new MyAdapter(context, list);
						lv_city.setAdapter(adapter);
						lv_city.setTextFilterEnabled(false);
						srv1.setSubmitButtonEnabled(false);

						// 设置查询效果
						srv1.setOnQueryTextListener(new OnQueryTextListener() {

							@Override
							public boolean onQueryTextSubmit(String query) {
								// TODO Auto-generated method stub
								return false;
							}

							@Override
							public boolean onQueryTextChange(String newText) {
								for (int i = 0; i < list.size(); i++) {
									boolean contains = list.get(i).contains(
											newText);
									if (!contains) {
										list.remove(i);
										i--;
									}
								}

								return false;
							}
						});
						adapter.notifyDataSetChanged();

						lv_city.setOnItemClickListener(new OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> arg0,
									View arg1, int arg2, long arg3) {
								Intent intent = new Intent();
								intent.putExtra("city", list.get(arg2));
								setResult(1, intent);
								finish();
							}
						});

					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

}
