package com.qintingfm.explayer.tiny_player;

import android.content.Context;
import android.media.AudioManager;

import java.lang.ref.WeakReference;

public class PlayerAudioManagerListener implements AudioManager.OnAudioFocusChangeListener {
    private AudioManager audioManager;
    private WeakReference<TinyPlayerService> tinyPlayerServiceWeakReference;
    private boolean mPausedByTransientLossOfFocus = false;
    private int volume = 0;

    public PlayerAudioManagerListener(TinyPlayerService tinyPlayerService) {
        this.tinyPlayerServiceWeakReference = new WeakReference<>(tinyPlayerService);
        audioManager = (AudioManager) tinyPlayerService.getSystemService(Context.AUDIO_SERVICE);
    }

    public void reqAudioFocus() {
        int i = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (i == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {

        }
    }
    public void loseAudioFocus(){
        audioManager.abandonAudioFocus(this);
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        TinyPlayerService tinyPlayerService = tinyPlayerServiceWeakReference.get();
        if (tinyPlayerService == null) {
            return;
        }
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN://恢复长时播放
                if ( mPausedByTransientLossOfFocus) {
//                    playerService.mediaControllerCompat.getTransportControls().play();
                }
                if (volume > 0) {
//                    tinyPlayerService.audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
                }
                volume = 0;
                break;
            case AudioManager.AUDIOFOCUS_LOSS://
                tinyPlayerService.mPlayerAudioManagerListener.loseAudioFocus();
//                playerService.loseAudioFocus();
//                if (playerService.mPlaybackStateCompat.getState() == PlaybackStateCompat.STATE_PLAYING) {
//                    playerService.mediaControllerCompat.getTransportControls().pause();
//                }


                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
//                if (playerService.mPlaybackStateCompat.getState() == PlaybackStateCompat.STATE_PLAYING) {
//                    playerService.mediaControllerCompat.getTransportControls().pause();
//                    mPausedByTransientLossOfFocus = true;
//                }

                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
//                if (playerService.mPlaybackStateCompat.getState() == PlaybackStateCompat.STATE_PLAYING) {
//                    volume = playerService.audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
//                    if (volume > 0) {
//                        playerService.audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 1, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
//                    }
//                    mPausedByTransientLossOfFocus = true;
//                }


                break;
            default:
        }
    }
}
