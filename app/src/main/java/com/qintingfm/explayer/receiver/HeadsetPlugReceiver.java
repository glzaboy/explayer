package com.qintingfm.explayer.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import com.qintingfm.explayer.player.PlayerService;

import java.lang.ref.WeakReference;

public class HeadsetPlugReceiver extends BroadcastReceiver {
    private final String TAG= HeadsetPlugReceiver.class.getName();
    WeakReference<PlayerService> playerServiceWeakReference;
    boolean mPauseByHeadset=false;

    public HeadsetPlugReceiver() {
    }

    public HeadsetPlugReceiver(PlayerService playerService) {
        this.playerServiceWeakReference = new WeakReference<>(playerService);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, intent.toString());
        PlayerService playerService = playerServiceWeakReference.get();

        if (playerService == null) {
            return;
        }

        if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equalsIgnoreCase(intent.getAction())) {
            if(playerService.mPlaybackStateCompat.getState()== PlaybackStateCompat.STATE_PLAYING){
                Log.d(TAG,"ACTION_AUDIO_BECOMING_NOISY stop");
                mPauseByHeadset=true;
                playerService.mediaControllerCompat.getTransportControls().pause();
            }
        } else if (Intent.ACTION_HEADSET_PLUG.equalsIgnoreCase(intent.getAction()) && mPauseByHeadset) {
            if(intent.hasExtra("state")){
//                if(intent.getIntExtra("state",0)==0){
//
//                }
                if(intent.getIntExtra("state",0)==1){
                    Log.d(TAG,"ACTION_HEADSET_PLUG start");
                    mPauseByHeadset=false;
                    playerService.mediaControllerCompat.getTransportControls().play();
                }
            }

        }
    }
}
