package com.qintingfm.explayer.player;

import android.util.Log;
import android.os.Message;
import android.os.RemoteException;

import java.util.TimerTask;


public class PlayerSeekTask extends TimerTask {
    static final String TAG=PlayerTimerTask.class.getName();
    @Override
    public void run() {
        Log.d(PlayerSeekTask.TAG,"music timer task");
        if(PlayerCore.uiMessenger!=null && PlayerCore.player!=null && PlayerCore.player.isPlaying()){
            Message obtain = Message.obtain();
            obtain.what=PlayerEumu.HANDLER_RANGE;
            obtain.arg1=PlayerCore.player.getDuration();
            obtain.arg2=PlayerCore.player.getCurrentPosition();
            try {
                Log.d(PlayerSeekTask.TAG,"music timer task");
                PlayerCore.uiMessenger.send(obtain);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
}
