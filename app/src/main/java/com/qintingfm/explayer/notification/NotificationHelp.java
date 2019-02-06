package com.qintingfm.explayer.notification;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import com.qintingfm.explayer.R;

public abstract class NotificationHelp {
    String notifyTag = null;
    Context context = null;
    NotificationCompat.Builder builder=null;

    public NotificationCompat.Builder getBuilder() {
        return builder;
    }

    public void setBuilder(NotificationCompat.Builder builder) {
        this.builder = builder;
    }

    public NotificationHelp(Context context, String notifyTag) {
        if(notifyTag==null){
            notifyTag=this.getClass().getSimpleName();
        }
        this.notifyTag = notifyTag;
        this.context = context;
    }

    public String getNotifyTag() {
        return notifyTag;
    }

    public void setNotifyTag(String notifyTag) {
        this.notifyTag = notifyTag;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
    public NotificationHelp getDefault(int rResourceIcon, String title, String content){
        NotificationChannel notifyChannel = createNotifyChannel();

        if (notifyChannel != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getManager().createNotificationChannel(notifyChannel);
            builder = new NotificationCompat.Builder(getContext(), notifyChannel.getId());
        } else {
            builder = new NotificationCompat.Builder(getContext());
        }
        builder.setDefaults(Notification.DEFAULT_ALL);
        builder.setSmallIcon(rResourceIcon);
        builder.setContentTitle(title);
        builder.setContentText(content);
        builder.setTicker(content);
        builder.setNumber(1);
        builder.setWhen(System.currentTimeMillis());
        builder.setAutoCancel(true);
        Bitmap bitmap = getBitmap(context,rResourceIcon);
        builder.setLargeIcon(bitmap);
        return this;
    }
    public NotificationHelp setNubmber(int number){
        getBuilder().setNumber(number);
        return this;
    }
    /**
     * 将r.drawable中的资源转成bitmap;
     * @param context
     * @param vectorDrawableId
     * @return
     */
    public Bitmap getBitmap(Context context,int vectorDrawableId) {
        Bitmap bitmap=null;
        if (Build.VERSION.SDK_INT>Build.VERSION_CODES.LOLLIPOP){
            Drawable vectorDrawable = context.getDrawable(vectorDrawableId);
            bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                    vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            vectorDrawable.draw(canvas);
        }else {
            bitmap = BitmapFactory.decodeResource(context.getResources(), vectorDrawableId);
        }
        return bitmap;
    }


    public NotificationManager getManager() {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        return notificationManager;
    }


    @TargetApi(Build.VERSION_CODES.ECLAIR)
    public void notify(final Context context, final Notification notification) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            getManager().notify(notifyTag, 0, notification);
        } else {
            getManager().notify(notifyTag.hashCode(), notification);
        }
    }

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    public void cancel(final Context context, String tag) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            getManager().cancel(notifyTag, 0);
        } else {
            getManager().cancel(notifyTag.hashCode());
        }
    }

    public NotificationChannel createNotifyChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(String.valueOf(notifyTag.hashCode()), notifyTag, NotificationManager.IMPORTANCE_DEFAULT);
            return notificationChannel;
        }
        return null;
    }
}
