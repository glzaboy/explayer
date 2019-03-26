package com.qintingfm.explayer.tiny_player;

import android.app.Activity;
import android.content.ComponentName;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;
import androidx.annotation.Nullable;

import java.io.File;
import java.lang.ref.WeakReference;

public class PlayerClient extends MediaBrowserCompat.ConnectionCallback {
    private WeakReference<Activity> activityWeakReference;
    MediaBrowserCompat mediaBrowserCompat;
    private PlayerControlCallback playerControlCallback=new PlayerControlCallback();
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
        }
    }
    public void onDestroy(){
        if(mediaBrowserCompat!=null){
            mediaBrowserCompat=null;
        }

    }
    public void onResume(){
        Activity activity = activityWeakReference.get();
        activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    /**
     * 播放指定的uri
     * @param uri 需要播放的音乐文件
     * @param bundle 播放的其它参数
     */
    public void playFromUri(Uri uri, Bundle bundle){
        if (uri.toString().startsWith("/")){
            uri=Uri.fromFile(new File(uri.getPath()));
        }
        mediaControllerCompat.getTransportControls().playFromUri(uri,bundle);
    }


    public void playFromMediaId(String mediaId, Bundle bundle){
        mediaControllerCompat.getTransportControls().playFromMediaId(mediaId,bundle);
    }
    public void playFromSearch(String query, Bundle bundle){
        mediaControllerCompat.getTransportControls().playFromSearch(query,bundle);
    }

    /**
     * 播放
     */
    public void play(){
        mediaControllerCompat.getTransportControls().play();
    }

    /**
     * 暂停
     */
    public void pause(){
        mediaControllerCompat.getTransportControls().pause();
    }
    /**
     * 跳转前一首
     */
    public void skipToPrevious(){
        mediaControllerCompat.getTransportControls().skipToPrevious();
    }
    /**
     * 跳转下一首
     */
    public void skipToNext(){
        mediaControllerCompat.getTransportControls().skipToNext();
    }
    /**
     * 跳转下一首
     */
    public void seekTo(long pos){
        mediaControllerCompat.getTransportControls().seekTo(pos);
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
