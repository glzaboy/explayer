package com.qintingfm.explayer.player;

import android.content.Context;
import com.qintingfm.explayer.notification.NotificationHelp;

public class PlayerNotification extends NotificationHelp {
    public PlayerNotification(Context context, String notifyTag) {
        super(context, notifyTag);
    }

    public PlayerNotification(Context context) {
        this(context,null);
    }
}
