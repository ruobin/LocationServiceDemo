package com.disney.shdr.locationservicedemo;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;


public class LocationJobService extends JobService {


    @Override
    public boolean onStartJob(JobParameters params) {
        sendLocalNotification(getApplicationContext(), "Location Job", "on start");
        getCurrentGPSLocation(LocationManager.NETWORK_PROVIDER, getApplicationContext());
        getCurrentGPSLocation(LocationManager.GPS_PROVIDER, getApplicationContext());

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        sendLocalNotification(getApplicationContext(), "Location Job", "on stop");
        return false;
    }

    public static void sendLocalNotification(Context context, String title, String content) {
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder b = new NotificationCompat.Builder(context);
        String CHANNEL_ID = "my_channel_01";// The id of the channel.
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, "default notification channel", importance);

        b.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(title)
                .setContentText(content)
                .setDefaults(Notification.DEFAULT_LIGHTS| Notification.DEFAULT_SOUND)
                .setContentIntent(contentIntent)
                .setChannelId(CHANNEL_ID)
                .setContentInfo("Info");


        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(mChannel);
        notificationManager.notify(1, b.build());
    }

    public void getCurrentGPSLocation(String locationProvider, final Context context) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            sendLocalNotification(context, "Job Started", "last location, latitude:" + location.getLatitude() + ", longitude:" + location.getLongitude());
        }

        boolean enabled = locationManager.isProviderEnabled(locationProvider);
        if (!enabled) {
            sendLocalNotification(context,  "Warning", "Provider is disabled!");
            return;
        }

        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                sendLocalNotification(context, "onLocationChanged", "latitude:" + location.getLatitude() + ", longitude:" + location.getLongitude());
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
                sendLocalNotification(context, "onLocationChanged", "onStatusChanged:" + provider + status + extras.toString());
            }

            public void onProviderEnabled(String provider) {
                sendLocalNotification(context, "onLocationChanged", "onProviderEnabled:" + provider);
            }

            public void onProviderDisabled(String provider) {
                sendLocalNotification(context, "onLocationChanged", "onProviderDisabled:" + provider);
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        locationManager.requestLocationUpdates(locationProvider, 0, 0, locationListener);

    }

}
