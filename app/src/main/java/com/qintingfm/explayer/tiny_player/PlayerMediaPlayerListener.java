package com.qintingfm.explayer.tiny_player;

import android.media.MediaPlayer;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import com.qintingfm.explayer.tiny_player.server.TinyPlayerService;

import java.lang.ref.WeakReference;

public class PlayerMediaPlayerListener implements MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnSeekCompleteListener {
    final String TAG = PlayerMediaPlayerListener.class.getName();
    private WeakReference<TinyPlayerService> tinyPlayerServiceWeakReference;

    public PlayerMediaPlayerListener(TinyPlayerService tinyPlayerService) {
        tinyPlayerServiceWeakReference = new WeakReference<>(tinyPlayerService);
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        Log.d(TAG, "Music Buffering %" + percent);
        TinyPlayerService tinyPlayerService = tinyPlayerServiceWeakReference.get();
        if (tinyPlayerService != null) {
            tinyPlayerService.setPlaybackState(tinyPlayerService.stateBuilder.setBufferedPosition(percent).build());
        }

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        TinyPlayerService tinyPlayerService = tinyPlayerServiceWeakReference.get();
        tinyPlayerService.setPlaybackState(tinyPlayerService.stateBuilder.setState(PlaybackStateCompat.STATE_STOPPED, mp.getCurrentPosition(), 1.0f).build());
        tinyPlayerService.mMediaSessionCallback.onSkipToNext();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        TinyPlayerService tinyPlayerService = tinyPlayerServiceWeakReference.get();
        tinyPlayerService.setPlaybackState(tinyPlayerService.stateBuilder.setErrorMessage(what, "播放错误:代码" + what).build());
        return true;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        TinyPlayerService tinyPlayerService = tinyPlayerServiceWeakReference.get();
        tinyPlayerService.setPlaybackState(tinyPlayerService.stateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,0,1.0f).build());
        tinyPlayerService.mMediaSessionCallback.onPlay();
//        playerService.playerNotification.updateNotify();
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        TinyPlayerService tinyPlayerService = tinyPlayerServiceWeakReference.get();
        mp.start();
        tinyPlayerService.setPlaybackState(tinyPlayerService.stateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,mp.getCurrentPosition(),1.0f).build());
//        playerService.playerNotification.updateNotify();
//        if (playerService.playerSeekTask!=null) {
//            playerService.playerSeekTask.stop();
//            playerService.playerSeekTask=null;
//        }
//        if (playerService.playerSeekTask == null) {
//            playerService.playerSeekTask = new PlayerSeekTask(playerService);
//            playerService.playerSeekTask.start();
//        }
    }
}
