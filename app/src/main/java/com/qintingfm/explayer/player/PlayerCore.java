package com.qintingfm.explayer.player;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.*;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import java.util.List;

public class PlayerCore {
    private static final String TAG=PlayerCore.class.getName();
    public static boolean mBound = false;
    public static Messenger MusicMessenger;
//    static PlayerEvent playerEvent;
//    static String url;
    static Messenger uiMessenger;
    static private boolean isServiceRunning(Context packageContext){
        ActivityManager systemService = (ActivityManager)packageContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServices = systemService.getRunningServices(50);
        for (ActivityManager.RunningServiceInfo runningServiceInfo:runningServices) {
            if(runningServiceInfo.service.getClassName().equals(PlayerService.class.getName())){
                return true;
            }
        }
        return false;
    }

    public static void startService(Context packageContext){
        if(!isServiceRunning(packageContext)){
            Intent intent=new Intent(packageContext,PlayerService.class);
            packageContext.startService(intent);
//            PlayerCore.playerEvent=new PlayerEvent();
            Log.d(TAG,"启动服务：");
        }else{
            Log.d(TAG,"启动服务：服务已经在运行");
        }

    }
    public static void stopService(Context packageContext){
        if(!isServiceRunning(packageContext)){
            Log.d(TAG,"停止服务:未在运行");
        }else{
            Intent intent=new Intent(packageContext,PlayerService.class);
            packageContext.stopService(intent);
            Log.d(TAG,"停止服务：");
        }
    }
    public static synchronized void attach(Context packageContext, Handler handler){
        Intent intent = new Intent(packageContext, PlayerService.class);
        packageContext.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        PlayerCore.uiMessenger=new Messenger(handler);
//        PlayerTimerTask.start();
    }
    public static synchronized void detach(Context packageContext){
        if (PlayerCore.uiMessenger!=null) {
            PlayerCore.uiMessenger=null;
            packageContext.unbindService(mConnection);
//            PlayerTimerTask.stop();
        }
    }
    /** Defines callbacks for service binding, passed to bindService() */
    private static ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            PlayerCore.MusicMessenger = new Messenger(service);
            Message message=Message.obtain();
            message.what=PlaybackStateCompat.STATE_NONE;
            message.replyTo=PlayerCore.uiMessenger;
            Log.d(TAG,"Player SERVICE READY ");
            try {
                PlayerCore.MusicMessenger.send(message);
                Message message2=Message.obtain();
                message2.what= PlaybackStateCompat.STATE_NONE;
                PlayerCore.uiMessenger.send(message2);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
            PlayerCore.MusicMessenger=null;
            PlayerCore.uiMessenger=null;
        }
    };




}
