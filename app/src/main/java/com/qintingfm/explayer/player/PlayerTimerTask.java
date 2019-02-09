package com.qintingfm.explayer.player;

import java.util.Timer;
import java.util.TimerTask;

public class PlayerTimerTask {
    static final String TAG=PlayerTimerTask.class.getName();
    static Timer timer;
    static TimerTask task;
    public static void start(){
        if(PlayerTimerTask.timer!=null){
            PlayerTimerTask.stop();
        }
        PlayerTimerTask.timer=new Timer();
        PlayerTimerTask.task=new PlayerSeekTask();
        PlayerTimerTask.timer.schedule(PlayerTimerTask.task,0,1000);
    }
    public static void stop(){
        if(PlayerTimerTask.timer!=null){
            PlayerTimerTask.timer.cancel();
            PlayerTimerTask.timer=null;
            PlayerTimerTask.task=null;
        }
    }
}
