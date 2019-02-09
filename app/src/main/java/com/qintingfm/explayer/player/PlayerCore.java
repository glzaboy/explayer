package com.qintingfm.explayer.player;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerCore {
    private static final String TAG=PlayerCore.class.getName();
    public static boolean mBound = false;
    public static Messenger MusicMessenger;
    static MediaPlayer player;
    static PlayerEvent playerEvent;
    static String url;
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
            PlayerCore.playerEvent=new PlayerEvent();
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

    public static void openUrl2(String url) throws IOException {
        if(PlayerCore.player==null){
            PlayerCore.player=new MediaPlayer();
            PlayerCore.player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }
        PlayerCore.player.reset();
        PlayerCore.player.setDataSource(url);
        PlayerCore.player.setOnPreparedListener(PlayerCore.playerEvent);
        PlayerCore.player.setOnErrorListener(PlayerCore.playerEvent);
        PlayerCore.player.setOnBufferingUpdateListener(PlayerCore.playerEvent);
        PlayerCore.player.setOnCompletionListener(PlayerCore.playerEvent);
        PlayerCore.player.prepareAsync();
        PlayerCore.url=url;
        PlayerTimerTask.start();
    }


    protected static void destroyPlayer(){
        if(PlayerCore.player!=null){
            if(PlayerCore.player.isPlaying()){
                PlayerCore.player.stop();
            }
            PlayerCore.player.release();
            PlayerCore.player=null;
        }
    }


    private static  void seek2(int msec) {
        if(PlayerCore.player!=null){
            PlayerCore.player.seekTo(msec);
        }
    }
    private static void stop2() {
        if(PlayerCore.player!=null){
            if(PlayerCore.uiMessenger!=null){
                Message obtain = Message.obtain();
                obtain.what=PlayerEumu.HANDLER_DISABLE;
                PlayerTimerTask.stop();
                try {
                    PlayerCore.uiMessenger.send(obtain);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            PlayerTimerTask.stop();
            PlayerCore.player.stop();
            PlayerCore.player.release();
            PlayerCore.player=null;
        }
    }
    private static  void pause2() {
        Log.d(TAG,"music pauseing ");
        if(PlayerCore.player!=null && PlayerCore.player.isPlaying()){
            PlayerCore.player.pause();
            PlayerTimerTask.stop();
        }
    }
    private static void play2() {
        if( PlayerCore.url!=null  && PlayerCore.player!=null && !PlayerCore.player.isPlaying()){
            PlayerCore.player.start();
            PlayerTimerTask.start();
        }
    }
    private static void trigger2(){
        if(PlayerCore.url!=null && PlayerCore.player!=null){
            if(PlayerCore.player.isPlaying()){
                PlayerCore.player.pause();
                PlayerTimerTask.stop();
            }else{
                PlayerCore.player.start();
                PlayerTimerTask.start();
            }
        }
    }





    public static synchronized void attach(Context packageContext, Handler handler){
        Intent intent = new Intent(packageContext, PlayerService.class);
        packageContext.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        PlayerCore.uiMessenger=new Messenger(handler);
        PlayerTimerTask.start();
    }
    public static synchronized void detach(Context packageContext){
        if (PlayerCore.uiMessenger!=null) {
            PlayerCore.uiMessenger=null;
            packageContext.unbindService(mConnection);
            PlayerTimerTask.stop();
        }
    }
    public static synchronized void openUrl(String url){
        if(PlayerCore.uiMessenger!=null){
            Message message=Message.obtain();
            message.what=PlayerEumu.HANDLE_OPEN_URL;
            HashMap<String,String> hashMap=new HashMap<>();
            hashMap.put("url",url);
            message.obj=hashMap;
            message.replyTo=PlayerCore.uiMessenger;
            try {
                Log.d(TAG,"Player Message"+message.toString());
                PlayerCore.MusicMessenger.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
    public static void stop(){
        if(PlayerCore.uiMessenger!=null){
            Message message=Message.obtain();
            message.what=PlayerEumu.HANDLE_STOP;
            message.replyTo=PlayerCore.uiMessenger;
            try {
                PlayerCore.MusicMessenger.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
    public static void seek(int msec){
        if(PlayerCore.uiMessenger!=null){
            Message message=Message.obtain();
            message.what=PlayerEumu.HANDLE_SEEK;
            message.obj=msec;
            message.replyTo=PlayerCore.uiMessenger;
            try {
                PlayerCore.MusicMessenger.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
    public static void play(){
        if(PlayerCore.uiMessenger!=null){
            Message message=Message.obtain();
            message.what=PlayerEumu.HANDLE_PLAY;
            message.replyTo=PlayerCore.uiMessenger;
            try {
                PlayerCore.MusicMessenger.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
    public static void pause(){
        if(PlayerCore.uiMessenger!=null){
            Message message=Message.obtain();
            message.what=PlayerEumu.HANDLE_PAUSE;
            message.replyTo=PlayerCore.uiMessenger;
            try {
                PlayerCore.MusicMessenger.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
    public static void trigger(){
        if(PlayerCore.uiMessenger!=null){
            Message message=Message.obtain();
            message.what=PlayerEumu.HANDLE_TRIGGER;
            message.replyTo=PlayerCore.uiMessenger;
            try {
                PlayerCore.MusicMessenger.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
    /** Defines callbacks for service binding, passed to bindService() */
    private static ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            PlayerCore.MusicMessenger = new Messenger(service);
            Message message=Message.obtain();
            message.what=PlayerEumu.HANDLER_PLAYERREADY;
            message.replyTo=PlayerCore.uiMessenger;
            Log.d(TAG,"Player SERVICE READY ");
            try {
                PlayerCore.MusicMessenger.send(message);
                Message message2=Message.obtain();
                message2.what=PlayerEumu.HANDLER_PLAYERREADY;
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
        }
    };
    static final Messenger mMessenger = new Messenger(new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.replyTo!=null){
                PlayerCore.uiMessenger=msg.replyTo;
            }else{
                PlayerCore.uiMessenger=null;
            }
            switch (msg.what){
                case PlayerEumu.HANDLE_OPEN_URL:
                    Map<String, String> obj = (Map<String, String>) msg.obj;
                    if(obj.get("url")!=null){
                        try {
                            Log.d(TAG,"music start play{}"+obj.get("url"));
                            PlayerCore.openUrl2(obj.get("url"));
                        } catch (IOException e) {
                            Log.d(TAG,"music start error{}"+e.getMessage());
                            e.printStackTrace();
                        }
                    }
                    break;
                case PlayerEumu.HANDLE_STOP:
                    PlayerCore.stop2();
                    break;
                case PlayerEumu.HANDLE_SEEK:
                    PlayerCore.seek2((int)msg.obj);
                    break;
                case PlayerEumu.HANDLE_PLAY:
                    PlayerCore.play2();
                    break;
                case PlayerEumu.HANDLE_PAUSE:
                    Log.d(TAG,"music pause");
                    PlayerCore.pause2();
                    break;
                case PlayerEumu.HANDLE_TRIGGER:
                    PlayerCore.trigger2();
                    break;
                default:

            }
            super.handleMessage(msg);
        }
    });




}
