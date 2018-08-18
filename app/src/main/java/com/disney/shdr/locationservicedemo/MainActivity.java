package com.disney.shdr.locationservicedemo;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public final static String TAG = MainActivity.class.getName();
    private TextView GPSLocationLabel;
    private TextView NetworkLocationLabel;
    private TextView PassiveLocationLabel;
    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GPSLocationLabel = this.findViewById(R.id.latest_GPS_location);
        NetworkLocationLabel = this.findViewById(R.id.latest_network_location);
        PassiveLocationLabel = this.findViewById(R.id.latest_passive_location);

        Button requestGPSLocationButton = this.findViewById(R.id.request_GPS_location);
        requestGPSLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentGPSLocation(LocationManager.GPS_PROVIDER, GPSLocationLabel);
            }
        });

        Button requestNetworkLocationButton = this.findViewById(R.id.request_network_location);
        requestNetworkLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentGPSLocation(LocationManager.NETWORK_PROVIDER, NetworkLocationLabel);
            }
        });

        Button requestPassiveLocationButton = this.findViewById(R.id.request_passive_location);
        requestPassiveLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentGPSLocation(LocationManager.PASSIVE_PROVIDER, PassiveLocationLabel);
            }
        });

        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            GPSLocationLabel.setText("onLocationChanged, latitude:" + location.getLatitude() + ", longitude:" + location.getLongitude());
        }
    }

    public void getCurrentGPSLocation(String locationProvider, final TextView locationLabel) {

        boolean enabled = locationManager.isProviderEnabled(locationProvider);
        if (!enabled) {
            locationLabel.setText("Warning! Provider is disabled!");
            return;
        }

        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                Log.i(TAG, "onLocationChanged:" + location.getLatitude());
                locationLabel.setText("onLocationChanged, latitude:" + location.getLatitude() + ", longitude:" + location.getLongitude());
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.i(TAG, "onStatusChanged:" + provider + status + extras.toString());
            }

            public void onProviderEnabled(String provider) {
                Log.i(TAG, "onProviderEnabled:" + provider);
            }

            public void onProviderDisabled(String provider) {
                Log.i(TAG, "onProviderDisabled:" + provider);

            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        locationManager.requestLocationUpdates(locationProvider, 0, 0, locationListener);

    }
}