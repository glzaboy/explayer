package com.qintingfm.explayer.tiny_player;

import android.app.Activity;
import android.content.ComponentName;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.view.View;

import com.qintingfm.explayer.fragment.PlayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;

public class PlayerClient extends MediaBrowserCompat.ConnectionCallback {
    private WeakReference<Activity> activityWeakReference;
    MediaBrowserCompat mediaBrowser;
    private PlayerControlCallback playerControlCallback=new PlayerControlCallback();
    MediaControllerCompat mediaControllerCompat;


    public PlayerClient(Activity activity) {
        this.activityWeakReference = new WeakReference<>(activity);
    }
    /**
     * 界面创建
     * @param rootHints
     */
    public void onCreate(@Nullable Bundle rootHints){
        Activity activity = activityWeakReference.get();
        mediaBrowser =new MediaBrowserCompat(activity,new ComponentName(activity, TinyPlayerService.class),this,rootHints);
    }
    /**
     * 界面开始
     */
    public void onStart(){
        if(mediaBrowser !=null && !mediaBrowser.isConnected()){
            mediaBrowser.connect();
        }
    }
    /**
     * 界面停止
     */
    public void onStop(){
        Activity activity = activityWeakReference.get();
        if(MediaControllerCompat.getMediaController(activity)!=null){
            MediaControllerCompat.getMediaController(activity).unregisterCallback(playerControlCallback);
        }
        if(mediaBrowser !=null && mediaBrowser.isConnected()){
            mediaBrowser.disconnect();
        }
    }
    /**
     * 界面销毁
     */
    public void onDestroy(){
        if(mediaBrowser !=null){
            mediaBrowser =null;
        }
    }
    /**
     * 界面恢复
     */
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
    /**
     *
     * @param mediaId 媒体ID
     * @param bundle 播放的其它参数
     */
    public void playFromMediaId(String mediaId, Bundle bundle){
        mediaControllerCompat.getTransportControls().playFromMediaId(mediaId,bundle);
    }
    /**
     * 来源于搜索的播放
     * @param query 搜索条件
     * @param bundle 播放的其它参数
     */
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
        final Activity activity = activityWeakReference.get();
        try {
            mediaControllerCompat =new MediaControllerCompat(activity, mediaBrowser.getSessionToken());
            MediaControllerCompat.setMediaController(activity,mediaControllerCompat);
            if (activity instanceof PlayerClientListen) {
                final PlayerClientListen playerClientListen=(PlayerClientListen) activity;
                mediaBrowser.subscribe(activityWeakReference.get().getPackageName(), new MediaBrowserCompat.SubscriptionCallback() {
                    @Override
                    public void onChildrenLoaded(@NonNull String parentId, @NonNull List<MediaBrowserCompat.MediaItem> children) {
                        playerClientListen.onChildrenLoaded(parentId, children);
                    }
                });
            }

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
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface PlayerClientListen {
        void onChildrenLoaded(@NonNull String parentId, @NonNull List<MediaBrowserCompat.MediaItem> children);
    }

}
