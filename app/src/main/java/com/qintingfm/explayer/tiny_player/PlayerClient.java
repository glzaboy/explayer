package com.qintingfm.explayer.tiny_player;

import android.support.v4.media.MediaBrowserCompat;
import androidx.appcompat.app.AppCompatActivity;

import java.lang.ref.WeakReference;

public class PlayerClient extends MediaBrowserCompat.ConnectionCallback {
    WeakReference<AppCompatActivity> appCompatActivityWeakReference;

}
