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
    protected PlaybackStateCompat mPlaybackStateCompat;
    List<MediaDescriptionCompat> mediaDescriptionCompatList=new LinkedList<>();
//    PlayerAudioManagerListener playerAudioManagerListener=new PlayerAudioManagerListener(this);
    protected HeadsetPlugReceiver headsetPlugReceiver=new HeadsetPlugReceiver(this);
    PlayerMediaSessionCompatCallback playerMediaSessionCompatCallback=new PlayerMediaSessionCompatCallback(this);




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
        mediaSession.setCallback(playerMediaSessionCompatCallback);
        setSessionToken(mediaSession.getSessionToken());
        stateBuilder=new PlaybackStateCompat.Builder();
        setPlaybackState(PlaybackStateCompat.STATE_NONE,0,1.0f);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
        mPlayerAudioManagerListener.loseAudioFocus();
        mPlayerAudioManagerListener=null;
        playerMediaSessionCompatCallback.destroyMediaPlayer();
        playerMediaSessionCompatCallback=null;
        mediaSession.release();
        mediaSession=null;
        setPlaybackState(PlaybackStateCompat.STATE_NONE,0,1.0f);
    }

    protected void setPlaybackState(@PlaybackStateCompat.State int state, long position, float playbackSpeed){
        mPlaybackStateCompat=stateBuilder.setState(state,position,playbackSpeed).build();
        mediaSession.setPlaybackState(mPlaybackStateCompat);
    }
    protected void setPlaybackState(@PlaybackStateCompat.State int state, String errorMsg){
        mPlaybackStateCompat=stateBuilder.setErrorMessage(state,errorMsg).build();
        mediaSession.setPlaybackState(mPlaybackStateCompat);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        RemoteMediaButtonReceiver.handleIntent(mediaSession,intent);
        if (intent != null && intent.getAction() != null) {
            long aLong = Long.valueOf(intent.getAction());
//            if (aLong == PlaybackStateCompat.ACTION_PLAY_FROM_URI) {
//                Bundle bundle = new Bundle();
//                bundle.putString("title", intent.getExtras().getString("title"));
//                bundle.putString("artist", intent.getExtras().getString("artist"));
//                bundle.putInt("position", intent.getExtras().getInt("position"));
//            }
//            if (aLong == PlaybackStateCompat.ACTION_SEEK_TO) {
//                int seek = intent.getExtras().getInt("seek");
//                playerMediaSessionCompatCallback.onSeekTo(seek);
//            }
//            if (aLong == PlaybackStateCompat.ACTION_PLAY_PAUSE) {
//                if (mPlaybackStateCompat.getState() == PlaybackStateCompat.STATE_PLAYING) {
//                    playerMediaSessionCompatCallback.onPause();
//                    mPlayerAudioManagerListener.loseAudioFocus();
//                } else if (mPlaybackStateCompat.getState() == PlaybackStateCompat.STATE_PAUSED) {
//                    playerMediaSessionCompatCallback.onPlay();
//                    mPlayerAudioManagerListener.reqAudioFocus();
//                }
//            }
        }


        return super.onStartCommand(intent, flags, startId);
    }
}
