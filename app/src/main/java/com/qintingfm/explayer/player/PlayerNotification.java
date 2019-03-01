package com.qintingfm.explayer.player;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.media.session.PlaybackStateCompat;
import com.qintingfm.explayer.R;
import com.qintingfm.explayer.activity.NavActivity;
import com.qintingfm.explayer.notification.NotificationHelp;

import java.lang.ref.WeakReference;

import androidx.core.app.NotificationCompat;

public class PlayerNotification extends NotificationHelp {
    private WeakReference<PlayerService> playerServiceWeakReference;
    private NotificationCompat.Action playAction = new NotificationCompat.Action(android.R.drawable.ic_media_play
            , "播放", PendingIntent.getService(getContext().getApplicationContext(), 100, new Intent(getContext().getApplicationContext(), PlayerService.class).setAction(String.valueOf(PlaybackStateCompat.ACTION_PLAY)), PendingIntent.FLAG_UPDATE_CURRENT));

    private NotificationCompat.Action pauseAction = new NotificationCompat.Action(android.R.drawable.ic_media_pause
            , "暂停", PendingIntent.getService(getContext().getApplicationContext(), 100, new Intent(getContext().getApplicationContext(), PlayerService.class).setAction(String.valueOf(PlaybackStateCompat.ACTION_PAUSE)), PendingIntent.FLAG_UPDATE_CURRENT));

    private NotificationCompat.Action prevAction = new NotificationCompat.Action(android.R.drawable.ic_media_previous
            , "上一首", PendingIntent.getService(getContext().getApplicationContext(), 100, new Intent(getContext().getApplicationContext(), PlayerService.class).setAction(String.valueOf(PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)), PendingIntent.FLAG_UPDATE_CURRENT));
    private NotificationCompat.Action nextAction = new NotificationCompat.Action(android.R.drawable.ic_media_next
            , "暂停", PendingIntent.getService(getContext().getApplicationContext(), 100, new Intent(getContext().getApplicationContext(), PlayerService.class).setAction(String.valueOf(PlaybackStateCompat.ACTION_SKIP_TO_NEXT)), PendingIntent.FLAG_UPDATE_CURRENT));

    private NotificationCompat.Action appAction = new NotificationCompat.Action(R.drawable.ic_music_black_24dp
            , "进入应用", PendingIntent.getActivity(getContext().getApplicationContext(), 100, new Intent(getContext().getApplicationContext(), NavActivity.class), PendingIntent.FLAG_UPDATE_CURRENT));
    private NotificationCompat.Action playListAction = new NotificationCompat.Action(R.drawable.ic_queue_music_list_24dp
            , "进入应用", PendingIntent.getActivity(getContext().getApplicationContext(), 100, new Intent(getContext().getApplicationContext(), NavActivity.class), PendingIntent.FLAG_UPDATE_CURRENT));
    public PlayerNotification(Context context, String notifyTag) {
        super(context, notifyTag);
    }

    public PlayerNotification(Context context) {
        this(context, PlayerNotification.class.getSimpleName());
    }

    public WeakReference<PlayerService> getPlayerServiceWeakReference() {
        return playerServiceWeakReference;
    }

    public void setPlayerServiceWeakReference(PlayerService playerService) {
        this.playerServiceWeakReference = new WeakReference<>(playerService);
    }

    protected void updateNotify(){
        PlayerService playerService = getPlayerServiceWeakReference().get();
        if(playerService!=null){
            getDefault(R.drawable.ic_music_black_24dp, playerService.bundle.getString("title"), playerService.bundle.getString("artist"));
            getBuilder().setDefaults(0);
            if(playerService.mPlaybackStateCompat.getState()==PlaybackStateCompat.STATE_PLAYING){
                addAction(prevAction).addAction(pauseAction).addAction(nextAction);
                setOngoing(true);
            }else if(playerService.mPlaybackStateCompat.getState()==PlaybackStateCompat.STATE_PAUSED){
                addAction(prevAction).addAction(playAction).addAction(nextAction);
                setOngoing(false);
            }else{
                addAction(appAction).addAction(playAction).addAction(playListAction);
                setOngoing(true);
            }
            setOnClick(PendingIntent.getActivity(getContext().getApplicationContext(), 100, new Intent(getContext().getApplicationContext(), NavActivity.class), PendingIntent.FLAG_UPDATE_CURRENT));
//            setStyle(new NotificationCompat.MediaStyle().setShowCancelButton(true).setShowActionsInCompactView(0, 1, 2).setMediaSession(getPlayerServiceWeakReference().get().mediaSessionCompat.getSessionToken()));
            setNubmber(0);
            setLargeIcon(R.drawable.pig);
            playerService.startForeground(this.getClass().hashCode(),getBuilder().build());
            if(playerService.mPlaybackStateCompat.getState()==PlaybackStateCompat.STATE_PAUSED || playerService.mPlaybackStateCompat.getState()==PlaybackStateCompat.STATE_STOPPED){
                playerService.stopForeground(false);
            }
        }
    }
}
