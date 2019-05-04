package com.qintingfm.explayer.tiny_player.server;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import java.lang.ref.WeakReference;

public class HeadsetPlugReceiver extends BroadcastReceiver {
    private final String TAG= HeadsetPlugReceiver.class.getName();
    WeakReference<TinyPlayerService> tinyPlayerServiceWeakReference;
    boolean mPauseByHeadset=false;

    public HeadsetPlugReceiver() {
    }

    public HeadsetPlugReceiver(TinyPlayerService tinyPlayerService) {
        this.tinyPlayerServiceWeakReference = new WeakReference<>(tinyPlayerService);
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, intent.toString());
        TinyPlayerService tinyPlayerService = tinyPlayerServiceWeakReference.get();

        if (tinyPlayerService == null) {
            return;
        }

        if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equalsIgnoreCase(intent.getAction())) {
            if(tinyPlayerService.mPlaybackState.getState()== PlaybackStateCompat.STATE_PLAYING){
                Log.d(TAG,"ACTION_AUDIO_BECOMING_NOISY stop");
                mPauseByHeadset=true;
                tinyPlayerService.mMediaSessionCallback.onPause();
            }
        } else if (Intent.ACTION_HEADSET_PLUG.equalsIgnoreCase(intent.getAction()) && mPauseByHeadset) {
            if(intent.hasExtra("state")){
//                if(intent.getIntExtra("state",0)==0){
//
//                }
                if(intent.getIntExtra("state",0)==1){
                    Log.d(TAG,"ACTION_HEADSET_PLUG start");
                    mPauseByHeadset=false;
                    tinyPlayerService.mMediaSessionCallback.onPlay();
                }
            }

        }
    }
}
