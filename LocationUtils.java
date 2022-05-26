package com.android.example;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

//  <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
//  <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />

public class LocationUtils {
    private final Context mContext;
    double longitude;
    double latitude;
    Activity mActivity;
    String strOK;
    String strCancel;
    String strLocDisabled;
    double acquiredLon;
    double acquiredLat;
    private final onLocationListener mListener;
    String TAG = "YOURDEBUGTAG";

    LocationManager lm;

    public LocationUtils(Context context, Activity activity, String btnOK, String btnCancel,
                         String txtLocDisabled, onLocationListener listener) {
        this.mContext = context;
        this.mActivity = activity;
        this.longitude = 0.0;
        this.latitude = 0.0;
        this.mListener = listener;
        this.strOK = btnOK;
        this.strCancel = btnCancel;
        this.strLocDisabled = txtLocDisabled;

        this.lm = (LocationManager)mContext.getSystemService(Context.LOCATION_SERVICE);
    }

    public void getCoordinates() {
        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mActivity,
                Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Location Utils: permissions granted");

            if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Log.d(TAG, "Location Utils: GPS is on");
                Location gps_loc = null;
                Location network_loc = null;
                Location final_loc;

                final LocationListener locationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        Log.d(TAG, "Location Changes: " + location.toString());
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {
                        Log.d(TAG, "Status Changed: " + status);
                    }

                    @Override
                    public void onProviderEnabled(String provider) {
                        Log.d(TAG, "Provider Enabled: " + provider);
                    }

                    @Override
                    public void onProviderDisabled(String provider) {
                        Log.d(TAG, "Provider Disabled: " + provider);
                    }
                };

                // Now first make a criteria with your requirements
                // this is done to save the battery life of the device
                // there are various other other criteria you can search for..
                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_COARSE);
                criteria.setPowerRequirement(Criteria.POWER_LOW);
                criteria.setAltitudeRequired(false);
                criteria.setBearingRequired(false);
                criteria.setSpeedRequired(false);
                criteria.setCostAllowed(true);
                criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
                criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);

                // Now create a location manager
                final LocationManager locationManager = (LocationManager)
                        mContext.getSystemService(Context.LOCATION_SERVICE);

                // This is the Best And IMPORTANT part
                try {
                    Log.d(TAG, "Location Utils: requesting update");
                    final Looper looper = null;
                    locationManager.requestSingleUpdate(criteria, locationListener, looper);
                    //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
                    gps_loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    network_loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                } catch (Exception e) {
                    Log.d(TAG, "Location Utils: error requesting update");
                    e.printStackTrace();
                }

                if (gps_loc != null) {
                    final_loc = gps_loc;
                    latitude = final_loc.getLatitude();
                    longitude = final_loc.getLongitude();
                } else if (network_loc != null) {
                    final_loc = network_loc;
                    latitude = final_loc.getLatitude();
                    longitude = final_loc.getLongitude();
                } else {
                    latitude = 0.0;
                    longitude = 0.0;
                }
                Log.d(TAG, "Location Utils: " + latitude + " long: " + longitude);

                if (gps_loc != null || network_loc != null) {
                    ActivityCompat.requestPermissions(mActivity, new String[]{
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_NETWORK_STATE}, 1);
                    Log.d(TAG, "locateByGPS lat: " + latitude + " long: " + longitude);

                    //mosca devolver con un callback los valores de la latitud y longitud
                    setCoordinates(latitude, longitude);
                }
            }
            else {
                Log.d(TAG, "Location Utils: error getting coordinates");
                displayPromptForEnablingGPS();
            }
        }
        else {
            Log.d(TAG, "Location Utils: error getting coordinates");
            requestGpsAccess();
        }
    }

    void requestGpsAccess() {
        if (ContextCompat.checkSelfPermission(mActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity,
                    Manifest.permission.ACCESS_COARSE_LOCATION)){
                ActivityCompat.requestPermissions(mActivity,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            }else{
                ActivityCompat.requestPermissions(mActivity,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            }
        }
    }

    public void displayPromptForEnablingGPS() {
        final AlertDialog.Builder builder =  new AlertDialog.Builder(mActivity);
        final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
        //R.string.gps_off mensaje en recursos
        builder.setMessage(strLocDisabled)
                .setPositiveButton(strOK, (d, id) -> {
                    mActivity.startActivity(new Intent(action));
                    d.dismiss();
                })
                .setNegativeButton(strCancel, (d, id) -> d.cancel());
        builder.create().show();
    }

    void setCoordinates(double lat, double lon) {
        if (mListener!=null) {
            acquiredLon = lon;
            acquiredLat = lat;
            mListener.onLocationAcquired(acquiredLat, acquiredLon);
        }
    }

    /**
     * Callback interface
     * @author <a href="cmpoggi@gmail.com">cmpoggi</a>
     *
     * Create on 2022-5-25 08:04:00 PM
     *
     */
    public interface onLocationListener  {
        /**
         * Callback
         * @param lon coordinates
         * @param lat coordinates
         */
        void onLocationAcquired(double lat, double lon);
    }
}
