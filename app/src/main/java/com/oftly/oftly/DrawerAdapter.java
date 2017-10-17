package com.oftly.oftly;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class DrawerAdapter extends BaseAdapter {
	private String[] items = {"My contacts", "Show call list"};
	private LayoutInflater inflater;
	private Typeface typeface;
	Context context;
	
	DrawerAdapter(Typeface typeface, Context context) {
		this.context = context;
		this.typeface = typeface;
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	@Override
	public int getCount() {
		return items.length;
	}

	@Override
	public Object getItem(int position) {
		return items[position];
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView textView = (TextView)inflater.inflate(R.layout.drawer_list_item, null);
		textView.setText(items[position]);
		textView.setTypeface(typeface);
		textView.setTextColor(context.getResources().getColor(R.color.navigation_list_text_color));
		return textView;
	}

}
