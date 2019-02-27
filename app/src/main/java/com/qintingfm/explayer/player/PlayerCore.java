package com.qintingfm.explayer.player;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.*;
import android.util.Log;

import java.util.List;

public class PlayerCore {
    private static final String TAG=PlayerCore.class.getName();
    private static Messenger MusicMessenger;
    private static Messenger uiMessenger;
    private static  boolean isServiceRunning(Context packageContext){
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
    }
    public static synchronized void detach(Context packageContext){
        if (PlayerCore.uiMessenger!=null) {
            PlayerCore.uiMessenger=null;
            packageContext.unbindService(mConnection);
        }
    }
    /** Defines callbacks for service binding, passed to bindService() */
    private static ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            PlayerCore.MusicMessenger = new Messenger(service);
            Message message=Message.obtain();
            message.what=PlayerEvent.PLAYER_UI_ATTACH;
            message.replyTo=PlayerCore.uiMessenger;
            Log.d(TAG,"Player SERVICE READY ");
            try {
                PlayerCore.MusicMessenger.send(message);
                if(PlayerCore.uiMessenger!=null){
                    Message message2=Message.obtain();
                    message2.what= PlayerEvent.PLAYER_UI_ATTACH;
                    PlayerCore.uiMessenger.send(message2);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            PlayerCore.MusicMessenger=null;
            PlayerCore.uiMessenger=null;
        }
    };




}
