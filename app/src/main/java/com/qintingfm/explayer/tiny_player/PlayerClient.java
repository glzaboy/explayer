package com.qintingfm.explayer.tiny_player;

import android.app.Activity;
import android.content.ComponentName;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;
import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;

public class PlayerClient extends MediaBrowserCompat.ConnectionCallback {
    WeakReference<Activity> activityWeakReference;
    MediaBrowserCompat mediaBrowserCompat;
    PlayerControlCallback playerControlCallback=new PlayerControlCallback();
    MediaControllerCompat mediaControllerCompat;


    public PlayerClient(Activity activity) {
        this.activityWeakReference = new WeakReference<>(activity);
    }

    public void onCreate(@Nullable Bundle rootHints){
        Activity activity = activityWeakReference.get();
        mediaBrowserCompat=new MediaBrowserCompat(activity,new ComponentName(activity, TinyPlayerService.class),this,rootHints);
    }

    public void onStart(){
        if(mediaBrowserCompat!=null && !mediaBrowserCompat.isConnected()){
            mediaBrowserCompat.connect();
        }
    }


    public void onStop(){
        Activity activity = activityWeakReference.get();
        if(MediaControllerCompat.getMediaController(activity)!=null){
            MediaControllerCompat.getMediaController(activity).unregisterCallback(playerControlCallback);
        }
        if(mediaBrowserCompat!=null && mediaBrowserCompat.isConnected()){
            mediaBrowserCompat.disconnect();
            mediaBrowserCompat=null;
        }
    }
    public void onResume(){
        Activity activity = activityWeakReference.get();
        activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    @Override
    public void onConnected() {
        super.onConnected();
        Activity activity = activityWeakReference.get();
        try {
            mediaControllerCompat =new MediaControllerCompat(activity,mediaBrowserCompat.getSessionToken());
            MediaControllerCompat.setMediaController(activity,mediaControllerCompat);
//            bind ui/
        } catch (RemoteException e) {
        }
    }

    @Override
    public void onConnectionSuspended() {
        super.onConnectionSuspended();
    }

    @Override
    public void onConnectionFailed() {
        super.onConnectionFailed();
    }

}
