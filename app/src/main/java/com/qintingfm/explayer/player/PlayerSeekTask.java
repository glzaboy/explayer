package com.qintingfm.explayer.player;

import android.os.Message;
import android.os.RemoteException;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;


public class PlayerSeekTask extends TimerTask {
    private static final String TAG=PlayerSeekTask.class.getName();
    private WeakReference<PlayerService> playerServiceWeakReference;
    private Timer timer;

    public PlayerSeekTask(PlayerService playerService) {
        super();
        playerServiceWeakReference = new WeakReference<>(playerService);
    }

    protected void start() {
        Log.d(TAG,"启动进度通知");
        timer = new Timer();
        timer.schedule(this, 0, 300);
    }

    protected void stop() {
        if (timer != null) {
            Log.d(TAG,"关闭进度通知");
            timer.cancel();
            timer=null;
        }
    }

    @Override
    public void run() {
        PlayerService playerService = playerServiceWeakReference.get();
//        if (!playerService.mBind) {
//            Log.d(TAG,"关闭进度通知服务已游离");
//            stop();
//        }
        int state = playerService.mPlaybackStateCompat.getState();

        if (state != PlaybackStateCompat.STATE_BUFFERING && state != PlaybackStateCompat.STATE_PLAYING) {
            Log.d(TAG,"不处理播放状态不通知");
            return;
        }
        if (playerService.uiMessenger != null) {
            Message obtain = Message.obtain();

            obtain.what = PlayerEvent.PLAYER_UPDATE_SEEK_BAR;
            Log.d(TAG,"发送播放状态");
            obtain.arg1 = playerService.mediaPlayer.getDuration();
            obtain.arg2 = playerService.mediaPlayer.getCurrentPosition();
//            obtain.obj=playerService.getAvailableActions();
            try {
                playerService.uiMessenger.send(obtain);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
}
