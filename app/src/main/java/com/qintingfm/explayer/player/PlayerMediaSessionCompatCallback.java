package com.qintingfm.explayer.player;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.KeyEvent;
import com.qintingfm.explayer.entity.LocalMedia;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Timer;

public class PlayerMediaSessionCompatCallback extends MediaSessionCompat.Callback {
    final String TAG = PlayerMediaSessionCompatCallback.class.getName();
    WeakReference<PlayerService> playerServiceWeakReference;

    public PlayerMediaSessionCompatCallback(PlayerService playerService) {
        this.playerServiceWeakReference = new WeakReference<>(playerService);
    }

    public int headsetClick = 0;

    public Timer headsetTimer = new Timer();

    public boolean isFinishHeadsetClick = false;

    @Override
    public void onCommand(String command, Bundle extras, ResultReceiver cb) {
        super.onCommand(command, extras, cb);
    }

    @Override
    public void onPlayFromUri(Uri uri, Bundle extras) {
        PlayerService playerService = playerServiceWeakReference.get();
        if (extras.getString("title") == null) {
            extras.putString("title", "Ex player No title");
        }
        playerService.mediaPlayer.reset();
        try {
            playerService.mediaPlayer.setDataSource(uri.getPath());
            playerService.mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
        playerService.bundle = extras;
        super.onPlayFromUri(uri, extras);
    }

    @Override
    public void onPlayFromMediaId(String mediaId, Bundle extras) {
        PlayerService playerService = playerServiceWeakReference.get();
        if (extras.getString("title") == null) {
            extras.putString("title", "No title");
        }
        playerService.bundle = extras;
        super.onPlayFromMediaId(mediaId, extras);
    }

    @Override
    public void onPlayFromSearch(String query, Bundle extras) {
        PlayerService playerService = playerServiceWeakReference.get();
        if (extras.getString("title") == null) {
            extras.putString("title", "Ex player query");
        }
        if (extras.getString("artist") == null) {
            extras.putString("artist", null);
        }
        playerService.bundle = extras;
        super.onPlayFromSearch(query, extras);
    }

    @Override
    public void onPrepare() {
        super.onPrepare();
    }

    @Override
    public void onPlay() {
        PlayerService playerService = playerServiceWeakReference.get();
        super.onPlay();
        switch (playerService.mPlaybackStateCompat.getState()) {
            case PlaybackStateCompat.STATE_PAUSED:
                playerService.mPlaybackStateCompat = playerService.mPlaybackBuilder.setState(PlaybackStateCompat.STATE_PLAYING, playerService.mediaPlayer.getCurrentPosition(), 1.0f).build();
                playerService.mediaSessionCompat.setPlaybackState(playerService.mPlaybackStateCompat);
                playerService.mediaPlayer.start();
                playerService.playerNotification.updateNotify();
                break;
            default:
        }
    }

    @Override
    public void onPause() {
        PlayerService playerService = playerServiceWeakReference.get();
        super.onPause();
        switch (playerService.mPlaybackStateCompat.getState()) {
            case PlaybackStateCompat.STATE_PLAYING:
                playerService.mPlaybackStateCompat = playerService.mPlaybackBuilder.setState(PlaybackStateCompat.STATE_PAUSED, playerService.mediaPlayer.getCurrentPosition(), 1.0f).build();
                playerService.mediaSessionCompat.setPlaybackState(playerService.mPlaybackStateCompat);
                playerService.mediaPlayer.pause();
                playerService.playerNotification.updateNotify();
                break;
            default:
        }
    }

    @Override
    public void onSkipToNext() {
        PlayerService playerService = playerServiceWeakReference.get();
        super.onSkipToNext();
        LocalMedia position = playerService.localMediaDao.findNext(playerService.bundle.getInt("position"));
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
        PlayerService playerService = playerServiceWeakReference.get();
        super.onSkipToPrevious();
        LocalMedia position = playerService.localMediaDao.findPrev(playerService.bundle.getInt("position"));
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
        PlayerService playerService = playerServiceWeakReference.get();
        super.onSeekTo(pos);
        switch (playerService.mPlaybackStateCompat.getState()) {
            case PlaybackStateCompat.STATE_PLAYING:
                playerService.mPlaybackStateCompat = playerService.mPlaybackBuilder.setState(PlaybackStateCompat.STATE_REWINDING, pos, 1.0f).build();
                playerService.mediaSessionCompat.setPlaybackState(playerService.mPlaybackStateCompat);
                playerService.mediaPlayer.seekTo((int) pos);
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
        PlayerService playerService = playerServiceWeakReference.get();
        super.onStop();
        switch (playerService.mPlaybackStateCompat.getState()) {
            case PlaybackStateCompat.STATE_PLAYING:
                playerService.mPlaybackStateCompat = playerService.mPlaybackBuilder.setState(PlaybackStateCompat.STATE_STOPPED, playerService.mediaPlayer.getCurrentPosition(), 1.0f).build();
                playerService.mediaSessionCompat.setPlaybackState(playerService.mPlaybackStateCompat);
                playerService.mediaPlayer.stop();
                playerService.playerNotification.updateNotify();
                break;
            default:
        }


    }

    @Override
    public boolean onMediaButtonEvent(Intent mediaButtonEvent) {
        PlayerService playerService = playerServiceWeakReference.get();
        String action = mediaButtonEvent.getAction();
        KeyEvent keyEvent = (KeyEvent) mediaButtonEvent.getExtras().get(Intent.EXTRA_KEY_EVENT);
        switch (keyEvent.getKeyCode()) {
            case KeyEvent.KEYCODE_HEADSETHOOK://单键耳机单独点击播放或暂停，双击下一首，三击上一首。
                int action1 = keyEvent.getAction();
                if(action1==KeyEvent.ACTION_DOWN){
                    return super.onMediaButtonEvent(mediaButtonEvent);
                }
                if (headsetClick == 0) {
//                    isFinishHeadsetClick = false;
                    headsetClick++;
                    headsetTimer.schedule(new PlayerHeadsetClick(this), 1000, 2000);
                }else{
                    headsetClick++;
                }

//                if (isFinishHeadsetClick == true) {
//                    switch (headsetClick) {
//                        case 1:
//                            if (playerService.mPlaybackStateCompat.getState() == PlaybackStateCompat.STATE_PLAYING) {
//                                playerService.mediaControllerCompat.getTransportControls().pause();
//                            } else if (playerService.mPlaybackStateCompat.getState() == PlaybackStateCompat.STATE_PAUSED) {
//                                playerService.mediaControllerCompat.getTransportControls().play();
//                            }
//                            break;
//                        case 2:
//                            playerService.mediaControllerCompat.getTransportControls().skipToNext();
//                            break;
//                        case 3:
//                            playerService.mediaControllerCompat.getTransportControls().skipToPrevious();
//                            break;
//                        default:
//
//                    }
//                    headsetClick = 0;
//                    isFinishHeadsetClick = false;
//                }


                break;
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                if (playerService.mPlaybackStateCompat.getState() == PlaybackStateCompat.STATE_PLAYING) {
                    playerService.mediaControllerCompat.getTransportControls().pause();
                } else if (playerService.mPlaybackStateCompat.getState() == PlaybackStateCompat.STATE_PAUSED) {
                    playerService.mediaControllerCompat.getTransportControls().play();
                }
                break;
            case KeyEvent.KEYCODE_MEDIA_PLAY:
                playerService.mediaControllerCompat.getTransportControls().play();
                break;
            case KeyEvent.KEYCODE_MEDIA_PAUSE:
                playerService.mediaControllerCompat.getTransportControls().pause();
                break;
            case KeyEvent.KEYCODE_MEDIA_NEXT:
                playerService.mediaControllerCompat.getTransportControls().skipToNext();
                break;
            case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                playerService.mediaControllerCompat.getTransportControls().skipToPrevious();
                break;
        }
        Log.i(TAG, "onMediaButtonEvent: " + mediaButtonEvent.getAction());
        return super.onMediaButtonEvent(mediaButtonEvent);
    }
}
