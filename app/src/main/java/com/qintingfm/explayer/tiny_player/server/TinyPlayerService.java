package com.qintingfm.explayer.tiny_player.server;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import com.qintingfm.explayer.RemoteMediaButtonReceiver;
import com.qintingfm.explayer.dao.LocalMediaDao;
import com.qintingfm.explayer.database.MediaStoreDatabase;
import com.qintingfm.explayer.entity.LocalMedia;
import com.qintingfm.explayer.tiny_player.PlayerMediaPlayerListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media.MediaBrowserServiceCompat;
import androidx.room.Room;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TinyPlayerService extends MediaBrowserServiceCompat {
    final String TAG = PlayerMediaPlayerListener.class.getName();
    protected MediaSessionCompat mediaSession;
    public PlaybackStateCompat.Builder stateBuilder = new PlaybackStateCompat.Builder();
    public PlaybackStateCompat mPlaybackState;
    List<MediaMetadataCompat> mediaMetadataCompats = new LinkedList<>();
    protected HeadsetPlugReceiver mHeadsetPlugReceiver;
    public PlayerMediaSessionCompatCallback mMediaSessionCallback;
    PlayerAudioManagerListener mPlayerAudioManagerListener;

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        if (clientPackageName.equalsIgnoreCase(this.getPackageName())) {
            return new MediaBrowserServiceCompat.BrowserRoot(this.getPackageName(), null);
        }
        return new MediaBrowserServiceCompat.BrowserRoot(null, null);
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
        if(parentId.equalsIgnoreCase(this.getPackageName())){//本地媒体使用mediaId进行播放
            List<MediaBrowserCompat.MediaItem> mediaItems=new ArrayList<>();
            for (MediaMetadataCompat m: mediaMetadataCompats
            ) {
                mediaItems.add(new MediaBrowserCompat.MediaItem(m.getDescription(),MediaBrowserCompat.MediaItem.FLAG_PLAYABLE));
            }
            result.sendResult(mediaItems);
        }else{
            result.sendResult(null);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: ");
        mMediaSessionCallback = new PlayerMediaSessionCompatCallback(this);
        mHeadsetPlugReceiver = new HeadsetPlugReceiver(this);
        final MediaStoreDatabase media_store_database = Room.databaseBuilder(this.getApplicationContext(), MediaStoreDatabase.class, "Media Store Database").build();
        final LocalMediaDao localMediaDao = media_store_database.getLocalMediaDao();
        Observable<LocalMedia> localMediaObservable = Observable.create(new ObservableOnSubscribe<LocalMedia>() {
            @Override
            public void subscribe(ObservableEmitter<LocalMedia> e) {
                List<LocalMedia> all = localMediaDao.findAll();
                for (LocalMedia localMedia:all){
                    e.onNext(localMedia);
                }
                e.onComplete();

            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
        localMediaObservable.subscribe(
                new Observer<LocalMedia>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mediaMetadataCompats.clear();
                    }

                    @Override
                    public void onNext(LocalMedia localMedia) {
                        MediaMetadataCompat.Builder builder = new MediaMetadataCompat.Builder();
                        builder.putString(MediaMetadataCompat.METADATA_KEY_ARTIST,localMedia.getArtist());
                        builder.putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID,String.valueOf(localMedia.getId()));
                        builder.putLong(MediaMetadataCompat.METADATA_KEY_DURATION,localMedia.getDuration());
                        builder.putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI,localMedia.getData());
                        builder.putString(MediaMetadataCompat.METADATA_KEY_TITLE,localMedia.getTitle());
                        builder.putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE,localMedia.getDisplayName());
                        Log.d(TAG, "add media:"+builder.toString());
                        mediaMetadataCompats.add(builder.build());
                    }

                    @Override
                    public void onError(Throwable e) {
                    }
                    @Override
                    public void onComplete() {
                        media_store_database.close();
                    }
                }
        );
        mPlayerAudioManagerListener = new PlayerAudioManagerListener(this);
        mediaSession = new MediaSessionCompat(this, this.getClass().getSimpleName());

        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS | MediaSessionCompat.FLAG_HANDLES_QUEUE_COMMANDS);
        mediaSession.setCallback(mMediaSessionCallback);
        setSessionToken(mediaSession.getSessionToken());
        setPlaybackState(stateBuilder.setState(PlaybackStateCompat.STATE_NONE, 0, 1.0f).build());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
        setPlaybackState(stateBuilder.setState(PlaybackStateCompat.STATE_NONE, 0, 1.0f).build());
        mPlayerAudioManagerListener.loseAudioFocus();
        mPlayerAudioManagerListener = null;
        mMediaSessionCallback.onStop();
        mMediaSessionCallback = null;
        mediaSession.release();
        mediaSession = null;
        stateBuilder=null;

    }

    public void setPlaybackState(@Nullable PlaybackStateCompat playBackState) {
        if (playBackState != null) {
            mPlaybackState = playBackState;
        }
        mediaSession.setPlaybackState(mPlaybackState);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        RemoteMediaButtonReceiver.handleIntent(mediaSession, intent);
        if (intent != null && intent.getAction() != null) {
            long aLong = Long.valueOf(intent.getAction());
        }
        return super.onStartCommand(intent, flags, startId);
    }
}
