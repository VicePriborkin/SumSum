package com.android.pribo.vice.sumsum;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    FusedLocationProviderClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        SupportMapFragment mapFragment = new SupportMapFragment();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame1 , mapFragment)
                .replace(R.id.frame2 , new AddGate())
                .commit();

        //tell me when the map is loaded
        mapFragment.getMapAsync(this);
        //client = LocationServices.getFusedLocationProviderClient(this);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        //addMarker(map);
        setMyLocation(map);
        setUpMap(map);

    }


    private void setUpMap(final GoogleMap map) {
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(final LatLng latLng) {

                map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
                Toast.makeText(MapsActivity.this, latLng.toString(), Toast.LENGTH_SHORT).show();

                final CircleOptions circleOptions = new CircleOptions()
                        .center(latLng)
                        .radius(200)
                        .strokeColor(Color.GREEN)
                        .strokeWidth(8).clickable(true);

                Circle mCircle;

                mCircle = map.addCircle(circleOptions);

                final Circle finalMCircle = mCircle;

                map.addMarker(new MarkerOptions()
                        .position(latLng)
                        .draggable(true));


                map.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                    @Override
                    public void onMarkerDragStart(Marker marker) {

                    }

                    @Override
                    public void onMarkerDrag(Marker marker) {
                        finalMCircle.setCenter(marker.getPosition());
                    }

                    @Override
                    public void onMarkerDragEnd(Marker marker) {

                    }
                });


            }
        });

        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                Toast.makeText(MapsActivity.this, "Cleared all markers", Toast.LENGTH_SHORT).show();

                map.clear();
            }
        });

    }


    private void setMyLocation(GoogleMap map) {

        if (checkLocationPermission())
            map.setMyLocationEnabled(true);
    }


    private void addMarker(GoogleMap map) {

        if (!checkLocationPermission()) return;

        Task<Location> task = client.getLastLocation();
        Location location = task.getResult();

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());


        map.addMarker(new MarkerOptions().position(latLng));
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
    }

    private boolean checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1
            );

            return false;
        }
        return true;
    }
}
