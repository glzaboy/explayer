package com.qintingfm.explayer.tiny_player.server.ui;

import android.content.Context;

import com.qintingfm.explayer.notification.NotificationHelp;
import com.qintingfm.explayer.tiny_player.server.TinyPlayerService;

import java.lang.ref.WeakReference;

public class PlayerNotification extends NotificationHelp {
    private WeakReference<TinyPlayerService> tinyPlayerServiceWeakReference;
    public PlayerNotification(Context context, String notifyTag) {
        super(context, notifyTag);
    }

    public WeakReference<TinyPlayerService> getTinyPlayerServiceWeakReference() {
        return tinyPlayerServiceWeakReference;
    }

    public void setTinyPlayerServiceWeakReference(WeakReference<TinyPlayerService> tinyPlayerServiceWeakReference) {
        this.tinyPlayerServiceWeakReference = tinyPlayerServiceWeakReference;
    }
}
