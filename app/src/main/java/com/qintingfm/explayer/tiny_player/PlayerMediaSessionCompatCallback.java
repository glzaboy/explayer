package com.qintingfm.explayer.tiny_player;

import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.session.MediaSessionCompat;

import java.io.IOException;
import java.lang.ref.WeakReference;

public class PlayerMediaSessionCompatCallback  extends MediaSessionCompat.Callback {
    WeakReference<TinyPlayerService> tinyPlayerServiceWeakReference;

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
    private void destroyMediaPlayer(boolean reset){
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
    public void onPrepareFromUri(Uri uri, Bundle extras) {
        super.onPrepareFromUri(uri, extras);
        TinyPlayerService tinyPlayerService = tinyPlayerServiceWeakReference.get();
        try {
            getMediaPlayer(true).setDataSource(tinyPlayerService,uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPlay() {
        super.onPlay();
        TinyPlayerService tinyPlayerService = tinyPlayerServiceWeakReference.get();
        IntentFilter headSetPlug=new IntentFilter();
        headSetPlug.addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY  );
        headSetPlug.addAction(Intent.ACTION_HEADSET_PLUG);
        tinyPlayerService.registerReceiver(tinyPlayerService.headsetPlugReceiver,headSetPlug);
        getMediaPlayer(false).start();
    }

    @Override
    public void onPause() {
        super.onPause();
        getMediaPlayer(false).pause();
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
        tinyPlayerService.unregisterReceiver(tinyPlayerService.headsetPlugReceiver);
        destroyMediaPlayer(true);
    }

    @Override
    public void onAddQueueItem(MediaDescriptionCompat description) {
        super.onAddQueueItem(description);
        TinyPlayerService tinyPlayerService = tinyPlayerServiceWeakReference.get();
        tinyPlayerService.mediaDescriptionCompatList.add(description);
    }

    @Override
    public void onAddQueueItem(MediaDescriptionCompat description, int index) {
        super.onAddQueueItem(description, index);
        TinyPlayerService tinyPlayerService = tinyPlayerServiceWeakReference.get();
        tinyPlayerService.mediaDescriptionCompatList.add(index,description);
    }

    @Override
    public void onRemoveQueueItem(MediaDescriptionCompat description) {
        super.onRemoveQueueItem(description);
        TinyPlayerService tinyPlayerService = tinyPlayerServiceWeakReference.get();
        tinyPlayerService.mediaDescriptionCompatList.remove(description);
    }
}
