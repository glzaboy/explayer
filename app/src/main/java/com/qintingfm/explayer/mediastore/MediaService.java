package com.qintingfm.explayer.mediastore;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

import java.util.List;

public class MediaService extends Service {
    private static final String TAG=MediaService.class.getName();
    Messenger messager=new Messenger(new MediaHandle());

    public MediaService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
//        throw new UnsupportedOperationException("Not yet implemented");
        return messager.getBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"onStartCommand");
//        MediaStoreMessageNotification.createNotifyChannel();
        startForeground(1000,MediaStoreMessageNotification.buildNotification(this.getApplicationContext(),"test",1).build());
        return super.onStartCommand(intent, flags, startId);
    }
}
