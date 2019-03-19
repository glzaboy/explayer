package com.qintingfm.explayer.tiny_player;

import android.media.MediaPlayer;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import com.qintingfm.explayer.player.PlayerService;

import java.lang.ref.WeakReference;

public class PlayerMediaPlayerListener implements MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnSeekCompleteListener {
    final String TAG = PlayerService.class.getName();
    private WeakReference<TinyPlayerService> tinyPlayerServiceWeakReference;
    public PlayerMediaPlayerListener(TinyPlayerService tinyPlayerService) {
        tinyPlayerServiceWeakReference=new WeakReference<>(tinyPlayerService);
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        Log.d(TAG, "Music Buffering %" + percent);
        TinyPlayerService tinyPlayerService = tinyPlayerServiceWeakReference.get();
        if(tinyPlayerService!=null){
            tinyPlayerService.setPlaybackState(PlaybackStateCompat.STATE_BUFFERING, tinyPlayerService.playerMediaSessionCompatCallback.mediaPlayer.getCurrentPosition(), 1.0f);
        }

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        TinyPlayerService tinyPlayerService = tinyPlayerServiceWeakReference.get();
        tinyPlayerService.setPlaybackState(PlaybackStateCompat.STATE_STOPPED,0,1.0f);
        tinyPlayerService.playerMediaSessionCompatCallback.onSkipToNext();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        TinyPlayerService tinyPlayerService = tinyPlayerServiceWeakReference.get();
        tinyPlayerService.setPlaybackState(PlaybackStateCompat.STATE_ERROR,"播放错误:代码"+what);
        return true;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        TinyPlayerService tinyPlayerService = tinyPlayerServiceWeakReference.get();
        tinyPlayerService.mPlayerAudioManagerListener.reqAudioFocus();
        mp.start();
        tinyPlayerService.setPlaybackState(PlaybackStateCompat.STATE_PLAYING,mp.getCurrentPosition(), 1.0f);
//        playerService.playerNotification.updateNotify();
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        TinyPlayerService tinyPlayerService = tinyPlayerServiceWeakReference.get();
        mp.start();
        tinyPlayerService.setPlaybackState(PlaybackStateCompat.STATE_PLAYING,mp.getCurrentPosition(),1.0f);
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
