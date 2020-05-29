package yavor.kolev.gpsservice;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;


import static yavor.kolev.gpsservice.App.CHANNEL_ID;
import static java.lang.Math.round;


public class GpsService extends Service {

    float accuracy;
    private IBinder mBinder = new MyBinder();
    final static String GET_GPS_DATA = "GET_GPS_DATA";
    final static String GET_GPS_DISABLED = "GET_GPS_DISABLED";
    LocationListener listener;
    String[] gpsData = new String[5];
    float distanceTrip;
    boolean firstStart = false;
    float distance;
    Location locationA;
    Location locationB;

    LocationManager locationManager;


    public class MyBinder extends Binder {
        GpsService getService() {

            return GpsService.this;
        }
    }

    @Override
    public void onCreate() { ;

        super.onCreate();
    }

    @SuppressLint("MissingPermission")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                accuracy = location.getAccuracy();

                if ((accuracy > 0.0f) && (accuracy <= 10.0f)){
                    // Distance calculation
                    if (!firstStart){
                        firstStart = true;
                        locationA = new Location(location);
                        locationA.setLatitude(round(location.getLatitude() * 1000000.0) / 1000000.0);
                        locationA.setLatitude(round(location.getLongitude() * 1000000.0) / 1000000.0);
                    } else if (firstStart) {
                        locationB = new Location(location);
                        locationB.setLatitude(round(location.getLatitude() * 1000000.0) / 1000000.0);
                        locationB.setLatitude(round(location.getLongitude() * 1000000.0) / 1000000.0);

                        distance = locationA.distanceTo(locationB);

                        distanceTrip = distanceTrip + distance;
                        locationA = locationB;
                    }

                    gpsData[0] = String.valueOf((round(location.getLatitude() * 1000000.0) / 1000000.0));
                    gpsData[1] = String.valueOf((round(location.getLongitude() * 1000000.0) / 1000000.0));
                    // It's rounded in main activity depending of the value up or above 1000
                    gpsData[2] = String.valueOf(distanceTrip);
                    // From meter per second to kilometer per hour, float
                    gpsData[3] = String.valueOf(round((location.getSpeed()*3.6) * 10.0) / 10.0);
                    // Double if available, in meters above the WGS 84 reference ellipsoid.
                    // Add hard codded correction for bulgaria for test - 50 meters!
                    if (location.getAltitude() > 50.0f){
                        gpsData[4] = String.valueOf((round(location.getAltitude() * 10.0) / 10.0) - 50);
                    }
                    else gpsData[4] = String.valueOf((round(location.getAltitude() * 10.0) / 10.0));

                    serviceDataIntentCreation();

                } else {
                    gpsData[0] = "0.0";
                    gpsData[1] = "0.0";
                    gpsData[2] = "0.0";
                    gpsData[3] = "0.0";
                    gpsData[4] = "0.0";

                    serviceDataIntentCreation();
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
            }

            @Override
            public void onProviderEnabled(String s) {
            }

            @Override
            public void onProviderDisabled(String s) {

                Intent intent = new Intent();
                intent.setAction(GET_GPS_DISABLED);
                sendBroadcast(intent);

                locationManager.removeUpdates(listener);
            }
        };

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 1, listener);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("GPS Service")
                .setContentText("GPS is started")
                .setSmallIcon(R.drawable.ic_android)
                .build();

        startForeground(1, notification);

        return START_STICKY;
    }

    private void serviceDataIntentCreation(){

        Intent intent = new Intent();
        intent.setAction(GET_GPS_DATA);
        sendBroadcast(intent);
    }

    public String[] passGpsData() {

        return gpsData;
    }

    @Override
    public void onDestroy() {

        locationManager.removeUpdates(listener);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return mBinder;
    }
}