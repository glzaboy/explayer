package com.qintingfm.explayer.tiny_player;

import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.KeyEvent;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Timer;

public class PlayerMediaSessionCompatCallback  extends MediaSessionCompat.Callback {
    WeakReference<TinyPlayerService> tinyPlayerServiceWeakReference;

    public int headsetClick = 0;

    private Timer headsetTimer = new Timer();

    protected MediaPlayer mediaPlayer;
    PlayerMediaPlayerListener playerMediaPlayerListener;


    protected MediaPlayer getMediaPlayer(boolean reset){
        if(mediaPlayer==null){
            mediaPlayer=new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.reset();
            playerMediaPlayerListener=new PlayerMediaPlayerListener(tinyPlayerServiceWeakReference.get());
            mediaPlayer.setOnBufferingUpdateListener(playerMediaPlayerListener);
            mediaPlayer.setOnCompletionListener(playerMediaPlayerListener);
            mediaPlayer.setOnErrorListener(playerMediaPlayerListener);
            mediaPlayer.setOnPreparedListener(playerMediaPlayerListener);
            mediaPlayer.setOnSeekCompleteListener(playerMediaPlayerListener);
        }
        if(reset){
            mediaPlayer.reset();
        }
        return mediaPlayer;

    }
    protected void destroyMediaPlayer(){
        if(mediaPlayer!=null){
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer=null;
        }
    }

    public PlayerMediaSessionCompatCallback(TinyPlayerService tinyPlayerService) {
        tinyPlayerServiceWeakReference=new WeakReference<>(tinyPlayerService);

    }

    @Override
    public void onPrepareFromMediaId(String mediaId, Bundle extras) {
        super.onPrepareFromMediaId(mediaId, extras);
    }

    @Override
    public void onPrepareFromSearch(String query, Bundle extras) {
        super.onPrepareFromSearch(query, extras);
    }

    @Override
    public void onPlayFromUri(Uri uri, Bundle extras) {
        super.onPlayFromUri(uri, extras);
        TinyPlayerService tinyPlayerService = tinyPlayerServiceWeakReference.get();
        try {
            getMediaPlayer(true).setDataSource(tinyPlayerService,uri);
            getMediaPlayer(false).prepareAsync();
        } catch (IOException e) {
            tinyPlayerService.setPlaybackState(PlaybackStateCompat.STATE_ERROR,e.getMessage());
        }
    }

    @Override
    public void onPlay() {
        super.onPlay();
        TinyPlayerService tinyPlayerService = tinyPlayerServiceWeakReference.get();
        if(tinyPlayerService.mPlayerAudioManagerListener.reqAudioFocus()!=AudioManager.AUDIOFOCUS_REQUEST_GRANTED){
            return;
        }
        IntentFilter headSetPlug=new IntentFilter();
        headSetPlug.addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY  );
        headSetPlug.addAction(Intent.ACTION_HEADSET_PLUG);
        tinyPlayerService.registerReceiver(tinyPlayerService.mHeadsetPlugReceiver,headSetPlug);
        tinyPlayerService.mediaSession.setActive(true);
        tinyPlayerService.startService(new Intent(tinyPlayerService,TinyPlayerService.class));
        getMediaPlayer(false).start();
        tinyPlayerService.setPlaybackState(PlaybackStateCompat.STATE_PLAYING,0,1.0f);
    }

    @Override
    public void onPause() {
        super.onPause();
        TinyPlayerService tinyPlayerService = tinyPlayerServiceWeakReference.get();
        getMediaPlayer(false).pause();
        tinyPlayerService.setPlaybackState(PlaybackStateCompat.STATE_PAUSED,0,1.0f);
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
    public void onStop() {
        super.onStop();
        TinyPlayerService tinyPlayerService = tinyPlayerServiceWeakReference.get();
        tinyPlayerService.mPlayerAudioManagerListener.loseAudioFocus();
        tinyPlayerService.unregisterReceiver(tinyPlayerService.mHeadsetPlugReceiver);
        tinyPlayerService.mediaSession.setActive(false);
        tinyPlayerService.stopSelf();
        tinyPlayerService.stopForeground(false);
        destroyMediaPlayer();
        tinyPlayerService.setPlaybackState(PlaybackStateCompat.STATE_STOPPED,0,1.0f);
    }

    @Override
    public void onAddQueueItem(MediaDescriptionCompat description) {
        super.onAddQueueItem(description);
        TinyPlayerService tinyPlayerService = tinyPlayerServiceWeakReference.get();
        tinyPlayerService.mediaDescriptionList.add(description);
    }

    @Override
    public void onAddQueueItem(MediaDescriptionCompat description, int index) {
        super.onAddQueueItem(description, index);
        TinyPlayerService tinyPlayerService = tinyPlayerServiceWeakReference.get();
        tinyPlayerService.mediaDescriptionList.add(index,description);
    }

    @Override
    public void onRemoveQueueItem(MediaDescriptionCompat description) {
        super.onRemoveQueueItem(description);
        TinyPlayerService tinyPlayerService = tinyPlayerServiceWeakReference.get();
        tinyPlayerService.mediaDescriptionList.remove(description);
    }

    @Override
    public boolean onMediaButtonEvent(Intent mediaButtonEvent) {
        TinyPlayerService tinyPlayerService = tinyPlayerServiceWeakReference.get();
        KeyEvent keyEvent = (KeyEvent) mediaButtonEvent.getExtras().get(Intent.EXTRA_KEY_EVENT);
        switch (keyEvent.getKeyCode()) {
            case KeyEvent.KEYCODE_HEADSETHOOK://单键耳机单独点击播放或暂停，双击下一首，三击上一首。
                int action1 = keyEvent.getAction();
                if(action1==KeyEvent.ACTION_DOWN){
                    return super.onMediaButtonEvent(mediaButtonEvent);
                }
                if (headsetClick == 0) {
                    headsetClick++;
                    headsetTimer.schedule(new PlayerMediaButtonEvent(this), 1000, 2000);
                }else{
                    headsetClick++;
                }
                break;
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                if (tinyPlayerService.mPlaybackState.getState() == PlaybackStateCompat.STATE_PLAYING) {
                    onPause();
                } else if (tinyPlayerService.mPlaybackState.getState() == PlaybackStateCompat.STATE_PAUSED) {
                    onPlay();
                }
                break;
            case KeyEvent.KEYCODE_MEDIA_PLAY:
                onPlay();
                break;
            case KeyEvent.KEYCODE_MEDIA_PAUSE:
                onPause();
                break;
            case KeyEvent.KEYCODE_MEDIA_NEXT:
                onSkipToNext();
                break;
            case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                onSkipToPrevious();
                break;
        }
        return super.onMediaButtonEvent(mediaButtonEvent);
    }
}
