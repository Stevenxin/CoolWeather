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
		// �ж��ǲ��ǵ�һ�μ���
		if (convertView == null) {
			// ��ͼ�����
			convertView = mInflater.inflate(R.layout.item_city_list, null);
			// �����ڲ������
			baby = new MyBaby();
			// ��ʼ���ؼ�
			baby.viewHolderTextView = (TextView) convertView
					.findViewById(R.id.tv_city);
			// ������ͼ
			convertView.setTag(baby);
		} else {
			// ������ͼ
			baby = (MyBaby) convertView.getTag();
		}
		// ��������
		baby.viewHolderTextView.setText(list.get(position).toString());

		return convertView;
	}

	class MyBaby {
		TextView viewHolderTextView;
	}

//	// ǿ��ˢ������������
//	public void say() {
//		// ���� ˢ������
//		notifyDataSetChanged();
//	}

}
