package com.oftly.oftly;

import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import com.oftly.oftly.asynctasks.ContactListUploaderTask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends Activity {	
	Context context;
    static Boolean wasMyOwnNumber;
    static Boolean workDone;
    final static int SMS_ROUNDTRIP_TIMOUT = 30000;
    static String phoneNumber;
    ListView listView;
    LinearLayout transparentLayer;
	ArrayList<CountryInfo> countryInfo;
	boolean listViewVisible = false;
    ProgressDialog progress;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		AssetManager assetManager = getBaseContext().getAssets();
        try {
            InputStream is = assetManager.open("countries.xml");
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser sp = spf.newSAXParser();
            XMLReader xr = sp.getXMLReader();

            CountriesXMLHandler countriesXMLHandler = new CountriesXMLHandler();
            xr.setContentHandler(countriesXMLHandler);
            InputSource inStream = new InputSource(is);
            xr.parse(inStream);

            countryInfo = countriesXMLHandler.getCountryList();
        } catch (Exception e) {
        	e.printStackTrace();
        }
        //requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		showRegistrationScreen();
		setTitle("Verify your phone number");
		//getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.registration_title);
	}

	private void showRegistrationScreen() {
		this.context = this;
		wasMyOwnNumber = false;
		workDone = false;
		setContentView(R.layout.activity_register);
		
		final Typeface typeface = Typeface.createFromAsset(getAssets(), "RobotoCondensed-Light.ttf");
		final Typeface typefaceRegular = Typeface.createFromAsset(getAssets(), "RobotoCondensed-Regular.ttf");
		LinearLayout layout = (LinearLayout)findViewById(R.id.fragment_get_phone_layout);

		((TextView)layout.findViewById(R.id.registration_country)).setTypeface(typeface);
		((EditText)layout.findViewById(R.id.registration_phone_number)).setTypeface(typeface);
		((TextView)layout.findViewById(R.id.registration_send_check_sms)).setTypeface(typeface);

		TextView textView = (TextView)findViewById(R.id.registration_send_check_sms);

		CountryInfo defaultCountryInfo = getDefaultCountryInfo();
		TextView phoneCodeView = (TextView)findViewById(R.id.registration_country);
		phoneCodeView.setText(getCountryText(defaultCountryInfo));
		
		EditText editText = (EditText)findViewById(R.id.registration_phone_number);
		editText.requestFocus();
		getWindow().setSoftInputMode(
			      WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
			
		DisplayMetrics displaymetrics = new DisplayMetrics();
		displaymetrics = context.getResources().getDisplayMetrics();
		      
		final int width = displaymetrics.widthPixels;
		final int height = displaymetrics.heightPixels;
		final float width_frac = 4.0f/5.0f;
		final float height_frac = 3.0f/4.0f;
      	textView = (TextView)findViewById(R.id.registration_country);
      	textView.setOnClickListener(new OnClickListener() {
      		@Override
      		public void onClick(View view) {
				RelativeLayout relativeLayout = (RelativeLayout)findViewById(R.id.register_outer);

				/* First, add transparent Layer */
				transparentLayer = new LinearLayout(context);
				transparentLayer.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 
																				LinearLayout.LayoutParams.MATCH_PARENT));
				transparentLayer.setBackgroundColor(Color.parseColor("#cc000000"));
				relativeLayout.addView(transparentLayer);
				
				/* Now, add the list view */
				listView = new ListView(context);
				listView.setBackgroundResource(R.drawable.country_list_rounded_corner);
				listView.setAdapter(new RegistrationCountryListAdapter(context, R.id.registration_country_list_item, 
						countryInfo));
				
				TextView textView = new TextView(context);
				textView.setText("Please select your country");
				textView.setTextColor(Color.parseColor("#3a8646"));
				textView.setTextSize(25);
				textView.setTypeface(typefaceRegular);
				textView.setPadding(18, 18, 18, 18);
				listView.addHeaderView(textView);
				
				listView.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?>adapter,View v, int position, long arg){
						TextView textView = (TextView)findViewById(R.id.registration_country);
						textView.setText((String)v.getTag());
						hideCountryList();
					}
				});
				int listViewWidth = (int)(width_frac * (float)width);
				int listViewHeight = (int)(height_frac * (float)height);

				float xOffsetFrac = (1 - width_frac) / 2; 
				float yOffsetFrac = (1 - height_frac) / 2;
				int listViewXOffset = (int)(xOffsetFrac * (float)width);
				int listViewYOffset = (int)(yOffsetFrac * (float)height);

				RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(listViewWidth, listViewHeight);
				rlp.setMargins(listViewXOffset, listViewYOffset, 0, 0);
				relativeLayout.addView(listView, rlp);
									
				listViewVisible = true;      	
			}
		});
	}
	
	@Override
	public void onBackPressed() {
		if (listViewVisible) {
			hideCountryList();
		} else {
			super.onBackPressed();
		}
	}
	private void hideCountryList() {
		RelativeLayout relativeLayout = (RelativeLayout)findViewById(R.id.register_outer);
		relativeLayout.removeView(listView);
		relativeLayout.removeView(transparentLayer);
		EditText editText = (EditText)findViewById(R.id.registration_phone_number);
		editText.requestFocus();

		listViewVisible = false;
	}
	private CountryInfo getDefaultCountryInfo() {
		TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
		String countryCode = tm.getSimCountryIso();
		for (int i = 0; i < countryInfo.size(); i++) {
			if (countryInfo.get(i).getCountryCode().equals(countryCode)) {
				return countryInfo.get(i);
			}
		}
		return countryInfo.get(0);
	}
	private static String getCountryText(CountryInfo countryInfo) {
		String countryName = countryInfo.getCountryName();
		String countryPhoneCode = countryInfo.getCountryPhoneCode();
		return countryName + "(" + countryPhoneCode + ")";
	}
    private class CheckOwnMobileNumber extends AsyncTask<String, Void, String> {
    	@Override
    	protected void onPreExecute() {
            progress = ProgressDialog.show(RegisterActivity.this, "","Checking Mobile Number...");
            progress.setIndeterminate(true);
            progress.getWindow().setLayout(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
            super.onPreExecute();
    	}

    	@Override
    	protected String doInBackground(String... params) {
    		String phoneNumber = ((EditText)findViewById(R.id.registration_phone_number)).getText().toString();
    		int waited = 0;
    		RegisterActivity.phoneNumber = phoneNumber;
            try {
                sendSMS(phoneNumber, phoneNumber);
            }
            catch(Exception ex) {
                Log.v("Exception: ", "" + ex);
            }
            while (!workDone && waited < 60) {
            	try {
            		waited += 1;
            		Thread.sleep(1000);
            	} catch (InterruptedException e) {
            		e.printStackTrace();
            	}
            }
            Log.v("VIVEK", "Just before returning, workDone = " + workDone);
            return null;
    	}
    	
    	@Override
    	protected void onPostExecute(String result) {
    		progress.dismiss();
    		if (wasMyOwnNumber) {
    			Toast.makeText(getApplicationContext(), "Thanks for confirming", Toast.LENGTH_LONG).show();
    			
    			EditText editText = (EditText)findViewById(R.id.registration_phone_number);
    			String phoneNumber = editText.getText().toString();
    			Log.v("VIVEK", "I am storing " + phoneNumber);
    			Util.setStoredValue(context, Util.PHONE_NUMBER, phoneNumber);
    			
    			/* Store his contact list for the first time. */
    			//new ContactListUploaderTask(context).execute();

        		Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        		startActivity(intent);
    		}
    		if (!workDone) {
    			Toast.makeText(getApplicationContext(), "Unable to confirm. Try again", Toast.LENGTH_LONG).show();    		
    		}
    		if (workDone && !wasMyOwnNumber) {
    			Toast.makeText(getApplicationContext(), "Numbers did not match. Try again", Toast.LENGTH_LONG).show();
    		}
    		super.onPostExecute(result);
    	}
    }
    private void sendSMS(String phoneNumber, String message) {
        SmsManager sms = SmsManager.getDefault();
        Log.v("VIVEK", "Sending SMS");
        sms.sendTextMessage(phoneNumber, "OFTLY", message, null, null);
    }
    public void sendSmsButtonClicked(View view) {
    	if (!Util.isNetworkAvailable(context)) {
			Toast.makeText(getApplicationContext(), "No network connection. Try later.", Toast.LENGTH_LONG).show();
			return;
    	}
    	
    	/*Util.setStoredValue(context, Util.PHONE_NUMBER, "9916133937");
		new ContactListUploaderTask(context).execute();
		Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
		startActivity(intent);*/
    	
		new CheckOwnMobileNumber().execute();
    }
}