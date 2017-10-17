package com.oftly.oftly;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.CallLog;
import android.util.Log;

public class Util {
	public static final String INVALID = "invalid";
	public static final String PHONE_NUMBER = "phone_number";
	public static final String MAPPING_STORED = "mapping_stored";
	public static final String REG_ID = "registration_id";
	public static final String CONTACT_LIST = "contact_list";
	public static final String TRUE = "true";
	public static final String FALSE = "false";

	public static String getReadableCallTime(long callMillis) {
		long currMillis = System.currentTimeMillis();
		long diffMillis = currMillis - callMillis;
		
		if (diffMillis < 1000 * 60) {
			return Long.valueOf(diffMillis / 1000) + " sec ago";
		}
		if (diffMillis < 1000 * 60 * 30) {
			return Long.valueOf(diffMillis / (1000 * 60)) + " min ago";
		}
		
		/*if (diffMillis < 1000 * 60 * 60 * 4) {
			return Long.valueOf(diffMillis / (1000 * 60 * 60)) + " hr ago";			
		}*/

		Date callTime = new Date(callMillis);
		Date currTime = new Date();

		SimpleDateFormat dayFormat = new SimpleDateFormat("d");
		int currDay = Integer.valueOf(dayFormat.format(currTime));
		int callDay = Integer.valueOf(dayFormat.format(callTime));
		
		if (currDay == callDay || currDay - callDay == 1) {
			String prefix;
			if (currDay == callDay) {
				prefix = "Today";
			} else {
				prefix = "Yesterday";
			}
			SimpleDateFormat dateFormat = new SimpleDateFormat("h:mm aaa");
			return prefix + " " + dateFormat.format(callTime);
		}
		SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, h:mm aaa");
		return dateFormat.format(callTime);
	}
	
	public static String convertSecToString(int d) {
		if (d == 0) {
			return "0 secs";
		}
		if (d < 60) {
			return pluralize(d, "sec");
		} else if (d < 3600) {
			return pluralize(d / 60, "min")  + 
					pluralize(d % 60, "sec");
		} else {
			return pluralize(d / 3600 ,"hr") + 
					pluralize((d % 3600) / 60, "min") + 
					pluralize(d % 60, "sec");
		}
	}
	private static String pluralize(int i, String base) {
		if (i == 0) {
			return "";
		}
		if (i == 1) {
			return "1 " + base + " ";
		}
		return String.valueOf(i) + " " + base + "s ";
	}
	public static int getCallTypeImageResourceId(int type) {
		if (type == CallLog.Calls.INCOMING_TYPE) {
			return R.drawable.incoming_purple;
		} else if (type == CallLog.Calls.OUTGOING_TYPE) {
			return R.drawable.outgoing_green;
		} else if (type == CallLog.Calls.MISSED_TYPE) {
			return R.drawable.missed_red;
		} else {
			return 0;
		}

	}
	/* Canonical phone number is not meant to be actual number using
	 * which you can make a call. It just means that if c(a) == c(b)
	 * then a and b are same phone numbers.
	 * 
	 * So, for indexing a dict you can use canonical number, but don't
	 * use it in call details, or to dial someone.
	 */
	public static String getCanonicalPhoneNumber(String phone) {
		phone = phone.replace(" ", "");
		phone = phone.replace("-", "");
		phone = phone.replace("(", "");
		phone = phone.replace(")", "");
		if (phone.length() < 10) {
			return phone;
		} else {
			return phone.substring(phone.length() - 10);
		}
	}
	public static String getLast10Chars(String str) {
		if (str == null) {
			return null;
		}
		if (str.length() < 10) {
			return str;
		}
		return str.substring(str.length() - 10);
	}
	public static int getNumBlocks(int numContacts) {
		return numContacts / 6 + 1;
	}
	public static String getDisplayableNumber(String str) {
		if (str.startsWith("*") || str.startsWith("#")) {
			return str;
		}
		int len = str.length();
		if (len <= 3) {
			return str;
		} else if (len <= 6) {
			return str.substring(0, 3) + " " + str.substring(3);
		} else if (len <= 10) {
			return str.substring(0, 3) + " " + str.substring(3, 6) + " " + str.substring(6); 
		} else {
			return str;
		}
	}
	public static String getStoredValue(Context context, String key) {
		SharedPreferences prefs = context.getSharedPreferences("OFTLY", Context.MODE_PRIVATE);
		String value = prefs.getString(key, INVALID);
		return value;
	}
	public static void setStoredValue(Context context, String key, String value) {
		SharedPreferences prefs = context.getSharedPreferences("OFTLY", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(key, value);
		editor.commit();
	}
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivityManager = 
				(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
	public static void Log(String tag, String message) {
		Log.v(tag, message);
	}
	public static void Log(String message) {
		Log.v("VIVEK", message);
	}
}
