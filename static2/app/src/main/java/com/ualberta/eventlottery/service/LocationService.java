package com.ualberta.eventlottery.service;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class LocationService {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private static final int REQUEST_FINE_LOCATION_PERMISSIONS_REQUEST_CODE = 1002;
    private static final int REQUEST_COARSE_LOCATION_PERMISSIONS_REQUEST_CODE = 1003;
    private static final int REQUEST_BACKGROUND_LOCATION_PERMISSIONS_REQUEST_CODE = 1004;

    private Context context;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private CompletableFuture<Location> locationFuture;

    public LocationService(Context context) {
        this.context = context;
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        this.locationFuture = new CompletableFuture<>();
    }

    public boolean hasLocationPermissions() {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
               ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    public boolean hasBackgroundLocationPermission() {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    public void requestLocationPermissions(Activity activity) {
        ActivityCompat.requestPermissions(activity,
                new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                },
                REQUEST_FINE_LOCATION_PERMISSIONS_REQUEST_CODE);
    }

    public void requestBackgroundLocationPermission(Activity activity) {
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                REQUEST_BACKGROUND_LOCATION_PERMISSIONS_REQUEST_CODE);
    }

    @SuppressLint("MissingPermission")
    public CompletableFuture<Location> getCurrentLocation() {
        locationFuture = new CompletableFuture<>();

        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
                .build();

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    locationFuture.completeExceptionally(new Exception("Unable to get location"));
                    return;
                }

                Location location = locationResult.getLastLocation();
                if (location != null) {
                    locationFuture.complete(location);
                } else {
                    locationFuture.completeExceptionally(new Exception("Location is null"));
                }

                fusedLocationClient.removeLocationUpdates(locationCallback);
            }
        };

        try {
            fusedLocationClient.requestLocationUpdates(locationRequest,
                    locationCallback, Looper.getMainLooper());
        } catch (Exception e) {
            locationFuture.completeExceptionally(e);
        }

        return locationFuture;
    }

    public String getAddressFromLocation(Location location) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    1
            );

            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                StringBuilder addressText = new StringBuilder();

                for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                    if (i > 0) addressText.append(", ");
                    addressText.append(address.getAddressLine(i));
                }

                return addressText.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return String.format("Lat: %.6f, Lng: %.6f",
                location.getLatitude(), location.getLongitude());
    }

    public double calculateDistance(Location loc1, Location loc2) {
        if (loc1 == null || loc2 == null) {
            return Double.MAX_VALUE;
        }

        return loc1.distanceTo(loc2); // Distance in meters
    }

    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        Location loc1 = new Location("point1");
        loc1.setLatitude(lat1);
        loc1.setLongitude(lon1);

        Location loc2 = new Location("point2");
        loc2.setLatitude(lat2);
        loc2.setLongitude(lon2);

        return calculateDistance(loc1, loc2);
    }
}