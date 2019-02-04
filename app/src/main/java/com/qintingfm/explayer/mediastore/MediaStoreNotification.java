package com.qintingfm.explayer.mediastore;

import android.content.Context;
import com.qintingfm.explayer.notification.NotificationHelp;

public class MediaStoreNotification extends NotificationHelp {
    public MediaStoreNotification(Context context, String notifyTag) {
        super(context, notifyTag);
    }

    public MediaStoreNotification(Context context) {
        this(context,null);
    }
}
