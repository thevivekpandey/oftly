package com.oftly.oftly;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.HashMap;

public class GCMIntentService  {
	public static final int NOTIFICATION_ID = 1;
	private NotificationManager mNotificationManager;
	

	/*@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

		String messageType = gcm.getMessageType(intent);
		if (!extras.isEmpty()) {
			if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
				sendNotification("Send error: " + extras.toString());
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
				sendNotification("Deleted messages on server: " + extras.toString());
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
				writeOnNotificationBar(extras);
			}
		}
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}
	private void sendNotification(String msg) {		
		mNotificationManager = 
				(NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
		PendingIntent contentIntent =
				PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);
		Notification.Builder mBuilder = new Notification.Builder(this)
					.setSmallIcon(R.drawable.white_notification_logo)
					.setContentTitle("Oftly")
					.setStyle(new Notification.BigTextStyle().bigText(msg))
					.setContentText(msg);
		Log.v("VIVEK", "Msg received is " + msg);
		mBuilder.setContentIntent(contentIntent);
		mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
	}
	private void writeOnNotificationBar(Bundle bundle) {
		String senderPhone = bundle.getString("src");
		String[] targetPhones = bundle.getString("target").split(",");
		
		MainAdapter mainAdapter = MainAdapter.getMainAdapterInstance(getApplicationContext());
		HashMap<String, Contact> contactDetails = mainAdapter.contactDetails;
		String senderName = senderPhone;
		if (contactDetails.containsKey(senderPhone)) {
			senderName = contactDetails.get(senderPhone).getName();
		}

		String[] targetNames = new String[targetPhones.length];
		for (int i = 0; i < targetPhones.length; i++) {
			targetNames[i] = targetPhones[i];
			if (contactDetails.containsKey(targetPhones[i])) {
				targetNames[i] = contactDetails.get(targetPhones[i]).getName();
			}
		}
		
		String msg = "Hey! "+ senderName + " would like to gift you ";
		
		if (targetPhones.length == 1) {
			msg += "a photo of " + targetNames[0];
		} else {
			msg += " photos of ";
			for (int i = 0; i < targetNames.length; i++) {
				msg += targetNames[i];
				if (i == targetNames.length - 2) {
					msg += " and ";
				} else {
					msg += ", ";
				}
			}
			msg = msg.substring(0, msg.length() - 2);
		}
		msg += '.';

		mNotificationManager = 
				(NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);

		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtra("src", senderPhone);
		intent.putExtra("target_phones", targetPhones);

		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		
		PendingIntent tickIntent = PendingIntent.getActivity(this, 0, intent, 
																		PendingIntent.FLAG_UPDATE_CURRENT);
		PendingIntent crossIntent = PendingIntent.getBroadcast(this, 0, new Intent(this, NotificationClearer.class), 0);

		Notification.Builder builder = new Notification.Builder(this)
					.setSmallIcon(R.drawable.white_notification_logo)
					.setContentTitle("Oftly")
					.setStyle(new Notification.BigTextStyle().bigText(msg))
					.setContentText(msg)
					.addAction(R.drawable.tick, "Import", tickIntent)
					.addAction(R.drawable.cross, "No, Thanks", crossIntent);

		mNotificationManager.notify(NOTIFICATION_ID, builder.build());
	}*/
}