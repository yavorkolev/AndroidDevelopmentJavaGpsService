package yavor.kolev.gpsservice;


import yavor.kolev.gpsservice.gpsdb.GpsDatabase;
import yavor.kolev.gpsservice.gpsdb.models.AltitudeMin;
import yavor.kolev.gpsservice.gpsdb.models.AltitudeMax;
import yavor.kolev.gpsservice.gpsdb.models.Distance;
import yavor.kolev.gpsservice.gpsdb.models.Speed;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static java.lang.Math.round;

public class MainActivity extends AppCompatActivity {

    private GpsDatabase gpsDatabase;

    private Distance distance;
    private Speed speed;
    private AltitudeMin altitudeMin;
    private AltitudeMax altitudeMax;

    String distanceTrip;
    Float tmpSpeedBuf;

    Boolean firstOrOtherLocationUpdates = true;
    Boolean haveCoordinatesForSave = false;

    StringBuilder gpxFileContentBuilder = new StringBuilder("");
    String gpxFileContentStr;

    DateFormat df = new SimpleDateFormat("yyyy.MM.dd'_'HH-mm-ss");
    String date;

    private static final int WRITE_REQUEST_CODE = 101;
    private static final int PERMISSION_REQUEST_CODE = 1;
    int rejectCounter = 0;

    private TextView mLatitudeTextView;
    private TextView mLongitudeTextView;
    private TextView distanceTextView;
    private TextView speedTextView;
    private TextView speedMaxTextView;
    private TextView altitudeTextView;
    private TextView altitudeMinTextView;
    private TextView altitudeMaxTextView;

    private TextView distanceTotalTextView;
    private TextView speedMaxTotalTextView;
    private TextView altitudeMinTotalTextView;
    private TextView altitudeMaxTotalTextView;

    private Button startButton;
    private Button stopExitButton;

    boolean isStopExitButton_EXIT = false;

    GpsDataReceiver gpsDataReceiver;
    GpsDisabledReceiver gpsDisabledReceiver;
    GpsService mBoundService;
    boolean mServiceBound = false;

    String[] gpsPassedData = new String[5];
    Float maxSpeed, tmpSpeedMax = 0.0f;

    Float minAltitude= 0.0f;
    Float maxAltitude = 0.0f;
    boolean isFirstAltitude = false;

    LocationManager locationManager;

    // Speed and Altitude CHARTS for each meter(update)
    private int tripInMeters = 0;
    List<Integer> listSpeeds = new ArrayList<>();
    List<Integer> listAltitudes = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("#--Method----MainActivity-onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!hasRuntimePermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)){
            requestRuntimePermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION, PERMISSION_REQUEST_CODE);
        }

        startButton = findViewById(R.id.StartButtonID);
        stopExitButton = findViewById(R.id.stopExitButton);

        mLatitudeTextView = findViewById((R.id.latitude_textview));
        mLongitudeTextView = findViewById((R.id.longitude_textview));
        distanceTextView = findViewById((R.id.distance_textview));
        speedTextView = findViewById((R.id.speed_textview));
        speedMaxTextView = findViewById((R.id.speedMax_textview));
        altitudeTextView = findViewById((R.id.altitude_textview));
        altitudeMinTextView = findViewById((R.id.altitudeMin_textview));
        altitudeMaxTextView = findViewById((R.id.altitudeMax_textview));

        distanceTotalTextView = findViewById((R.id.distanceTotal_textview));
        speedMaxTotalTextView = findViewById((R.id.speedMaxTotal_textview));
        altitudeMinTotalTextView = findViewById(R.id.altitudeMinTotal_textview);
        altitudeMaxTotalTextView = findViewById(R.id.altitudeMaxTotal_textview);

        displayList();

        distance = new Distance("0.0");
        speed = new Speed("0.0");
        altitudeMin = new AltitudeMin("0.0");
        altitudeMax = new AltitudeMax("0.0");

        new InsertTask(MainActivity.this, distance, speed, altitudeMin, altitudeMax).execute();

        displayList();
    }

    private void speedChart(int numMeters, List<Integer> listSpeeds) {

        LineChart chart;
        chart = findViewById(R.id.chartSpeed);

        final String[] meters = new String[numMeters];

        ArrayList<Entry> entries = new ArrayList<>();

        int counter = 0;

        while (counter < numMeters) {
            meters[counter] = counter + "m.";

            entries.add(new Entry(counter, listSpeeds.get(counter)));
            counter++;
        }

        LineDataSet dataSet = new LineDataSet(entries, "Speed Kilometers per hour");
        dataSet.setColor(ContextCompat.getColor(this, R.color.colorPrimary));
        dataSet.setValueTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        IAxisValueFormatter formatter = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return meters[(int) value];
            }
        };
        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        xAxis.setValueFormatter(formatter);

        YAxis yAxisRight = chart.getAxisRight();
        yAxisRight.setEnabled(true);

        YAxis yAxisLeft = chart.getAxisLeft();
        yAxisLeft.setGranularity(1f);

        LineData data = new LineData(dataSet);
        chart.setData(data);
        chart.animateX(25);
        chart.invalidate();
    }

    private void altitudeChart(int numMeters,  List<Integer> listAltitudes) {

        LineChart chart;
        chart = findViewById(R.id.chartAltitude);

        final String[] meters = new String[numMeters];

        ArrayList<Entry> entries = new ArrayList<>();

        int counter = 0;

        while (counter < numMeters) {
            meters[counter] = counter + "m.";

            entries.add(new Entry(counter, listAltitudes.get(counter)));
            counter++;
        }

        LineDataSet dataSet = new LineDataSet(entries, "Altitude Meters above WGS 84 ellipsoid-50 for BG");
        dataSet.setColor(ContextCompat.getColor(this, R.color.colorPrimary));
        dataSet.setValueTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        IAxisValueFormatter formatter = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return meters[(int) value];
            }
        };
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(formatter);

        YAxis yAxisRight = chart.getAxisRight();
        yAxisRight.setEnabled(true);

        YAxis yAxisLeft = chart.getAxisLeft();
        yAxisLeft.setGranularity(1f);

        LineData data = new LineData(dataSet);
        chart.setData(data);
        chart.animateX(25);
        chart.invalidate();
    }

    private class InsertTask extends AsyncTask<Void, Void, Boolean> {

        private WeakReference<MainActivity> activityReference;
        private Distance distance;
        private Speed speed;
        private AltitudeMin altitudeMin;
        private AltitudeMax altitudeMax;

        InsertTask(MainActivity context, Distance distance, Speed speed, AltitudeMin altitudeMin, AltitudeMax altitudeMax) {

            activityReference = new WeakReference<>(context);
            this.distance = distance;
            this.speed = speed;
            this.altitudeMin = altitudeMin;
            this.altitudeMax = altitudeMax;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            Distance distanceDb = activityReference.get().gpsDatabase.getDistanceDao().getDistance();
            if(distanceDb == null){
                long j = activityReference.get().gpsDatabase.getDistanceDao().insertDistance(distance);
                distance.setDistance_id(j);
            }else if (distanceTrip != null){
                Float totalDistance = Float.parseFloat(distanceDb.getDistance()) + Float.parseFloat(distanceTrip);
                String totalDistanceStr = String.valueOf(totalDistance);
                distance = distanceDb;
                distance.setDistance(totalDistanceStr);
                activityReference.get().gpsDatabase.getDistanceDao().updateDistance(distance);
                distanceTrip = null;
            }

            Speed speedDb = activityReference.get().gpsDatabase.getSpeedDao().getSpeed();
            if(speedDb == null){
                long j = activityReference.get().gpsDatabase.getSpeedDao().insertSpeed(speed);
                speed.setSpeed_id(j);
            }else if (tmpSpeedMax != 0.0f){
                speed = speedDb;
                speed.setSpeed(String.valueOf(tmpSpeedMax));
                activityReference.get().gpsDatabase.getSpeedDao().updateSpeed(speed);
            }

            AltitudeMin altitudeMinDb = activityReference.get().gpsDatabase.getAltitudeMinDao().getAltitudeMin();
            if(altitudeMinDb == null){
                long j = activityReference.get().gpsDatabase.getAltitudeMinDao().insertAltitudeMin(altitudeMin);
                altitudeMin.setAltitudeMin_id(j);
            }else if (minAltitude != 0.0f){
                altitudeMin = altitudeMinDb;
                altitudeMin.setAltitudeMin(String.valueOf(minAltitude));
                activityReference.get().gpsDatabase.getAltitudeMinDao().updateAltitudeMin(altitudeMin);
            }

            AltitudeMax altitudeMaxDb = activityReference.get().gpsDatabase.getAltitudeMaxDao().getAltitudeMax();
            if(altitudeMaxDb == null){
                long j = activityReference.get().gpsDatabase.getAltitudeMaxDao().insertAltitudeMax(altitudeMax);
                altitudeMax.setAltitudeMax_id(j);
            }else if (maxAltitude != 0.0f){
                altitudeMax = altitudeMaxDb;
                altitudeMax.setAltitudeMax(String.valueOf(maxAltitude));
                activityReference.get().gpsDatabase.getAltitudeMaxDao().updateAltitudeMax(altitudeMax);
            }
            return true;
        }
    }

    private void displayList() {

        gpsDatabase = GpsDatabase.getInstance(MainActivity.this);
        new RetrieveTaskDistance(this).execute();

        gpsDatabase = GpsDatabase.getInstance(MainActivity.this);
        new RetrieveTaskSpeed(this).execute();

        gpsDatabase = GpsDatabase.getInstance(MainActivity.this);
        new RetrieveTaskAltitudeMin(this).execute();

        gpsDatabase = GpsDatabase.getInstance(MainActivity.this);
        new RetrieveTaskAltitudeMax(this).execute();
    }

    private class RetrieveTaskDistance extends AsyncTask<Void, Void, Distance> {

        private WeakReference<MainActivity> activityReference;

        RetrieveTaskDistance(MainActivity context) {

            activityReference = new WeakReference<>(context);
        }

        @Override
        protected Distance doInBackground(Void... voids) {

            if (activityReference.get() != null)
                return activityReference.get().gpsDatabase.getDistanceDao().getDistance();
            else
                return null;
        }

        @Override
        protected void onPostExecute(Distance distancePost) {

            Distance distanceDb = activityReference.get().gpsDatabase.getDistanceDao().getDistance();
            Float totalDistanceFloat = Float.parseFloat(distanceDb.getDistance());
            if (totalDistanceFloat <= 1000.0f){
                distance = distanceDb;
                distanceTotalTextView.setText((round(totalDistanceFloat * 10.0) / 10.0) + "m.");
            } else if (totalDistanceFloat > 1000.0f){
                totalDistanceFloat = totalDistanceFloat / 1000.0f;
                distanceTotalTextView.setText((round(totalDistanceFloat * 1000.000) / 1000.000) + "km.");
            }
        }
    }

    private class RetrieveTaskSpeed extends AsyncTask<Void, Void, Speed> {

        private WeakReference<MainActivity> activityReference;

        RetrieveTaskSpeed(MainActivity context) {

            activityReference = new WeakReference<>(context);
        }

        @Override
        protected Speed doInBackground(Void... voids) {

            if (activityReference.get() != null)
                return activityReference.get().gpsDatabase.getSpeedDao().getSpeed();
            else
                return null;
        }

        @Override
        protected void onPostExecute(Speed speedPost) {

            Speed speedDb = activityReference.get().gpsDatabase.getSpeedDao().getSpeed();
            speed = speedDb;
            speedMaxTotalTextView.setText(speed.getSpeed()+ "km/h");
        }
    }

    private class RetrieveTaskAltitudeMin extends AsyncTask<Void, Void, AltitudeMin> {

        private WeakReference<MainActivity> activityReference;

        RetrieveTaskAltitudeMin(MainActivity context) {

            activityReference = new WeakReference<>(context);
        }

        @Override
        protected AltitudeMin doInBackground(Void... voids) {

            if (activityReference.get() != null)
                return activityReference.get().gpsDatabase.getAltitudeMinDao().getAltitudeMin();
            else
                return null;
        }

        @Override
        protected void onPostExecute(AltitudeMin altitudeMinPost) {

            AltitudeMin altitudeMinDb = activityReference.get().gpsDatabase.getAltitudeMinDao().getAltitudeMin();
            altitudeMin = altitudeMinDb;
            altitudeMinTotalTextView.setText(altitudeMin.getAltitudeMin()+ "m.");
        }
    }

    private class RetrieveTaskAltitudeMax extends AsyncTask<Void, Void, AltitudeMax> {

        private WeakReference<MainActivity> activityReference;

        RetrieveTaskAltitudeMax(MainActivity context) {

            activityReference = new WeakReference<>(context);
        }

        @Override
        protected AltitudeMax doInBackground(Void... voids) {

            if (activityReference.get() != null)
                return activityReference.get().gpsDatabase.getAltitudeMaxDao().getAltitudeMax();
            else
                return null;
        }

        @Override
        protected void onPostExecute(AltitudeMax altitudeMaxPost) {

            AltitudeMax altitudeMaxDb = activityReference.get().gpsDatabase.getAltitudeMaxDao().getAltitudeMax();
            altitudeMax = altitudeMaxDb;
            altitudeMaxTotalTextView.setText(altitudeMax.getAltitudeMax()+ "m.");
        }
    }

    @Override
    protected void onDestroy() {

        gpsDatabase.cleanUp();

        haveCoordinatesForSave = false;

        stopGpsService();

        exit();

        super.onDestroy();
    }

    private class GpsDataReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent arg1) {

            date = df.format(Calendar.getInstance().getTime());
            gpsPassedData = (mBoundService.passGpsData());

            if((Float.parseFloat(gpsPassedData[0]) != 0.0f) || (Float.parseFloat(gpsPassedData[1]) != 0.0f)){

                if ((Float.parseFloat(gpsPassedData[4]) != 0.0f) && (Float.parseFloat(gpsPassedData[4]) != 0.0f)){
                    // To use on stop to create charts speed and altitude
                    // Each meters(updates) counter
                    tripInMeters++;
                    // Get current speed for each meters(updates) in list like int
                    listSpeeds.add((round((Float.valueOf(gpsPassedData[3]) * 10) / 10)));
                    // Get current altitude for each meters(updates) in list like int
                    listAltitudes.add((round((Float.valueOf(gpsPassedData[4]) * 10) / 10)));
                }

                haveCoordinatesForSave = true;

                tmpSpeedBuf = Float.parseFloat(gpsPassedData[3]);

                maxSpeed = tmpSpeedBuf;
                if ((maxSpeed > tmpSpeedMax) || (tmpSpeedMax < maxSpeed)){
                    speedMaxTextView.setText(gpsPassedData[3]+"km/h");
                    tmpSpeedMax = maxSpeed;
                }

                // Check if location update is first with data
                if (firstOrOtherLocationUpdates){
                    firstOrOtherLocationUpdates = false;
                    gpxFileWriterHeader();
                } else if (tmpSpeedBuf > 0.0f){
                    gpxFileWriterBody();
                }

                if (gpsPassedData[0].length() == 8){
                    gpsPassedData[0] = gpsPassedData[0] + '0';
                } else mLatitudeTextView.append(gpsPassedData[0] + " - " + date +"\n");

                mLatitudeTextView.setMovementMethod(new ScrollingMovementMethod());

                if (gpsPassedData[1].length() == 8){
                    gpsPassedData[1] = gpsPassedData[0] + '0';
                } else mLongitudeTextView.append(gpsPassedData[1] + " - " + date +"\n");

                mLongitudeTextView.setMovementMethod(new ScrollingMovementMethod());

                distanceTrip = gpsPassedData[2];
                Float distanceTripFloat = Float.parseFloat(distanceTrip);
                if (distanceTripFloat <= 1000.0f){
                    distanceTextView.setText((round(distanceTripFloat * 10.0) / 10.0) + "m.");
                } else if (distanceTripFloat > 1000.0f){
                    distanceTripFloat = distanceTripFloat / 1000.0f;
                    distanceTextView.setText((round(distanceTripFloat * 1000.000) / 1000.000) + "km.");
                }


                if (Float.parseFloat(gpsPassedData[3]) != 0.0f){
                    speedTextView.append(gpsPassedData[3] + "km/h" + "\n");
                    speedTextView.setMovementMethod(new ScrollingMovementMethod());
                }

                if (Float.parseFloat(gpsPassedData[4]) != 0.0f){
                    altitudeTextView.append(gpsPassedData[4] + "m."+"\n");
                    altitudeTextView.setMovementMethod(new ScrollingMovementMethod());
                }

                // Only first time set min/maxAltitude to the current
                if ((!isFirstAltitude) && (Float.parseFloat(gpsPassedData[4]) != 0.0f)){
                    minAltitude = Float.parseFloat(gpsPassedData[4]);
                    altitudeMinTextView.setText(gpsPassedData[4] + "m."+"\n");
                    maxAltitude = Float.parseFloat(gpsPassedData[4]);
                    altitudeMaxTextView.setText(gpsPassedData[4] + "m."+"\n");
                    isFirstAltitude = true;
                } else if ((isFirstAltitude) && (Float.parseFloat(gpsPassedData[4]) != 0.0f)){
                    if ((Float.parseFloat(gpsPassedData[4]) <= minAltitude)){
                        altitudeMinTextView.setText(gpsPassedData[4] + "m."+"\n");
                        minAltitude = (Float.parseFloat(gpsPassedData[4]));
                    }
                    if ((Float.parseFloat(gpsPassedData[4]) > maxAltitude)){
                        altitudeMaxTextView.setText(gpsPassedData[4] + "m."+"\n");
                        maxAltitude = (Float.parseFloat(gpsPassedData[4]));
                    }
                }
            } else {
                mLatitudeTextView.append("Low signal" + " - " + date +"\n");
                mLatitudeTextView.setMovementMethod(new ScrollingMovementMethod());

                mLongitudeTextView.append("Low signal" + " - " + date +"\n");
                mLongitudeTextView.setMovementMethod(new ScrollingMovementMethod());
            }
        }
    }

    private class GpsDisabledReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            System.out.println("#--Method----MainActivity-onReceive-GpsDisabledReceiver");

            stopGpsService();
        }
    }

    private void requestRuntimePermission(Activity activity, String runtimePermission, int requestCode) {
        ActivityCompat.requestPermissions(activity, new String[]{runtimePermission}, requestCode);
    }

    private boolean hasRuntimePermission(Context context, String runtimePermission) {

        boolean ret = false;
        int currentAndroidVersion = Build.VERSION.SDK_INT;

        if(currentAndroidVersion > Build.VERSION_CODES.M) {
            if(ContextCompat.checkSelfPermission(context, runtimePermission) == PackageManager.PERMISSION_GRANTED) {
                ret = true;
            }
        }else {
            ret = true;
        }
        return ret;
    }

    private void rejectPermissionHandler() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            rejectCounter++;
            if (rejectCounter == 3) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", this.getPackageName(), null);
                intent.setData(uri);
                this.startActivity(intent);
            } else if (rejectCounter == 2) {
                if (!hasRuntimePermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                    requestRuntimePermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION, PERMISSION_REQUEST_CODE);
                }
            } else if (rejectCounter == 4){
                rejectCounter = 0;
                if(Build.VERSION.SDK_INT>=16 && Build.VERSION.SDK_INT<21){
                    finishAffinity();
                } else if(Build.VERSION.SDK_INT>=21){
                    finishAndRemoveTask();
                }
            }
        }
    }

    private void gpxFileWriterHeader() {

        gpxFileContentBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "\n");
        gpxFileContentBuilder.append("<gpx version=\"1.1\">" + "\n");
        gpxFileContentBuilder.append("<wpt lat=\"" + gpsPassedData[0] + "\" lon=\"" + gpsPassedData[1] + "\"><desc>Start Point " + gpsPassedData[0] + "/" + gpsPassedData[1] + "</desc></wpt>" + "\n");
        gpxFileContentBuilder.append("<trk>" + "\n");
        gpxFileContentBuilder.append("<name>" + date + "</name>" + "\n");
        gpxFileContentBuilder.append("<trkseg>" + "\n");
        gpxFileContentBuilder.append("<trkpt lat=\"" + gpsPassedData[0] + "\" lon=\"" + gpsPassedData[1] + "\"></trkpt>" + "\n");
    }

    private void gpxFileWriterBody() {

        System.out.println("#--Method----MainActivity-gpxFileWriterBody");
        gpxFileContentBuilder.append("<trkpt lat=\"" + gpsPassedData[0] + "\" lon=\"" + gpsPassedData[1] + "\"></trkpt>" + "\n");
    }

    private void gpxFileWriterFooter() {

        gpxFileContentBuilder.append("</trkseg>" + "\n");
        gpxFileContentBuilder.append("</trk>" + "\n");
        gpxFileContentBuilder.append("<wpt lat=\"" + gpsPassedData[0] + "\" lon=\"" + gpsPassedData[1] + "\"><desc>End Point " + gpsPassedData[0] + "/" + gpsPassedData[1] + "</desc></wpt>");
        gpxFileContentBuilder.append("</gpx>");
    }

    public void startService(View v){

        startGpsService();
    }

    public void stopExitService(View view) {

        if (!isStopExitButton_EXIT){
            stopGpsService();
            isStopExitButton_EXIT = true;
        } else {
            exit();
        }

    }

    private void startGpsService() {

        if (isLocationEnabled() &&
                (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)){

            isStopExitButton_EXIT = false;

            stopExitButton.setVisibility(View.VISIBLE);
            stopExitButton.setText("STOP");

            startButton.setVisibility(View.INVISIBLE);

            // Gps service data receiver
            gpsDataReceiver = new GpsDataReceiver();
            IntentFilter gpsDataIntentFilter = new IntentFilter();
            gpsDataIntentFilter.addAction(GpsService.GET_GPS_DATA);
            registerReceiver(gpsDataReceiver, gpsDataIntentFilter);
            // Gps service disabled receiver
            gpsDisabledReceiver = new GpsDisabledReceiver();
            IntentFilter gpsDisabledIntentFilter = new IntentFilter();
            gpsDisabledIntentFilter.addAction(GpsService.GET_GPS_DISABLED);
            registerReceiver(gpsDisabledReceiver, gpsDisabledIntentFilter);

            // Start Location service
            Intent serviceIntent = new Intent(this, GpsService.class);
            ContextCompat.startForegroundService(this, serviceIntent);

            bindService(serviceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    private void stopGpsService() {

        if (tripInMeters > 0){
            // Invoke to create speed chart
            speedChart(tripInMeters, listSpeeds);

            // Invoke to create altitude chart
            altitudeChart(tripInMeters, listAltitudes);
        }

        new InsertTask(MainActivity.this, distance, speed, altitudeMin, altitudeMax).execute();
        displayList();

        firstOrOtherLocationUpdates = true;

        startButton.setVisibility(View.VISIBLE);

        stopExitButton.setText("EXIT");

        Intent serviceIntent = new Intent(this, GpsService.class);
        stopService(serviceIntent);
        stopService(serviceIntent);
        unregisterReceiver(gpsDataReceiver);
        unregisterReceiver(gpsDisabledReceiver);
        if (mServiceBound) {
            unbindService(mServiceConnection);
            mServiceBound = false;
        }

        // Open file save dialog only if have at least one successfully Latitude and Longitude
        if (haveCoordinatesForSave){
            gpxFileWriterFooter();
            createFile();
        }
    }

    private boolean isLocationEnabled() {

        final Boolean[] isGpsEnabled = {false};
        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        isGpsEnabled[0] = locationManager.isProviderEnabled(LocationManager. GPS_PROVIDER);

        if (!isGpsEnabled[0]) {
            final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("Enable Location")
                    .setMessage("Please enable GPS")
                    .setPositiveButton("GPS Settings", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            isGpsEnabled[0] = true;
                            Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(myIntent);

                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            // show message to enable GPS
                            Toast.makeText(getApplicationContext(), "Please enable GPS", Toast.LENGTH_LONG).show();
                            // invoke custom exit function
                            exit();
                        }
                    });
            dialog.show();
        }

        return isGpsEnabled[0];
    }

    public void onStart() {

        super.onStart();
    }

    public void onRestart() {

        super.onRestart();
    }

    public void onResume() {

        rejectPermissionHandler();

        super.onResume();
    }

    public void exit(){

        if(Build.VERSION.SDK_INT>=16 && Build.VERSION.SDK_INT<21){
            finishAffinity();
            finish();
            System.exit(0);
        } else if(Build.VERSION.SDK_INT>=21){
            finishAndRemoveTask();
            finish();
            System.exit(0);
        }

    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {

            mServiceBound = false;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            GpsService.MyBinder myBinder = (GpsService.MyBinder) service;
            mBoundService = myBinder.getService();
            mServiceBound = true;
        }
    };

    public void onPause() {

        super.onPause();
    }

    public void onStop() {

        super.onStop();
    }

    private void createFile() {

        // Get current date and time and set it like filename
        date = df.format(Calendar.getInstance().getTime());
        // when you create document, you need to add Intent.ACTION_CREATE_DOCUMENT
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);

        // filter to only show openable items.
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Create a file with the requested Mime type
        intent.setType("gpx/plain");
        intent.putExtra(Intent.EXTRA_TITLE, date + ".gpx");

        startActivityForResult(intent, WRITE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == WRITE_REQUEST_CODE) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    if (data != null
                            && data.getData() != null) {
                        gpxFileContentStr = String.valueOf(gpxFileContentBuilder);
                        writeInFile(data.getData(), gpxFileContentStr);
                        //exit();
                    }
                    break;
                case Activity.RESULT_CANCELED:
                    break;
            }
        }
    }

    private void writeInFile(@NonNull Uri uri, @NonNull String text) {

        OutputStream outputStream;
        try {
            outputStream = getContentResolver().openOutputStream(uri);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outputStream));
            bw.write(text);
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
