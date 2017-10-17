package com.oftly.oftly;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class AlarmService extends Service {
    public static String TAG = AlarmService.class.getSimpleName();
    
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
 
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v("VIVEK", "onStartCommand()");
        
        return super.onStartCommand(intent, flags, startId);
    }
}
