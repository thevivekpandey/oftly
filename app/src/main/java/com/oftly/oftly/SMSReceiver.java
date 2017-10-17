package com.oftly.oftly;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsMessage;
import android.util.Log;

public class SMSReceiver extends BroadcastReceiver {
	private static final String ACTION_SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
	Context mContext;
	private Intent mIntent;
	static String address, str = null;
	boolean isSame;

	@Override
	public void onReceive(Context context, Intent intent) {
	    Log.v("VIVEK", "RECEIVED MESSAGE");
	    mContext = context;
	    mIntent = intent;
	    String action = intent.getAction();       
        SmsMessage[] msgs = getMessagesFromIntent(mIntent);
        if (msgs != null) {
            for (int i = 0; i < msgs.length; i++) {
                address = msgs[i].getOriginatingAddress();
                str = msgs[i].getMessageBody().toString();
            }
        }
        Log.v("VIVEK", "Originating address: sender: " + address);
        Log.v("VIVEK", "Message from sender " + str);
        isSame = PhoneNumberUtils.compare(str, RegisterActivity.phoneNumber);
        Log.v("VIVEK", "Main phone number is " + RegisterActivity.phoneNumber);
        Log.v("VIVEK", "Comparison: Yes this is true " + isSame);
        //if (isSame) {
             RegisterActivity.wasMyOwnNumber = isSame;
             RegisterActivity.workDone = true;
        //}
        // ---send a broadcast intent to update the SMS received in the
        // activity---
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("SMS_RECEIVED_ACTION");
        broadcastIntent.putExtra("sms", str);
        context.sendBroadcast(broadcastIntent);	
    }
	
	public static SmsMessage[] getMessagesFromIntent(Intent intent) {
	    Object[] messages = (Object[]) intent.getSerializableExtra("pdus");
	    byte[][] pduObjs = new byte[messages.length][];
	
	    for (int i = 0; i < messages.length; i++) {
	        pduObjs[i] = (byte[]) messages[i];
	    }
	
	    byte[][] pdus = new byte[pduObjs.length][];
	    int pduCount = pdus.length;
	    SmsMessage[] msgs = new SmsMessage[pduCount];
	    for (int i = 0; i < pduCount; i++) {
	        pdus[i] = pduObjs[i];
	        msgs[i] = SmsMessage.createFromPdu(pdus[i]);
	    }
	    return msgs;
	}
}  