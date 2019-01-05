package com.qintingfm.explayer.playercore;

import android.media.MediaPlayer;
import android.util.Log;
import android.os.Message;
import android.os.RemoteException;

public class PlayerEvent implements MediaPlayer.OnPreparedListener,MediaPlayer.OnErrorListener,MediaPlayer.OnBufferingUpdateListener,MediaPlayer.OnCompletionListener {

    static final String TAG=PlayerEvent.class.getName();
    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        Log.d(PlayerEvent.TAG,"Music Buffering %"+percent);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        PlayerTimerTask.stop();
        try {
            if(PlayerCore.uiMessenger!=null){
                Message obtain = Message.obtain();
                obtain.what=PlayerEumu.HANDLER_DISABLE;
                PlayerCore.uiMessenger.send(obtain);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Log.d(TAG,"music onCompletion");
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.d(PlayerEvent.TAG,"Music error");
        PlayerTimerTask.stop();
        return true;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.d(PlayerEvent.TAG,"PlayerService onPrepared");
        mp.start();
    }
}
