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
import android.widget.RemoteViews;
import com.qintingfm.explayer.R;

import java.net.URI;

public abstract class NotificationHelp {
    String notifyTag = null;
    Context context = null;
    NotificationCompat.Builder builder=null;

    public NotificationCompat.Builder getBuilder() {
        if(builder==null){
            throw new UnsupportedOperationException("Not yet implemented");
        }
        return builder;
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
        builder.setVisibility(Notification.VISIBILITY_PUBLIC);
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(content).setSummaryText(title).setSummaryText(title));
        return this;
    }
    public NotificationHelp addAction(int icon, java.lang.CharSequence title, android.app.PendingIntent intent){
        getBuilder().addAction(icon,title,intent);
        return this;
    }
    public NotificationHelp setOnClick(android.app.PendingIntent intent){
        getBuilder().setContentIntent(intent);
        return this;
    }
//    public NotificationHelp setLargeIcon(URI uri){
//        Bitmap bitmap = getBitmap(getContext(),rResourceIcon);
//        getBuilder().setLargeIcon(bitmap);
//        return this;
//    }
    public NotificationHelp setLargeIcon(int rResourceIcon){
        Bitmap bitmap = getBitmap(getContext(),rResourceIcon);
        getBuilder().setLargeIcon(bitmap);
        return this;
    }
    public NotificationHelp setCustomContentView(RemoteViews remoteViews){

        return setCustomContentView(remoteViews,null);
    }
    public NotificationHelp setCustomContentView(RemoteViews remoteViews, RemoteViews bigRemoteViews){
        getBuilder()
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle());
        if(bigRemoteViews!=null){
            getBuilder().setCustomBigContentView(bigRemoteViews);
        }
        getBuilder().setCustomContentView(remoteViews);
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