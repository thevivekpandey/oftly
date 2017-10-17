package com.oftly.oftly;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class SuperActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		final Class<? extends Activity> activityClass;
		
		/*Log.v("VIVEK", "I got stored phone number as " + Util.getStoredValue(this, Util.PHONE_NUMBER));
		if (Util.getStoredValue(this, Util.PHONE_NUMBER).equals(Util.INVALID))  {
			activityClass = RegisterActivity.class;
		} else {
			activityClass = MainActivity.class;
		}*/
		activityClass = MainActivity.class;

		Intent newActivity = new Intent(this, activityClass);
		Log.v("VIVEK", "Starting activity " + activityClass.getClass().getSimpleName());
		startActivity(newActivity);
	}
}
