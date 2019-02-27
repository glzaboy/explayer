package com.qintingfm.explayer.player;

import android.media.MediaPlayer;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import java.lang.ref.WeakReference;

public class PlayerMediaPlayerListener implements MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnSeekCompleteListener {
    final String TAG = PlayerService.class.getName();
    private WeakReference<PlayerService> playerServiceWeakReference;
    public PlayerMediaPlayerListener(PlayerService playerService) {
        playerServiceWeakReference=new WeakReference<>(playerService);
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        Log.d(TAG, "Music Buffering %" + percent);
        PlayerService playerService = playerServiceWeakReference.get();
        if(playerService!=null){
            playerService.mPlaybackBuilder.setState(PlaybackStateCompat.STATE_BUFFERING, playerService.mediaPlayer.getCurrentPosition(), 1.0f);
        }

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        PlayerService playerService = playerServiceWeakReference.get();
        playerService.mPlaybackStateCompat = playerService.mPlaybackBuilder.setState(PlaybackStateCompat.STATE_STOPPED, mp.getCurrentPosition(), 1.0f).build();
        playerService.mediaSessionCompat.setPlaybackState(playerService.mPlaybackStateCompat);
        playerService.mediaControllerCompat.getTransportControls().skipToNext();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        PlayerService playerService = playerServiceWeakReference.get();
        playerService.mPlaybackStateCompat = playerService.mPlaybackBuilder.setErrorMessage(what, "").build();
        playerService.mediaSessionCompat.setPlaybackState(playerService.mPlaybackStateCompat);
        return true;
//        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        PlayerService playerService = playerServiceWeakReference.get();
        playerService.reqAudioFocus();
        mp.start();
        playerService.mPlaybackStateCompat = playerService.mPlaybackBuilder.setState(PlaybackStateCompat.STATE_PLAYING, mp.getCurrentPosition(), 1.0f).build();
        playerService.mediaSessionCompat.setPlaybackState(playerService.mPlaybackStateCompat);
        playerService.playerNotification.updateNotify();
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        PlayerService playerService = playerServiceWeakReference.get();
        mp.start();
        playerService.mPlaybackStateCompat = playerService.mPlaybackBuilder.setState(PlaybackStateCompat.STATE_PLAYING, mp.getCurrentPosition(), 1.0f).build();
        playerService.mediaSessionCompat.setPlaybackState(playerService.mPlaybackStateCompat);
        playerService.playerNotification.updateNotify();
        if (playerService.playerSeekTask!=null) {
            playerService.playerSeekTask.stop();
            playerService.playerSeekTask=null;
        }
        if (playerService.playerSeekTask == null) {
            playerService.playerSeekTask = new PlayerSeekTask(playerService);
            playerService.playerSeekTask.start();
        }
    }
}
