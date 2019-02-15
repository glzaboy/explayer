package com.qintingfm.explayer.player;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.*;
import android.support.annotation.RequiresApi;
import android.support.v4.media.app.NotificationCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.widget.Toast;
import com.qintingfm.explayer.R;

import java.io.IOException;
import java.lang.ref.WeakReference;


public class PlayerService extends Service implements MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {
    static final String TAG = PlayerService.class.getName();
    MediaSessionCompat mediaSessionCompat;
    PlaybackStateCompat.Builder mPlaybackBuilder;
    PlaybackStateCompat mPlaybackStateCompat;
    MediaControllerCompat mediaControllerCompat;
    private MediaPlayer mediaPlayer;
    PlayerServiceHandle playerServiceHandle;
    Messenger messenger;
    Messenger uiMessenger;
    MediaSessionCompat.Callback mediaSessionCompatCallback = new MediaSessionCompat.Callback() {
        @Override
        public void onCommand(String command, Bundle extras, ResultReceiver cb) {
            super.onCommand(command, extras, cb);
        }

        @Override
        public void onPlayFromUri(Uri uri, Bundle extras) {
            mediaPlayer.reset();
            try {
                mediaPlayer.setDataSource(uri.getPath());
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
            super.onPlayFromUri(uri, extras);
        }

        @Override
        public void onPlayFromMediaId(String mediaId, Bundle extras) {
            super.onPlayFromMediaId(mediaId, extras);
        }

        @Override
        public void onPlayFromSearch(String query, Bundle extras) {
            super.onPlayFromSearch(query, extras);
        }

        @Override
        public void onPrepare() {
            super.onPrepare();
        }

        @Override
        public void onPlay() {
            super.onPlay();

            switch (mPlaybackStateCompat.getState()) {
                case PlaybackStateCompat.STATE_PAUSED:
                    mPlaybackStateCompat = mPlaybackBuilder.setState(PlaybackStateCompat.STATE_PLAYING, mediaPlayer.getCurrentPosition(), 1.0f).build();
                    mediaSessionCompat.setPlaybackState(mPlaybackStateCompat);
                    mediaPlayer.start();
                    break;
                default:
            }
        }

        @Override
        public void onPause() {
            super.onPause();
            switch (mPlaybackStateCompat.getState()) {
                case PlaybackStateCompat.STATE_PLAYING:
                    mPlaybackStateCompat = mPlaybackBuilder.setState(PlaybackStateCompat.STATE_PAUSED, mediaPlayer.getCurrentPosition(), 1.0f).build();
                    mediaSessionCompat.setPlaybackState(mPlaybackStateCompat);
                    mediaPlayer.pause();
                    break;
                default:
            }
        }

        @Override
        public void onSkipToNext() {
            super.onSkipToNext();
        }

        @Override
        public void onSkipToPrevious() {
            super.onSkipToPrevious();
        }

        @Override
        public void onSeekTo(long pos) {
            super.onSeekTo(pos);
            switch (mPlaybackStateCompat.getState()) {
                case PlaybackStateCompat.STATE_PLAYING:
                    mPlaybackStateCompat = mPlaybackBuilder.setState(PlaybackStateCompat.STATE_REWINDING, pos, 1.0f).build();
                    mediaSessionCompat.setPlaybackState(mPlaybackStateCompat);
                    mediaPlayer.seekTo((int)pos/1000);
                    break;
                default:
            }
        }

        @Override
        public void onSetRepeatMode(int repeatMode) {
            super.onSetRepeatMode(repeatMode);
        }

        @Override
        public void onSetShuffleMode(int shuffleMode) {
            super.onSetShuffleMode(shuffleMode);
        }

        @Override
        public void onStop() {
            super.onStop();
            switch (mPlaybackStateCompat.getState()) {
                case PlaybackStateCompat.STATE_PLAYING:
                    mPlaybackStateCompat = mPlaybackBuilder.setState(PlaybackStateCompat.STATE_STOPPED, mediaPlayer.getCurrentPosition(), 1.0f).build();
                    mediaSessionCompat.setPlaybackState(mPlaybackStateCompat);
                    mediaPlayer.stop();
                    break;
                default:
            }


        }

        @Override
        public boolean onMediaButtonEvent(Intent mediaButtonEvent) {
            return super.onMediaButtonEvent(mediaButtonEvent);
        }
    };

    public PlayerService() {
    }


    @Override
    public IBinder onBind(Intent intent) {
        Toast.makeText(getApplicationContext(), "binding", Toast.LENGTH_SHORT).show();
        return messenger.getBinder();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            long aLong = Long.valueOf(intent.getAction());
            if(aLong==PlaybackStateCompat.ACTION_PLAY){
                mediaControllerCompat.getTransportControls().play();
            }
            if(aLong==PlaybackStateCompat.ACTION_STOP){
                mediaControllerCompat.getTransportControls().stop();
            }
            if(aLong==PlaybackStateCompat.ACTION_PAUSE){
                mediaControllerCompat.getTransportControls().pause();
            }
            if(aLong==PlaybackStateCompat.ACTION_PLAY_FROM_URI){
                mediaControllerCompat.getTransportControls().playFromUri(intent.getData(), new Bundle());
            }
            if(aLong==PlaybackStateCompat.ACTION_SEEK_TO){
                int seek = intent.getExtras().getInt("seek");
                mediaControllerCompat.getTransportControls().seekTo(seek);
            }
            if(aLong==PlaybackStateCompat.ACTION_PLAY_PAUSE){
                if(mPlaybackStateCompat.getState()==PlaybackStateCompat.STATE_PLAYING){
                    mediaControllerCompat.getTransportControls().pause();
                }else if(mPlaybackStateCompat.getState()==PlaybackStateCompat.STATE_PAUSED){
                    mediaControllerCompat.getTransportControls().play();
                }
//                mediaControllerCompat.getTransportControls().seekTo(Long.valueOf(intent.getData().toString()));
            }
        }


        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        playerServiceHandle=new PlayerServiceHandle(this);
        messenger=new Messenger(playerServiceHandle);
        mPlaybackBuilder = new PlaybackStateCompat.Builder();
        mPlaybackStateCompat = mPlaybackBuilder.setState(PlaybackStateCompat.STATE_NONE, 0, 1.0f).build();

        mediaSessionCompat = new MediaSessionCompat(this.getApplicationContext(), this.getClass().getSimpleName());
        mediaSessionCompat.setActive(true);
        mediaSessionCompat.setPlaybackState(mPlaybackStateCompat);

        mediaSessionCompat.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS);
        mediaSessionCompat.setCallback(mediaSessionCompatCallback);

        mediaControllerCompat = new MediaControllerCompat(this.getApplicationContext(), mediaSessionCompat);


        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnPreparedListener(this);
        PlayerNotification playerNotificatio = new PlayerNotification(this.getApplicationContext(), "Ex Player Core");
        playerNotificatio.getDefault(R.drawable.ic_music_black_24dp, "Explayer", "播放");

        playerNotificatio.addAction(android.R.drawable.ic_media_previous, "上一首", PendingIntent.getService(this.getApplicationContext(), 100, new Intent(this.getApplicationContext(), PlayerService.class).setAction(String.valueOf(PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)), PendingIntent.FLAG_UPDATE_CURRENT));
//        playerNotificatio.addAction(android.R.drawable.ic_media_rew,"",null);

        ;
        playerNotificatio.addAction(android.R.drawable.ic_media_play, "播放", PendingIntent.getService(this.getApplicationContext(), 1, new Intent(this.getApplicationContext(), PlayerService.class).setAction(String.valueOf(PlaybackStateCompat.ACTION_PLAY_PAUSE)), PendingIntent.FLAG_UPDATE_CURRENT));


        playerNotificatio.addAction(android.R.drawable.ic_media_next, "下一首", PendingIntent.getService(this.getApplicationContext(), 100, new Intent(this.getApplicationContext(), PlayerService.class).setAction(String.valueOf(PlaybackStateCompat.ACTION_SKIP_TO_NEXT)), PendingIntent.FLAG_UPDATE_CURRENT));
//        playerNotificatio.addAction(android.R.drawable.ic_media_ff,"",null);
        playerNotificatio.setStyle(new NotificationCompat.MediaStyle().setShowCancelButton(true).setShowActionsInCompactView(0, 1, 2).setMediaSession(mediaSessionCompat.getSessionToken()));
        playerNotificatio.setNubmber(0);
        playerNotificatio.setLargeIcon(R.drawable.pig);
        startForeground(100, playerNotificatio.getBuilder().build());
        Log.d(TAG, "PlayerService onCreate");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
        Log.d(TAG, "PlayerService onDestroy");
        if(mediaPlayer!=null){
            mediaPlayer.release();
            try {
                mediaPlayer.releaseDrm();
            } catch (MediaPlayer.NoDrmSchemeException e) {
                e.printStackTrace();
            }
            mediaPlayer=null;
        }
//        if(mediaControllerCompat!=null){
//            mediaControllerCompat.setC(mediaSessionCompatCallback);
//        }
        if(mediaSessionCompat!=null){
            mediaSessionCompat.release();
        }





//        PlayerCore.destroyPlayer();
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        Log.d(TAG, "Music Buffering %" + percent);
        mPlaybackBuilder.setState(PlaybackStateCompat.STATE_BUFFERING,mediaPlayer.getCurrentPosition(),1.0f);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
        mPlaybackStateCompat = mPlaybackBuilder.setState(PlaybackStateCompat.STATE_PLAYING, 0, 1.0f).build();
        mediaSessionCompat.setPlaybackState(mPlaybackStateCompat);
    }


    /**
     * Server Handle
     */
    public static class PlayerServiceHandle extends Handler{
        WeakReference<PlayerService> playerServiceWeakReference;

        public PlayerServiceHandle(PlayerService playerService) {
            this.playerServiceWeakReference = new WeakReference<>(playerService);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.replyTo!=null){
                playerServiceWeakReference.get().uiMessenger=msg.replyTo;
            }else{
                playerServiceWeakReference.get().uiMessenger=null;
            }
        }
    }
}
