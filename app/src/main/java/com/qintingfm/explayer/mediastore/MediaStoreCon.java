package com.qintingfm.explayer.mediastore;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.*;
import android.util.Log;

import java.util.List;

public class MediaStoreCon {
    private static final String TAG=MediaStoreCon.class.getName();
    protected static Messenger uiMessenger;
    private static Messenger MediaStoreMessenger;
    public static boolean mBound = false;

    static private boolean isServiceRunning(Context packageContext){
        ActivityManager systemService = (ActivityManager)packageContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServices = systemService.getRunningServices(50);
        for (ActivityManager.RunningServiceInfo runningServiceInfo:runningServices) {
            if(runningServiceInfo.service.getClassName().equals(MediaService.class.getName())){
                return true;
            }
        }
        return false;
    }
    public static void startService(Context packageContext){
        if(!isServiceRunning(packageContext)){
            Intent intent=new Intent(packageContext,MediaService.class);
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
            Intent intent=new Intent(packageContext,MediaService.class);
            packageContext.stopService(intent);
            Log.d(TAG,"停止服务：");
        }
    }
    public static synchronized void attach(Context packageContext, Handler handler){
        Intent intent = new Intent(packageContext, MediaService.class);
        packageContext.bindService(intent, MediaStoreCon.mConnection, Context.BIND_AUTO_CREATE);
        MediaStoreCon.uiMessenger=new Messenger(handler);
//        PlayerTimerTask.start();
    }
    public static synchronized void detach(Context packageContext){
        if (MediaStoreCon.uiMessenger!=null) {
            MediaStoreCon.uiMessenger=null;
            packageContext.unbindService(MediaStoreCon.mConnection);
//            PlayerTimerTask.stop();
        }
    }
//    /** Defines callbacks for service binding, passed to bindService() */
    private static ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {

            Log.d(TAG,"连接服务：");
            MediaStoreCon.MediaStoreMessenger=new Messenger(service);
            Message message= Message.obtain();
            try {
                message.what=MediaStoreConstant.HANDLE_READY;
                message.replyTo=MediaStoreCon.uiMessenger;
                MediaStoreCon.MediaStoreMessenger.send(message);
                Message message1=Message.obtain();
                message1.what=MediaStoreConstant.HANDLE_READY;
                MediaStoreCon.uiMessenger.send(message1);


            } catch (RemoteException e) {
                e.printStackTrace();
            }
            MediaStoreCon.mBound=true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Log.d(TAG,"断开服务：");
            if(MediaStoreCon.mBound){

                MediaStoreCon.uiMessenger=null;
                MediaStoreCon.mBound=false;
            }
            MediaStoreCon.MediaStoreMessenger=null;
        }
    };
    public static void scanfLocalMedia(){
        Log.d(TAG,"MediaStoreCon scanfLocalMedia");
        Message message=Message.obtain();
        message.what=MediaStoreConstant.HANDLE_SCAN_LOCAL;
        message.replyTo=MediaStoreCon.uiMessenger;
        try {
            MediaStoreCon.MediaStoreMessenger.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

}
