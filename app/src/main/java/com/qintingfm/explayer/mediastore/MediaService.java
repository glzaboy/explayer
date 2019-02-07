package com.qintingfm.explayer.mediastore;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.qintingfm.explayer.R;
import com.qintingfm.explayer.activity.NavActivity;

import java.util.List;

public class MediaService extends Service {
    private static final String TAG=MediaService.class.getName();
    Messenger messager=new Messenger(new MediaHandle());

    public MediaService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MediaStoreNotification mediaStoreNotification=new MediaStoreNotification(this.getApplicationContext(),"MediaService");
        mediaStoreNotification.getDefault(R.drawable.ic_music_black_24dp, "MediaService", " Media Store scan and update media").setOnClick(
                PendingIntent.getActivity(this,0x001,new Intent(this.getApplicationContext(),NavActivity.class),PendingIntent.FLAG_UPDATE_CURRENT)
        );
        startForeground(1000,mediaStoreNotification.getBuilder().build());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return messager.getBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }
}
