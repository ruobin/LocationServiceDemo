package com.disney.shdr.locationservicedemo;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

public class LocalNotificationHelper {

    private static int notificationId = 1001;

    private static boolean isCreateChannel = false;

    public static Notification buildLocalGeofencingNotification(Context context, String title, String content, int smallIcon) {
        Intent intent = new Intent("android.intent.action.MAIN", null);

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder;

        if(android.os.Build.VERSION.SDK_INT >= 26) {

            String channelId = context.getPackageName();
            if(!isCreateChannel) {
                NotificationChannel notificationChannel = new NotificationChannel(channelId,
                        "geofence", NotificationManager.IMPORTANCE_DEFAULT);
                notificationChannel.enableLights(false);
                notificationChannel.setLightColor(Color.BLUE);
                notificationChannel.setShowBadge(true);
                ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(notificationChannel);
                isCreateChannel = true;
            }
            notificationBuilder = new NotificationCompat.Builder(context, channelId);
        } else {
            notificationBuilder = new NotificationCompat.Builder(context);
        }

        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();

        if (!TextUtils.isEmpty(title)) {
            notificationBuilder.setContentTitle(title);
        }
        if (!TextUtils.isEmpty(content)) {
            notificationBuilder.setContentText(content);
        }
        if (smallIcon > 0) {
            notificationBuilder.setSmallIcon(smallIcon);
        } else {
            notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
        }

        notificationBuilder.setWhen(System.currentTimeMillis())
                .setShowWhen(true)
                .setContentIntent(contentIntent)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setStyle(bigTextStyle.bigText(content));
        return notificationBuilder.build();
    }

    public static void sendLocalNotification(Context context, Notification notification) {
        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).notify(notificationId, notification);
        notificationId++;
    }


    public static void sendLocalNotification(Context context, String title, String content) {
        Notification notification = buildLocalGeofencingNotification(context, title, content, R.mipmap.ic_launcher);
        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).notify(notificationId, notification);
        notificationId++;
    }


    public static void sendLocalNotification(Context context, String title, String content, int smallIcon) {
        Notification notification = buildLocalGeofencingNotification(context, title, content, smallIcon);
        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).notify(notificationId, notification);
        notificationId++;
    }
}
