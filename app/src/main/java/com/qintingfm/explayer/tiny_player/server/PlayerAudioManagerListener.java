package com.qintingfm.explayer.tiny_player.server;

import android.content.Context;
import android.media.AudioManager;
import android.support.v4.media.session.PlaybackStateCompat;


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

    public int reqAudioFocus() {
        int i = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (i == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {

        }
        return i;
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
                    tinyPlayerService.mMediaSessionCallback.onPlay();
                }
                if (volume > 0) {
                    if(audioManager.isVolumeFixed()){
                        tinyPlayerService.mMediaSessionCallback.getMediaPlayer(false).setVolume(1.0f,1.0f);
                    }else{
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
                    }
                }
                volume = 0;
                break;
            case AudioManager.AUDIOFOCUS_LOSS://
                tinyPlayerService.mPlayerAudioManagerListener.loseAudioFocus();
                if (tinyPlayerService.mPlaybackState.getState() == PlaybackStateCompat.STATE_PLAYING) {
                    tinyPlayerService.mMediaSessionCallback.onPause();
                }


                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                if (tinyPlayerService.mPlaybackState.getState() == PlaybackStateCompat.STATE_PLAYING) {
                    tinyPlayerService.mMediaSessionCallback.onPause();
                    mPausedByTransientLossOfFocus = true;
                }

                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                if (tinyPlayerService.mPlaybackState.getState() == PlaybackStateCompat.STATE_PLAYING) {
                    if(audioManager.isVolumeFixed()){
                        tinyPlayerService.mMediaSessionCallback.getMediaPlayer(false).setVolume(0.1f,0.1f);
                        volume=1;
                    }else{
                        volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                        if (volume > 0) {
                            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 1, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
                        }
                    }
                    mPausedByTransientLossOfFocus = true;
                }


                break;
            default:
        }
    }
}
