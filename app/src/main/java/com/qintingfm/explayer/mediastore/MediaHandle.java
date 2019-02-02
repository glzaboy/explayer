package com.qintingfm.explayer.mediastore;

import android.os.Handler;
import android.os.Message;

public class MediaHandle extends Handler {
    final int Scanf_Local_media=1;
    public MediaHandle() {
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what){
            case Scanf_Local_media:

                break;
        }
        super.handleMessage(msg);
    }
}
