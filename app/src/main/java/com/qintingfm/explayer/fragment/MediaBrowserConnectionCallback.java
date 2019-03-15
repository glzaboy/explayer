package com.qintingfm.explayer.fragment;

import android.os.RemoteException;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;
import androidx.appcompat.app.AppCompatActivity;
import com.qintingfm.explayer.activity.NavActivity;

import java.lang.ref.WeakReference;

public class MediaBrowserConnectionCallback extends MediaBrowserCompat.ConnectionCallback {
    WeakReference<MediaBrowserCompat> mediaBrowserCompatWeakReference;
    WeakReference<AppCompatActivity> appCompatActivityWeakReference;
    public MediaBrowserConnectionCallback() {
    }

    public WeakReference<MediaBrowserCompat> getMediaBrowserCompatWeakReference() {
        return mediaBrowserCompatWeakReference;
    }

    public void setMediaBrowserCompatWeakReference(MediaBrowserCompat mediaBrowserCompat) {
        mediaBrowserCompatWeakReference=new WeakReference<>(mediaBrowserCompat);
    }

    public WeakReference<AppCompatActivity> getAppCompatActivityWeakReference() {
        return appCompatActivityWeakReference;
    }

    public void setAppCompatActivityWeakReference(AppCompatActivity appCompatActivity) {
        appCompatActivityWeakReference=new WeakReference<>(appCompatActivity);
    }

    @Override
    public void onConnected() {
        super.onConnected();
        MediaBrowserCompat mediaBrowserCompat = mediaBrowserCompatWeakReference.get();
        AppCompatActivity appCompatActivity = appCompatActivityWeakReference.get();

        try {
            MediaControllerCompat mediaControllerCompat=new MediaControllerCompat(appCompatActivity,mediaBrowserCompat.getSessionToken());
//            buildTransportControls();
        } catch (RemoteException e) {


        }
    }

    @Override
    public void onConnectionSuspended() {
        super.onConnectionSuspended();
    }

    @Override
    public void onConnectionFailed() {
        super.onConnectionFailed();
    }
}
