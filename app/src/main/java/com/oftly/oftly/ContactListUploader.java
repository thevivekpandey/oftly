package com.oftly.oftly;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.oftly.oftly.asynctasks.ContactListUploaderTask;

public class ContactListUploader extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		//new ContactListUploaderTask(context).execute();
	}
}
