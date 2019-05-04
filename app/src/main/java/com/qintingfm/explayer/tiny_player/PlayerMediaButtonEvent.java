package com.qintingfm.explayer.tiny_player;

import android.support.v4.media.session.PlaybackStateCompat;

import com.qintingfm.explayer.tiny_player.server.PlayerMediaSessionCompatCallback;
import com.qintingfm.explayer.tiny_player.server.TinyPlayerService;

import java.lang.ref.WeakReference;
import java.util.TimerTask;

public class PlayerMediaButtonEvent extends TimerTask {
    private WeakReference<PlayerMediaSessionCompatCallback> playerMediaSessionCompatCallbackWeakReference;

    public PlayerMediaButtonEvent(PlayerMediaSessionCompatCallback playerMediaSessionCompatCallback) {
        this.playerMediaSessionCompatCallbackWeakReference = new WeakReference<>(playerMediaSessionCompatCallback);
    }

    @Override
    public void run() {
        PlayerMediaSessionCompatCallback playerMediaSessionCompatCallback = playerMediaSessionCompatCallbackWeakReference.get();
        if(playerMediaSessionCompatCallback==null){
            cancel();
            return;
        }
        TinyPlayerService tinyPlayerService = playerMediaSessionCompatCallback.tinyPlayerServiceWeakReference.get();
        switch (playerMediaSessionCompatCallback.headsetClick) {
            case 1:
                if (tinyPlayerService.mPlaybackState.getState() == PlaybackStateCompat.STATE_PLAYING) {
                    playerMediaSessionCompatCallback.onPause();
                } else if (tinyPlayerService.mPlaybackState.getState() == PlaybackStateCompat.STATE_PAUSED) {
                    playerMediaSessionCompatCallback.onPlay();

                }
                break;
            case 2:
                playerMediaSessionCompatCallback.onSkipToNext();
                break;
            case 3:
                playerMediaSessionCompatCallback.onSkipToPrevious();
                break;
            default:
        }
        playerMediaSessionCompatCallback.headsetClick=0;
    }

}
