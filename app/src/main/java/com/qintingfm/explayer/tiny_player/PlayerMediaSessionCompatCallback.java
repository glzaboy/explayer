package com.qintingfm.explayer.tiny_player;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.session.MediaSessionCompat;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;

public class PlayerMediaSessionCompatCallback  extends MediaSessionCompat.Callback {
    WeakReference<TinyPlayerService> tinyPlayerServiceWeakReference;
    List<MediaDescriptionCompat> mediaDescriptionCompatList=new LinkedList<>();
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
    }

    @Override
    public void onPlay() {
        super.onPlay();
        TinyPlayerService tinyPlayerService = tinyPlayerServiceWeakReference.get();
        if(tinyPlayerService.mMediaPlayer==null){
            tinyPlayerService.mMediaPlayer=new MediaPlayer();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
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
        if(tinyPlayerService.mMediaPlayer!=null){
            tinyPlayerService.mMediaPlayer.release();
            tinyPlayerService.mMediaPlayer=null;
        }
    }

    @Override
    public void onAddQueueItem(MediaDescriptionCompat description) {
        super.onAddQueueItem(description);
        mediaDescriptionCompatList.add(description);
    }

    @Override
    public void onAddQueueItem(MediaDescriptionCompat description, int index) {
        super.onAddQueueItem(description, index);
        mediaDescriptionCompatList.add(index,description);
    }

    @Override
    public void onRemoveQueueItem(MediaDescriptionCompat description) {
        super.onRemoveQueueItem(description);
        mediaDescriptionCompatList.remove(description);
    }
}
