package com.qintingfm.explayer.player;

import android.media.AudioManager;

import java.lang.ref.WeakReference;

public class PlayerAudioManagerListener implements AudioManager.OnAudioFocusChangeListener {
    WeakReference<PlayerService> playerServiceWeakReference;

    public PlayerAudioManagerListener(PlayerService playerService) {
        this.playerServiceWeakReference = new WeakReference<>(playerService);
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        PlayerService playerService = playerServiceWeakReference.get();
        if(playerService==null){
            return;
        }
//        switch (focusChange){
//            case AudioManager.AUDIOFOCUS_GAIN://恢复长时播放
//                break;
//            case AudioManager.AUDIOFOCUS_LOSS://
////                playerService.loseAudioFocus();
//                break;
//            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
//                playerService.mediaControllerCompat.getTransportControls().pause();
//                break;
//            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
//                break;
//        }
    }
}
