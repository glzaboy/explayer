package com.qintingfm.explayer.player;

import android.support.v4.media.session.PlaybackStateCompat;

import java.lang.ref.WeakReference;
import java.util.TimerTask;

public class PlayerHeadsetClick extends TimerTask {
    WeakReference<PlayerMediaSessionCompatCallback> callbackWeakReference;



    public PlayerHeadsetClick(PlayerMediaSessionCompatCallback callback) {
        callbackWeakReference=new WeakReference<>(callback);
    }

    @Override
    public void run() {
        PlayerMediaSessionCompatCallback callback = callbackWeakReference.get();
        if(callback==null){
            cancel();
        }
        cancel();
        PlayerService playerService = callback.playerServiceWeakReference.get();
        switch (callback.headsetClick) {
            case 1:
                if (playerService.mPlaybackStateCompat.getState() == PlaybackStateCompat.STATE_PLAYING) {
                    playerService.mediaControllerCompat.getTransportControls().pause();
                } else if (playerService.mPlaybackStateCompat.getState() == PlaybackStateCompat.STATE_PAUSED) {
                    playerService.mediaControllerCompat.getTransportControls().play();
                }
                break;
            case 2:
                playerService.mediaControllerCompat.getTransportControls().skipToNext();
                break;
            case 3:
                playerService.mediaControllerCompat.getTransportControls().skipToPrevious();
                break;
            default:

        }
        callback.headsetClick=0;
    }
}
