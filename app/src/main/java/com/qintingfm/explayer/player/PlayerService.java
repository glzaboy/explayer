package com.qintingfm.explayer.player;

import android.app.PendingIntent;
import android.app.Service;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.net.rtp.AudioStream;
import android.os.*;
import android.support.annotation.RequiresApi;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.KeyEvent;
import com.qintingfm.explayer.RemoteMediaButtonReceiver;
import com.qintingfm.explayer.dao.LocalMediaDao;
import com.qintingfm.explayer.database.MediaStoreDatabase;
import com.qintingfm.explayer.entity.LocalMedia;

import java.io.IOException;
import java.lang.ref.WeakReference;


public class PlayerService extends Service implements MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnSeekCompleteListener {
    static final String TAG = PlayerService.class.getName();
    MediaSessionCompat mediaSessionCompat;
    PlaybackStateCompat.Builder mPlaybackBuilder;
    PlaybackStateCompat mPlaybackStateCompat;
    MediaControllerCompat mediaControllerCompat;
    protected MediaPlayer mediaPlayer;
    PlayerServiceHandle playerServiceHandle;
    Messenger messenger;
    Messenger uiMessenger;
    Bundle bundle = new Bundle();
    PlayerNotification playerNotification = null;
    MediaStoreDatabase mediaStoreDatabase;
    LocalMediaDao localMediaDao;
    protected PlayerSeekTask playerSeekTask;
    AudioManager audioManager;
    private PlayerAudioManagerListener playerAudioManagerListener=new PlayerAudioManagerListener(this);
    MediaSessionCompat.Callback mediaSessionCompatCallback = new MediaSessionCompat.Callback() {
        @Override
        public void onCommand(String command, Bundle extras, ResultReceiver cb) {
            super.onCommand(command, extras, cb);
        }

        @Override
        public void onPlayFromUri(Uri uri, Bundle extras) {
            if (extras.getString("title") == null) {
                extras.putString("title", "Ex player No title");
            }
            mediaPlayer.reset();
            try {
                mediaPlayer.setDataSource(uri.getPath());
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
            bundle = extras;
            super.onPlayFromUri(uri, extras);
        }

        @Override
        public void onPlayFromMediaId(String mediaId, Bundle extras) {
            if (extras.getString("title") == null) {
                extras.putString("title", "No title");
            }
            bundle = extras;
            super.onPlayFromMediaId(mediaId, extras);
        }

        @Override
        public void onPlayFromSearch(String query, Bundle extras) {
            if (extras.getString("title") == null) {
                extras.putString("title", "Ex player query");
            }
            if (extras.getString("artist") == null) {
                extras.putString("artist", null);
            }
            bundle = extras;
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
                    reqAudioFocus();
                    mPlaybackStateCompat = mPlaybackBuilder.setState(PlaybackStateCompat.STATE_PLAYING, mediaPlayer.getCurrentPosition(), 1.0f).build();
                    mediaSessionCompat.setPlaybackState(mPlaybackStateCompat);
                    mediaPlayer.start();
                    playerNotification.updateNotify();
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
                    playerNotification.updateNotify();
                    break;
                default:
            }
        }

        @Override
        public void onSkipToNext() {
            super.onSkipToNext();
            LocalMedia position = localMediaDao.findNext(bundle.getInt("position"));
            if (position != null) {
                Bundle extras = new Bundle();
                extras.putString("title", position.getTitle());
                extras.putString("artist", position.getArtist());
                extras.putInt("position", position.getId());
                this.onPlayFromUri(Uri.parse(position.getData()), extras);
            }
        }

        @Override
        public void onSkipToPrevious() {
            super.onSkipToPrevious();
            LocalMedia position = localMediaDao.findPrev(bundle.getInt("position"));
            if (position != null) {
                Bundle extras = new Bundle();
                extras.putString("title", position.getTitle());
                extras.putString("artist", position.getArtist());
                extras.putInt("position", position.getId());
                this.onPlayFromUri(Uri.parse(position.getData()), extras);
            }


        }

        @Override
        public void onSeekTo(long pos) {
            super.onSeekTo(pos);
            switch (mPlaybackStateCompat.getState()) {
                case PlaybackStateCompat.STATE_PLAYING:
                    mPlaybackStateCompat = mPlaybackBuilder.setState(PlaybackStateCompat.STATE_REWINDING, pos, 1.0f).build();
                    mediaSessionCompat.setPlaybackState(mPlaybackStateCompat);
                    mediaPlayer.seekTo((int) pos);
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
                    playerNotification.updateNotify();
                    break;
                default:
            }


        }

        @Override
        public boolean onMediaButtonEvent(Intent mediaButtonEvent) {
            String action = mediaButtonEvent.getAction();
            KeyEvent keyEvent=(KeyEvent)mediaButtonEvent.getExtras().get(Intent.EXTRA_KEY_EVENT);
            switch (keyEvent.getKeyCode()){
                case KeyEvent.KEYCODE_HEADSETHOOK:
                case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                    if (mPlaybackStateCompat.getState() == PlaybackStateCompat.STATE_PLAYING) {
                        mediaControllerCompat.getTransportControls().pause();
                    } else if (mPlaybackStateCompat.getState() == PlaybackStateCompat.STATE_PAUSED) {
                        mediaControllerCompat.getTransportControls().play();
                    }
                    break;
                case KeyEvent.KEYCODE_MEDIA_PLAY:
                    mediaControllerCompat.getTransportControls().play();
                    break;
                case KeyEvent.KEYCODE_MEDIA_PAUSE:
                    mediaControllerCompat.getTransportControls().pause();
                    break;
                case KeyEvent.KEYCODE_MEDIA_NEXT:
                    mediaControllerCompat.getTransportControls().skipToNext();
                    break;
                case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                    mediaControllerCompat.getTransportControls().skipToPrevious();
                    break;
            }
            Log.i(TAG, "onMediaButtonEvent: " + mediaButtonEvent.getAction());
            return super.onMediaButtonEvent(mediaButtonEvent);
        }

    };

    public PlayerService() {
    }


    @Override
    public IBinder onBind(Intent intent) {
        if (PlaybackStateCompat.STATE_BUFFERING == mPlaybackStateCompat.getState() || PlaybackStateCompat.STATE_PLAYING == mPlaybackStateCompat.getState()) {
            if (playerSeekTask == null) {
                playerSeekTask = new PlayerSeekTask(this);
                playerSeekTask.start();
            }
        }
        Log.d(TAG,"onBind"+intent.toString());
        return messenger.getBinder();
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        if (PlaybackStateCompat.STATE_BUFFERING == mPlaybackStateCompat.getState() || PlaybackStateCompat.STATE_PLAYING == mPlaybackStateCompat.getState()) {
            if (playerSeekTask == null) {
                playerSeekTask = new PlayerSeekTask(this);
                playerSeekTask.start();
            }
        }
        Log.d(TAG,"onRebind"+intent.toString());
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if (playerSeekTask != null) {
            playerSeekTask.stop();
            playerSeekTask = null;
        }
        super.onUnbind(intent);
        Log.d(TAG,"onUnbind"+intent.toString());
        //super.onUnbind默认返回false,注意返回false后将在随后的流程中不再调用OnBind ,OnReBind和OnUnbind
        return true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        RemoteMediaButtonReceiver.handleIntent(mediaSessionCompat,intent);
        if (intent != null && intent.getAction() != null) {
            long aLong = Long.valueOf(intent.getAction());
            if (aLong == PlaybackStateCompat.ACTION_PLAY) {
                mediaControllerCompat.getTransportControls().play();
            }
            if (aLong == PlaybackStateCompat.ACTION_STOP) {
                mediaControllerCompat.getTransportControls().stop();
            }
            if (aLong == PlaybackStateCompat.ACTION_PAUSE) {
                mediaControllerCompat.getTransportControls().pause();
            }
            if (aLong == PlaybackStateCompat.ACTION_PLAY_FROM_URI) {
                Bundle bundle = new Bundle();
                bundle.putString("title", intent.getExtras().getString("title"));
                bundle.putString("artist", intent.getExtras().getString("artist"));
                bundle.putInt("position", intent.getExtras().getInt("position"));
                mediaControllerCompat.getTransportControls().playFromUri(intent.getData(), bundle);
            }
            if (aLong == PlaybackStateCompat.ACTION_SEEK_TO) {
                int seek = intent.getExtras().getInt("seek");
                mediaControllerCompat.getTransportControls().seekTo(seek);
            }
            if (aLong == PlaybackStateCompat.ACTION_PLAY_PAUSE) {
                if (mPlaybackStateCompat.getState() == PlaybackStateCompat.STATE_PLAYING) {
                    mediaControllerCompat.getTransportControls().pause();
                } else if (mPlaybackStateCompat.getState() == PlaybackStateCompat.STATE_PAUSED) {
                    mediaControllerCompat.getTransportControls().play();
                }
            }
            if (aLong == PlaybackStateCompat.ACTION_SKIP_TO_NEXT) {
                mediaControllerCompat.getTransportControls().skipToNext();
//                if(mPlaybackStateCompat.getState()==PlaybackStateCompat.STATE_PLAYING){
//                    mediaControllerCompat.getTransportControls().skipToNext();
//                }else if(mPlaybackStateCompat.getState()==PlaybackStateCompat.STATE_PAUSED){
//                    mediaControllerCompat.getTransportControls().skipToNext();
//                }
            }
            if (aLong == PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS) {
                mediaControllerCompat.getTransportControls().skipToPrevious();
//                if(mPlaybackStateCompat.getState()==PlaybackStateCompat.STATE_PLAYING){
//                    mediaControllerCompat.getTransportControls().skipToPrevious();
//                }else if(mPlaybackStateCompat.getState()==PlaybackStateCompat.STATE_PAUSED){
//                    mediaControllerCompat.getTransportControls().skipToPrevious();
//                }
            }
        }


        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        playerServiceHandle = new PlayerServiceHandle(this);
        messenger = new Messenger(playerServiceHandle);
        mPlaybackBuilder = new PlaybackStateCompat.Builder();
        mPlaybackStateCompat = mPlaybackBuilder.setState(PlaybackStateCompat.STATE_NONE, 0, 1.0f).build();

        mediaSessionCompat = new MediaSessionCompat(this.getApplicationContext(), this.getClass().getSimpleName());
        mediaSessionCompat.setActive(true);
        mediaSessionCompat.setPlaybackState(mPlaybackStateCompat);

        mediaSessionCompat.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS);
        mediaSessionCompat.setCallback(mediaSessionCompatCallback);
        mediaSessionCompat.setMediaButtonReceiver(PendingIntent.getBroadcast(this,200,new Intent(this,RemoteMediaButtonReceiver.class),PendingIntent.FLAG_UPDATE_CURRENT));

        mediaControllerCompat = new MediaControllerCompat(this.getApplicationContext(), mediaSessionCompat);


        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnSeekCompleteListener(this);
        playerNotification = new PlayerNotification(this.getApplicationContext(), "Ex Player Core");
        playerNotification.setPlayerServiceWeakReference(this);
        playerNotification.updateNotify();
        mediaStoreDatabase = Room.databaseBuilder(this.getApplicationContext(), MediaStoreDatabase.class, "Media Store Database").allowMainThreadQueries().build();
        localMediaDao = mediaStoreDatabase.getLocalMediaDao();
        startForeground(100, playerNotification.getBuilder().build());
        Log.d(TAG, "PlayerService onCreate");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
        Log.d(TAG, "PlayerService onDestroy");
        if (mediaPlayer != null) {
//            try {
//                mediaPlayer.releaseDrm();
//            } catch (MediaPlayer.NoDrmSchemeException e) {
//                e.printStackTrace();
//            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
//        if(mediaControllerCompat!=null){
//            mediaControllerCompat.setC(mediaSessionCompatCallback);
//        }
        if (mediaSessionCompat != null) {
            mediaSessionCompat.release();
        }

        mediaStoreDatabase.close();
        if (playerSeekTask != null) {
            playerSeekTask.stop();
            playerSeekTask = null;
        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        Log.d(TAG, "Music Buffering %" + percent);
        mPlaybackBuilder.setState(PlaybackStateCompat.STATE_BUFFERING, mediaPlayer.getCurrentPosition(), 1.0f);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mPlaybackStateCompat = mPlaybackBuilder.setState(PlaybackStateCompat.STATE_STOPPED, mp.getCurrentPosition(), 1.0f).build();
        mediaSessionCompat.setPlaybackState(mPlaybackStateCompat);
        mediaControllerCompat.getTransportControls().skipToNext();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mPlaybackStateCompat = mPlaybackBuilder.setErrorMessage(what, "").build();
        mediaSessionCompat.setPlaybackState(mPlaybackStateCompat);
        return true;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
        mPlaybackStateCompat = mPlaybackBuilder.setState(PlaybackStateCompat.STATE_PLAYING, mp.getCurrentPosition(), 1.0f).build();
        mediaSessionCompat.setPlaybackState(mPlaybackStateCompat);
        playerNotification.updateNotify();
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        mp.start();
        mPlaybackStateCompat = mPlaybackBuilder.setState(PlaybackStateCompat.STATE_PLAYING, mp.getCurrentPosition(), 1.0f).build();
        mediaSessionCompat.setPlaybackState(mPlaybackStateCompat);
        playerNotification.updateNotify();
        if (playerSeekTask!=null) {
            playerSeekTask.stop();
            playerSeekTask=null;
        }
        if (playerSeekTask == null) {
            playerSeekTask = new PlayerSeekTask(this);
            playerSeekTask.start();
        }
    }

    /**
     * Server Handle
     */
    public static class PlayerServiceHandle extends Handler {
        WeakReference<PlayerService> playerServiceWeakReference;

        public PlayerServiceHandle(PlayerService playerService) {
            this.playerServiceWeakReference = new WeakReference<>(playerService);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.replyTo != null) {
                playerServiceWeakReference.get().uiMessenger = msg.replyTo;
            } else {
                playerServiceWeakReference.get().uiMessenger = null;
            }
        }
    }
    public void reqAudioFocus(){
        if(audioManager==null){
            audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        }
        audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        int i = audioManager.requestAudioFocus(playerAudioManagerListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if(i==AudioManager.AUDIOFOCUS_REQUEST_GRANTED){

        }
    }
    public void loseAudioFocus(){
        if(audioManager==null){
            audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        }
        audioManager.abandonAudioFocus(playerAudioManagerListener);
    }


    @PlaybackStateCompat.Actions
    public long getAvailableActions() {
        long actions = PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID
                | PlaybackStateCompat.ACTION_PLAY_FROM_SEARCH
                | PlaybackStateCompat.ACTION_PLAY_FROM_URI
                | PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS;
        switch (mPlaybackStateCompat.getState()) {
            case PlaybackStateCompat.STATE_STOPPED:
                actions |= PlaybackStateCompat.ACTION_PLAY
                        | PlaybackStateCompat.ACTION_PAUSE;
                break;
            case PlaybackStateCompat.STATE_PLAYING:
                actions |= PlaybackStateCompat.ACTION_STOP
                        | PlaybackStateCompat.ACTION_PAUSE
                        | PlaybackStateCompat.ACTION_SEEK_TO;
                break;
            case PlaybackStateCompat.STATE_PAUSED:
                actions |= PlaybackStateCompat.ACTION_PLAY
                        | PlaybackStateCompat.ACTION_STOP;
                break;
            default:
                actions |= PlaybackStateCompat.ACTION_PLAY
                        | PlaybackStateCompat.ACTION_PLAY_PAUSE
                        | PlaybackStateCompat.ACTION_STOP
                        | PlaybackStateCompat.ACTION_PAUSE;
        }
        return actions;
    }
}
