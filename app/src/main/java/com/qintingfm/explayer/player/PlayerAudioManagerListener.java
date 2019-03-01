package com.qintingfm.explayer.player;

import android.media.AudioManager;
import android.support.v4.media.session.PlaybackStateCompat;

import java.lang.ref.WeakReference;

public class PlayerAudioManagerListener implements AudioManager.OnAudioFocusChangeListener {
    private WeakReference<PlayerService> playerServiceWeakReference;
    private boolean mPausedByTransientLossOfFocus = false;
    private int volume = 0;

    public PlayerAudioManagerListener(PlayerService playerService) {
        this.playerServiceWeakReference = new WeakReference<>(playerService);
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        PlayerService playerService = playerServiceWeakReference.get();
        if (playerService == null) {
            return;
        }
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN://恢复长时播放
                if ( mPausedByTransientLossOfFocus) {
                    playerService.mediaControllerCompat.getTransportControls().play();
                }
                if (volume > 0) {
                    playerService.audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
                }
                volume = 0;
                break;
            case AudioManager.AUDIOFOCUS_LOSS://
                playerService.loseAudioFocus();
                if (playerService.mPlaybackStateCompat.getState() == PlaybackStateCompat.STATE_PLAYING) {
                    playerService.mediaControllerCompat.getTransportControls().pause();
                }


                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                if (playerService.mPlaybackStateCompat.getState() == PlaybackStateCompat.STATE_PLAYING) {
                    playerService.mediaControllerCompat.getTransportControls().pause();
                    mPausedByTransientLossOfFocus = true;
                }

                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                if (playerService.mPlaybackStateCompat.getState() == PlaybackStateCompat.STATE_PLAYING) {
                    volume = playerService.audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                    if (volume > 0) {
                        playerService.audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 1, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
                    }
                    mPausedByTransientLossOfFocus = true;
                }


                break;
            default:
        }
    }
}
