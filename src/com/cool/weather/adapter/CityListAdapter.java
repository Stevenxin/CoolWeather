package com.cool.weather.adapter;

import java.util.List;

import com.cool.weather.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CityListAdapter extends BaseAdapter {

	public List<String> list;
	private LayoutInflater mInflater;
	private Context context;

	public CityListAdapter(Context context, List<String> list) {
		this.list = list;
		this.mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public String getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		View rowView = null;
		if (convertView == null) {
			rowView = mInflater.inflate(R.layout.item_city_list, null);
		} else {
			rowView = convertView;
		}

		TextView tv_city = (TextView) rowView.findViewById(R.id.tv_city);
		tv_city.setText(getItem(position));

		return rowView;
	}

}
