package com.qintingfm.explayer.mediastore;

import android.app.PendingIntent;
import android.app.Service;
import android.arch.persistence.room.Room;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.*;
import android.provider.MediaStore;
import android.util.Log;
import com.qintingfm.explayer.R;
import com.qintingfm.explayer.activity.NavActivity;
import com.qintingfm.explayer.dao.LocalMediaDao;
import com.qintingfm.explayer.database.MediaStoreDatabase;
import com.qintingfm.explayer.entity.LocalMedia;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;


public class MediaService extends Service {
    private static final String TAG=MediaService.class.getName();
    Messenger mMessenger=new Messenger(new MediaHandle());

    public MediaService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        Stetho.initializeWithDefaults(this.getApplicationContext());
        MediaStoreNotification mediaStoreNotification=new MediaStoreNotification(this.getApplicationContext(),"MediaService");
        mediaStoreNotification.getDefault(R.drawable.ic_music_black_24dp, "MediaService", " Media Store scan and update media").setOnClick(
                PendingIntent.getActivity(this,0x001,new Intent(this.getApplicationContext(),NavActivity.class),PendingIntent.FLAG_UPDATE_CURRENT)
        );
        startForeground(1000,mediaStoreNotification.getBuilder().build());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }
    private void scanfLocalMedia(){
        final MediaStoreDatabase media_store_database = Room.databaseBuilder(this.getApplicationContext(), MediaStoreDatabase.class, "Media Store Database").allowMainThreadQueries().build();
        media_store_database.clearAllTables();
        ContentResolver contentResolver = getContentResolver();
        String[] values = {MediaStore.Audio.Media.DATA,MediaStore.Audio.Media.DISPLAY_NAME,MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.TITLE,MediaStore.Audio.Media.ARTIST};
        final LocalMediaDao localMediaDao = media_store_database.getLocalMediaDao();
        final Cursor query = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values, "is_music > 0 ", null, null);

        Observable<LocalMedia> localMediaObservable = Observable.create(new ObservableOnSubscribe<LocalMedia>() {
            @Override
            public void subscribe(ObservableEmitter<LocalMedia> subscriber)  {
                if(query.moveToFirst()){
                    int indexData=query.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
                    int indexDisplayName=query.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);
                    int indexDuration=query.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION);
                    int indexTitle=query.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
                    int indexArtist=query.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);
                    do{
                        LocalMedia localMedia=new LocalMedia();
                        localMedia.setData(query.getString(indexData));
                        localMedia.setDisplayName(query.getString(indexDisplayName));
                        localMedia.setDuration(query.getInt(indexDuration));
                        localMedia.setTitle(query.getString(indexTitle));
                        localMedia.setArtist(query.getString(indexArtist));
                        localMedia.setPlayCount(0);
                        subscriber.onNext(localMedia);
                    }
                    while (query.moveToNext());
                }
                query.close();




                subscriber.onComplete();
            }
        });
        localMediaObservable.subscribe(new Observer<LocalMedia>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(LocalMedia localMedia) {
                localMediaDao.insertMedia(localMedia);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                media_store_database.close();
                Message message=Message.obtain();
                message.what=MediaStoreConstant.HANDLE_SCAN_FINISHED;
                try {
                    MediaStoreCon.uiMessenger.send(message);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

    }
    private final class MediaHandle extends Handler {
        private  final String TAG= MediaHandle.class.getName();
        public MediaHandle() {
        }

        @Override
        public void handleMessage(Message msg) {
            if(msg.replyTo!=null){
                MediaStoreCon.uiMessenger=msg.replyTo;
            }else{
                MediaStoreCon.uiMessenger=null;
            }
            switch (msg.what){
                case MediaStoreConstant.HANDLE_SCAN_LOCAL:
                    Log.d(TAG,"MediaStoreConstant.HANDLE_SCAN_LOCAL"+MediaService.this.getPackageName());
                    MediaService.this.scanfLocalMedia();
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    }
}
