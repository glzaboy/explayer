package com.qintingfm.explayer.player;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;
import com.qintingfm.explayer.R;


public class PlayerService extends Service {
    static final String TAG = PlayerService.class.getName();

    public PlayerService() {
    }



    @Override
    public IBinder onBind(Intent intent) {
        Toast.makeText(getApplicationContext(), "binding", Toast.LENGTH_SHORT).show();
        return PlayerCore.mMessenger.getBinder();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NotificationChannel notificationChannel = new NotificationChannel("PlayerCoreNotify", "App Service", NotificationManager.IMPORTANCE_DEFAULT);
            nm.createNotificationChannel(notificationChannel);
        }
//        Notification.Builder builder = new Notification.Builder(this.getApplicationContext(), "PlayerCoreNotify");
////        builder.setContentText("Player Core");
//        builder.setContentTitle("我是标题")
//                //设置内容
//                .setContentText("我是内容")
//                //设置大图标
////                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
//                //设置小图标
////                .setSmallIcon(R.mipmap.ic_launcher_round)
//                //设置通知时间
//                .setWhen(System.currentTimeMillis())
//                //首次进入时显示效果
//                .setTicker("我是测试内容")
//                //设置通知方式，声音，震动，呼吸灯等效果，这里通知方式为声音
//                .setDefaults(Notification.DEFAULT_SOUND);
//        startForeground(100, builder.build());
////        NewMessageNotification.notify(this.getApplicationContext(),"100",100);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        PlayerNotification playerNotificatio=new PlayerNotification(this.getApplicationContext(),"Ex Player Core");
        playerNotificatio.getDefault(R.drawable.ic_music_black_24dp,"Explayer","播放");
        startForeground(100,playerNotificatio.getBuilder().build());




        Log.d(TAG, "PlayerService onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
        Log.d(TAG, "PlayerService onDestroy");
        PlayerCore.destroyPlayer();
    }
}
