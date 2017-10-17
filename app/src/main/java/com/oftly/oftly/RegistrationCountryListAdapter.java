package com.oftly.oftly;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class RegistrationCountryListAdapter extends ArrayAdapter<CountryInfo>{
	private LayoutInflater inflater;
	ArrayList<CountryInfo>countryInfo;
	Typeface typefaceLight, typefaceRegular;
	public RegistrationCountryListAdapter(Context context, int resourceId, ArrayList<CountryInfo>countryInfo) {
		super(context ,resourceId, countryInfo);
		this.countryInfo = countryInfo;
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		typefaceLight = Typeface.createFromAsset(context.getAssets(), "RobotoCondensed-Light.ttf");
		typefaceRegular = Typeface.createFromAsset(context.getAssets(), "RobotoCondensed-Regular.ttf");
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = inflater.inflate(R.layout.registration_country_list_item, parent, false);
		
		String countryName = countryInfo.get(position).getCountryName();
		String countryPhoneCode = countryInfo.get(position).getCountryPhoneCode();
		
		TextView countryView = (TextView)convertView.findViewById(R.id.registration_country_list_item_country_name);
		countryView.setTypeface(typefaceRegular);
		countryView.setText(countryName);

		TextView codeView = (TextView)convertView.findViewById(R.id.registration_country_list_item_country_code);
		codeView.setTypeface(typefaceLight);
		codeView.setGravity(Gravity.RIGHT);
		codeView.setText("+" + countryPhoneCode);

		convertView.setTag(countryName + "(" + countryPhoneCode + ")");
		return convertView;
	}
}
