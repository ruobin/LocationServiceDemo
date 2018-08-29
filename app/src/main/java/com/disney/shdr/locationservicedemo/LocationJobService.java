package com.disney.shdr.locationservicedemo;

import android.Manifest;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class LocationJobService extends JobService {

    private static int NOTIFICATION_ID = 1000;

    private static int MINIMUM_LOCATION_UPDATES_TIME_INTERVAL = 30 * 1000;

    private static int MINIMUM_LOCATION_UPDATES_DISTANCE = 50;

    @Override
    public boolean onStartJob(final JobParameters jobParameters) {
        LocalNotificationHelper.sendLocalNotification(getApplicationContext(), "Location Job", "on start");

        getCurrentLocation(LocationManager.NETWORK_PROVIDER, getApplicationContext(), jobParameters);
        getCurrentLocation(LocationManager.GPS_PROVIDER, getApplicationContext(), jobParameters);
        getCurrentLocation(LocationManager.PASSIVE_PROVIDER, getApplicationContext(), jobParameters);

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        LocalNotificationHelper.sendLocalNotification(getApplicationContext(), "Location Job", "on stop");
        return false;
    }

    public void getCurrentLocation(final String locationProvider, final Context context, final JobParameters params) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        final LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        boolean enabled = locationManager.isProviderEnabled(locationProvider);
        if (!enabled) {
            LocalNotificationHelper.sendLocalNotification(context,  "Warning", "Provider is disabled!");
            return;
        }

        final LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                locationManager.removeUpdates(this);
                // Called when a new location is found by the network location provider.
                LocalNotificationHelper.sendLocalNotification(context, locationProvider + " provider: onLocationChanged", "latitude:" + location.getLatitude() + ", longitude:" + location.getLongitude());

                jobFinished(params, true);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
//                LocalNotificationHelper.sendLocalNotification(context, "onStatusChanged", "onStatusChanged:" + provider + status + extras.toString());
            }

            public void onProviderEnabled(String provider) {
//                LocalNotificationHelper.sendLocalNotification(context, "onProviderEnabled", "onProviderEnabled:" + provider);
            }

            public void onProviderDisabled(String provider) {
                LocalNotificationHelper.sendLocalNotification(context, "onProviderDisabled", "onProviderDisabled:" + provider);
            }
        };

        long minTime = MINIMUM_LOCATION_UPDATES_TIME_INTERVAL;
        float minDistance = MINIMUM_LOCATION_UPDATES_DISTANCE;

        if (locationProvider.equalsIgnoreCase(LocationManager.NETWORK_PROVIDER)) {
            minTime = MINIMUM_LOCATION_UPDATES_TIME_INTERVAL;
            minDistance = 0;
        } else if (locationProvider.equalsIgnoreCase(LocationManager.GPS_PROVIDER)) {
            minTime = 15 * 60 * 1000; // 15 minutes
            minDistance = 0;
        } else if (locationProvider.equalsIgnoreCase(LocationManager.PASSIVE_PROVIDER)) {
            minTime = MINIMUM_LOCATION_UPDATES_TIME_INTERVAL;
            minDistance = 0;
        }
        locationManager.requestLocationUpdates(locationProvider, minTime, minDistance, locationListener);
        Executors.newScheduledThreadPool(1).schedule(new Runnable() {
            @Override
            public void run() {
                locationManager.removeUpdates(locationListener);
                jobFinished(params, true);
            }
        }, 10, TimeUnit.SECONDS);
    }

}
