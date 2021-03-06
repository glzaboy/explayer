package com.qintingfm.explayer;

import android.content.Context;
import android.content.Intent;
import android.support.v4.media.session.PlaybackStateCompat;

import com.qintingfm.explayer.tiny_player.server.TinyPlayerService;

import androidx.media.session.MediaButtonReceiver;

public class RemoteMediaButtonReceiver extends MediaButtonReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
//        throw new UnsupportedOperationException("Not yet implemented");
        Intent playerServiceIntent = new Intent(context, TinyPlayerService.class);
        playerServiceIntent.setAction(String.valueOf(PlaybackStateCompat.ACTION_PLAY_PAUSE));
        context.startService(playerServiceIntent);
    }
}
