package com.qintingfm.explayer.tiny_player;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media.MediaBrowserServiceCompat;
import com.qintingfm.explayer.player.PlayerMediaPlayerListener;

import java.util.LinkedList;
import java.util.List;

public class TinyPlayerService extends MediaBrowserServiceCompat {
    private MediaSessionCompat mediaSession;
    private PlaybackStateCompat.Builder stateBuilder;
    List<MediaDescriptionCompat> mediaDescriptionCompatList=new LinkedList<>();
//    PlayerAudioManagerListener playerAudioManagerListener=new PlayerAudioManagerListener(this);
    PlayerMediaSessionCompatCallback playerMediaSessionCompatCallback=new PlayerMediaSessionCompatCallback(this);




    MediaPlayer mMediaPlayer;
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
        mPlayerAudioManagerListener=new PlayerAudioManagerListener(this);
        mediaSession=new MediaSessionCompat(this,this.getClass().getSimpleName());

        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mediaSession.setCallback(playerMediaSessionCompatCallback);
        setSessionToken(mediaSession.getSessionToken());
        stateBuilder=new PlaybackStateCompat.Builder();
        setPlayBackState(PlaybackStateCompat.STATE_NONE,0,1.0f);
    }
    private void setPlayBackState(@PlaybackStateCompat.State int state, long position, float playbackSpeed){
        mediaSession.setPlaybackState(stateBuilder.setState(state,position,playbackSpeed).build());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            long aLong = Long.valueOf(intent.getAction());
            if (aLong == PlaybackStateCompat.ACTION_PLAY) {
                mPlayerAudioManagerListener.reqAudioFocus();
//                mediaControllerCompat.getTransportControls().play();
            }
            if (aLong == PlaybackStateCompat.ACTION_STOP) {
//                mediaControllerCompat.getTransportControls().stop();
            }
            if (aLong == PlaybackStateCompat.ACTION_PAUSE) {
                mPlayerAudioManagerListener.loseAudioFocus();
//                mediaControllerCompat.getTransportControls().pause();
            }
            if (aLong == PlaybackStateCompat.ACTION_PLAY_FROM_URI) {
                Bundle bundle = new Bundle();
                bundle.putString("title", intent.getExtras().getString("title"));
                bundle.putString("artist", intent.getExtras().getString("artist"));
                bundle.putInt("position", intent.getExtras().getInt("position"));
//                mediaControllerCompat.getTransportControls().playFromUri(intent.getData(), bundle);
            }
            if (aLong == PlaybackStateCompat.ACTION_SEEK_TO) {
                int seek = intent.getExtras().getInt("seek");
//                mediaControllerCompat.getTransportControls().seekTo(seek);
            }
            if (aLong == PlaybackStateCompat.ACTION_PLAY_PAUSE) {
//                if (mPlaybackStateCompat.getState() == PlaybackStateCompat.STATE_PLAYING) {
//                    loseAudioFocus();
//                    mediaControllerCompat.getTransportControls().pause();
//                } else if (mPlaybackStateCompat.getState() == PlaybackStateCompat.STATE_PAUSED) {
//                    reqAudioFocus();
//                    mediaControllerCompat.getTransportControls().play();
//                }
            }
            if (aLong == PlaybackStateCompat.ACTION_SKIP_TO_NEXT) {
//                mediaControllerCompat.getTransportControls().skipToNext();
            }
            if (aLong == PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS) {
//                mediaControllerCompat.getTransportControls().skipToPrevious();
            }
        }


        return super.onStartCommand(intent, flags, startId);
    }
}
