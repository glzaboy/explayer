package com.qintingfm.explayer.player;

import android.support.v4.media.session.PlaybackStateCompat;
import android.os.Message;
import android.os.RemoteException;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;


public class PlayerSeekTask extends TimerTask {
    static final String TAG=PlayerSeekTask.class.getName();
    WeakReference<PlayerService> playerServiceWeakReference;

    public PlayerSeekTask(PlayerService playerService) {

        super();
        playerServiceWeakReference=new WeakReference<>(playerService);
    }

    Timer timer;
    public void start(){
        timer=new Timer();
        timer.schedule(this,0,1000);
    }
    public void stop(){
        if(timer!=null){
            timer.cancel();
        }
    }
    @Override
    public void run() {
        PlayerService playerService = playerServiceWeakReference.get();
        if(playerService.mBind==false){
            stop();
        }
        int state = playerService.mPlaybackStateCompat.getState();

        if(state== PlaybackStateCompat.STATE_BUFFERING || state==PlaybackStateCompat.STATE_PLAYING){
        }else {
            stop();
        }
        if(playerService!=null){
            Message obtain = Message.obtain();

            obtain.what=PlayerEvent.PLAYER_UPDATE_SEEK_BAR;

            obtain.arg1=playerService.mediaPlayer.getDuration();
            obtain.arg2=playerService.mediaPlayer.getCurrentPosition();
            try {
                if(playerService.uiMessenger!=null){
                    playerService.uiMessenger.send(obtain);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
}
