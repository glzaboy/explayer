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
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;


public abstract class NotificationHelp {
    private String notifyTag ;
    private Context context;
    private NotificationCompat.Builder builder=null;
    private NotificationChannel notifyChannel;

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
        setNotifyTag(notifyTag);
        this.context = context;
    }

    public String getNotifyTag() {
        return notifyTag;
    }

    public void setNotifyTag(String notifyTag) {
        this.notifyTag = notifyTag;
    }

    protected Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
    public NotificationHelp getDefault(int rResourceIcon, String title, String content){
        initNotifyChannel();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new NotificationCompat.Builder(getContext(), notifyChannel.getId());
        } else {
            builder = new NotificationCompat.Builder(getContext());
        }
        builder.setDefaults(NotificationCompat.DEFAULT_LIGHTS);
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setSmallIcon(rResourceIcon);
        builder.setContentTitle(title);
        builder.setContentText(content);
        builder.setTicker(content);
        builder.setNumber(1);

        builder.setWhen(System.currentTimeMillis());
        builder.setAutoCancel(true);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        }
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(content).setSummaryText(title).setSummaryText(title));
        return this;
    }
    public NotificationHelp addAction(int icon, java.lang.CharSequence title, android.app.PendingIntent intent){
        getBuilder().addAction(icon,title,intent);
        return this;
    }
    public NotificationHelp setOngoing(boolean ongoing){
        getBuilder().setOngoing(ongoing);
        return this;
    }
    public NotificationHelp addAction(NotificationCompat.Action action){
        getBuilder().addAction(action);
        return this;
    }
    public NotificationHelp setOnClick(android.app.PendingIntent intent){
        getBuilder().setContentIntent(intent);
        return this;
    }
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
    public NotificationHelp setStyle(NotificationCompat.Style style){
        getBuilder().setStyle(style);
        return this;
    }
    /**
     * 将r.drawable中的资源转成bitmap;
     * @param context
     * @param vectorDrawableId 资源ID
     * @return
     */
    public Bitmap getBitmap(Context context,int vectorDrawableId) {
        Bitmap bitmap;
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
    public void notify(final Notification notification) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            getManager().notify(getNotifyTag(), 0, notification);
        } else {
            getManager().notify(getNotifyTag().hashCode(), notification);
        }
    }

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    public void cancel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            getManager().cancel(getNotifyTag(), 0);
        } else {
            getManager().cancel(getNotifyTag().hashCode());
        }
    }
    private void initNotifyChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if(notifyChannel!=null){
                return;
            }
            notifyChannel=new NotificationChannel(String.valueOf(getNotifyTag().hashCode()), getNotifyTag(), NotificationManager.IMPORTANCE_DEFAULT);
            getManager().createNotificationChannel(notifyChannel);
        }
    }
}
