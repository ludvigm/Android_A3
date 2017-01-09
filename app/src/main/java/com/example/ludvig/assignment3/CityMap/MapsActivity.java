package com.example.ludvig.assignment3.CityMap;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.ludvig.assignment3.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.common.base.Charsets;

import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    final String cities = "Växjö,Malmö,Halmstad,Kalmar,Jönköping,Karlskrona,Karlshamn";
    private GoogleMap mMap;
    File dir;
    List<MarkerOptions> markerOptionsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        dir = this.getExternalFilesDir(null);
        markerOptionsList = new ArrayList<>();
        createCitiesFile();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;

        final Toast distanceToast = Toast.makeText(MapsActivity.this, "", Toast.LENGTH_SHORT);
        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                CameraPosition currentPos = mMap.getCameraPosition();
                //System.out.println(currentPos.target.latitude +","+currentPos.target.longitude);
                MarkerOptions closestToCenter = getClosestToCenter(markerOptionsList, currentPos.target);
                float km = distanceBetween(currentPos.target, closestToCenter.getPosition()) / 1000;
                DecimalFormat format = new DecimalFormat("#.#");
                String displayString = closestToCenter.getTitle() + ": " + format.format(km) + "km.";
                distanceToast.setText(displayString);
                distanceToast.show();
            }
        });
        final Toast labelToast = Toast.makeText(MapsActivity.this, "", Toast.LENGTH_SHORT);
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                labelToast.setText(marker.getTitle());
                labelToast.show();
                return true;
            }
        });
        String[] cities = readFromFile();

        for (String c : cities) {
            Geocoder gc = new Geocoder(this);
            List<Address> addressList;
            try {
                addressList = gc.getFromLocationName(c, 1);
                for (Address a : addressList) {
                    //markers.add()
                    if (a.hasLatitude() && a.hasLongitude()) {
                        MarkerOptions markerOptions = new MarkerOptions()
                                .position(new LatLng(a.getLatitude(), a.getLongitude()))
                                .title(c);
                        mMap.addMarker(markerOptions);
                        markerOptionsList.add(markerOptions);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        LatLngBounds bounds = calculateCameraPos(markerOptionsList);
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 125);
        mMap.moveCamera(cu);
    }

    private MarkerOptions getClosestToCenter(List<MarkerOptions> list, LatLng center) {
        MarkerOptions smallest = null;
        float min = Float.MAX_VALUE;
        for (MarkerOptions mo : list) {
            //  System.out.println("Distance from: " + mo.getTitle() + " to center: " + distanceBetween(mo.getPosition(),center));
            float distance = distanceBetween(mo.getPosition(), center);
            if (distance < min) {
                smallest = mo;
                min = distance;
            }
        }
        return smallest;
    }

    private float distanceBetween(LatLng l1, LatLng l2) {
        float[] results = new float[1];
        Location.distanceBetween(l1.latitude, l1.longitude, l2.latitude, l2.longitude, results);
        return results[0];
    }

    private LatLngBounds calculateCameraPos(List<MarkerOptions> list) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (MarkerOptions marker : list) {
            builder.include(marker.getPosition());
        }
        return builder.build();
    }

    private void createCitiesFile() {
        File file = new File(dir, "cities");
        try {
            Files.write(cities, file, Charsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String[] readFromFile() {
        File file = new File(dir, "cities");
        try {
            String cities = Files.toString(file, Charsets.UTF_8);
            System.out.println(cities);
            return cities.split(",");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
