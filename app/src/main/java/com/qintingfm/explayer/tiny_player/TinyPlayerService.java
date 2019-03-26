package com.qintingfm.explayer.tiny_player;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import com.qintingfm.explayer.RemoteMediaButtonReceiver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media.MediaBrowserServiceCompat;

import java.util.LinkedList;
import java.util.List;

public class TinyPlayerService extends MediaBrowserServiceCompat {
    final String TAG = PlayerMediaPlayerListener.class.getName();
    protected MediaSessionCompat mediaSession;
    private PlaybackStateCompat.Builder stateBuilder;
    protected PlaybackStateCompat mPlaybackState;
    List<MediaDescriptionCompat> mediaDescriptionList =new LinkedList<>();
    protected HeadsetPlugReceiver mHeadsetPlugReceiver =new HeadsetPlugReceiver(this);
    PlayerMediaSessionCompatCallback mMediaSessionCallback =new PlayerMediaSessionCompatCallback(this);
    PlayerAudioManagerListener mPlayerAudioManagerListener;
    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        if(clientPackageName.equalsIgnoreCase(this.getPackageName())){
            return new MediaBrowserServiceCompat.BrowserRoot(this.getPackageName(),null);
        }
        return new MediaBrowserServiceCompat.BrowserRoot(null,null);
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
        result.sendResult(null);
        return;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: ");
        mPlayerAudioManagerListener=new PlayerAudioManagerListener(this);
        mediaSession=new MediaSessionCompat(this,this.getClass().getSimpleName());

        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mediaSession.setCallback(mMediaSessionCallback);
        setSessionToken(mediaSession.getSessionToken());
        stateBuilder=new PlaybackStateCompat.Builder();
        setPlaybackState(PlaybackStateCompat.STATE_NONE,0,1.0f);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
        setPlaybackState(PlaybackStateCompat.STATE_NONE,0,1.0f);
        mPlayerAudioManagerListener.loseAudioFocus();
        mPlayerAudioManagerListener=null;
        mMediaSessionCallback.destroyMediaPlayer();
        mMediaSessionCallback =null;
        mediaSession.release();
        mediaSession=null;

    }

    protected void setPlaybackState(@PlaybackStateCompat.State int state, long position, float playbackSpeed){
        mPlaybackState =stateBuilder.setState(state,position,playbackSpeed).build();
        mediaSession.setPlaybackState(mPlaybackState);
    }
    protected void setPlaybackState(@PlaybackStateCompat.State int state, String errorMsg){
        mPlaybackState =stateBuilder.setErrorMessage(state,errorMsg).build();
        mediaSession.setPlaybackState(mPlaybackState);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        RemoteMediaButtonReceiver.handleIntent(mediaSession,intent);
        if (intent != null && intent.getAction() != null) {
            long aLong = Long.valueOf(intent.getAction());
        }
        return super.onStartCommand(intent, flags, startId);
    }
}
