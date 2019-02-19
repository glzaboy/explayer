package com.qintingfm.explayer.player;

import android.os.Message;
import android.os.RemoteException;
import android.support.v4.media.session.PlaybackStateCompat;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;


public class PlayerSeekTask extends TimerTask {
    private WeakReference<PlayerService> playerServiceWeakReference;
    private Timer timer;

    public PlayerSeekTask(PlayerService playerService) {
        super();
        playerServiceWeakReference = new WeakReference<>(playerService);
    }

    protected void start() {
        timer = new Timer();
        timer.schedule(this, 0, 300);
    }

    private void stop() {
        if (timer != null) {
            timer.cancel();
        }
    }

    @Override
    public void run() {
        PlayerService playerService = playerServiceWeakReference.get();
        if (!playerService.mBind) {
            stop();
        }
        int state = playerService.mPlaybackStateCompat.getState();

        if (state != PlaybackStateCompat.STATE_BUFFERING && state != PlaybackStateCompat.STATE_PLAYING) {
            stop();
        }
        if (playerService.uiMessenger != null) {
            Message obtain = Message.obtain();

            obtain.what = PlayerEvent.PLAYER_UPDATE_SEEK_BAR;

            obtain.arg1 = playerService.mediaPlayer.getDuration();
            obtain.arg2 = playerService.mediaPlayer.getCurrentPosition();
            try {
                playerService.uiMessenger.send(obtain);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
}
