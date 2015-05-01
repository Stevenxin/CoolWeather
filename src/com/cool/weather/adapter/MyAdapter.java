package com.cool.weather.adapter;

import java.util.List;

import com.cool.weather.*;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MyAdapter extends BaseAdapter {
	List<String> list;
	LayoutInflater mInflater;
	Context context;

	public MyAdapter(Context context, List<String> list) {
		this.context = context;
		this.list = list;
		this.mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	
	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		MyBaby baby = null;
		// 判断是不是第一次加载
		if (convertView == null) {
			// 视图填充器
			convertView = mInflater.inflate(R.layout.item_city_list, null);
			// 创建内部类对象
			baby = new MyBaby();
			// 初始化控件
			baby.viewHolderTextView = (TextView) convertView
					.findViewById(R.id.tv_city);
			// 加锁视图
			convertView.setTag(baby);
		} else {
			// 解锁视图
			baby = (MyBaby) convertView.getTag();
		}
		// 设置数据
		baby.viewHolderTextView.setText(list.get(position).toString());

		return convertView;
	}

	class MyBaby {
		TextView viewHolderTextView;
	}

//	// 强制刷新适配器数据
//	public void say() {
//		// 调用 刷新数据
//		notifyDataSetChanged();
//	}

}
