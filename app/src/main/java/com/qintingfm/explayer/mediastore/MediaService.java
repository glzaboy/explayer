package com.qintingfm.explayer.mediastore;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.qintingfm.explayer.R;

import java.util.List;

public class MediaService extends Service {
    private static final String TAG=MediaService.class.getName();
    Messenger messager=new Messenger(new MediaHandle());

    public MediaService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return messager.getBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"onStartCommand");
        MediaStoreNotification mediaStoreNotification=new MediaStoreNotification(this.getApplicationContext(),"Media Store scanf and update media");
        NotificationCompat.Builder test = mediaStoreNotification.getDefault(R.drawable.ic_music_black_24dp, "test", "I' ok").getBuilder();
        startForeground(1000,test.build());
        return super.onStartCommand(intent, flags, startId);
    }
}
