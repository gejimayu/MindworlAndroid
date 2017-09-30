package com.mindworld.howtosurvive.mindworld;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static android.content.Context.LOCATION_SERVICE;

public class UserCity {
    private Context mContext;
    private String mFilelocation;

    public UserCity(Context context) {
        mContext = context;
    }

    public void getCity() {
        if (ContextCompat.checkSelfPermission(mContext,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Getting LocationManager object from System Service LOCATION_SERVICE
            LocationManager locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

            // Creating a criteria object to retrieve provider
            Criteria criteria = new Criteria();

            // Getting the name of the best provider
            String provider = locationManager.getBestProvider(criteria, true);

            // Getting Current Location From GPS
            Location location = locationManager.getLastKnownLocation(provider);

            if (location != null) {
                getUserGeoInfo(location.getLatitude(), location.getLongitude());
            }

            // Define a listener that responds to location updates
            LocationListener locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    // Called when a new location is found by the network location provider.
                    getUserGeoInfo(location.getLatitude(), location.getLongitude());
                }

                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                public void onProviderEnabled(String provider) {
                }

                public void onProviderDisabled(String provider) {
                }
            };

            // Set how often you want to request location updates and where you want to receive them
            locationManager.requestLocationUpdates(provider, 20000, 0, locationListener);
        }
    }

    private void getUserGeoInfo(double latitude, double longitude) {
        Geocoder geoCoder = new Geocoder(mContext, Locale.getDefault());
        if (Geocoder.isPresent()) {
            try {
                List<Address> addresses = geoCoder.getFromLocation(latitude, longitude, 1);

                if (addresses.size() > 0) {
                    // obtain all information from addresses.get(0)
                    mFilelocation = addresses.get(0).getLocality();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getFilelocation() {
        return mFilelocation;
    }
}
