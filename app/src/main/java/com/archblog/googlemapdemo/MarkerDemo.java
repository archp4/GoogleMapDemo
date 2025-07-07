package com.archblog.googlemapdemo;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MarkerDemo extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker_demo);

        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //North campus
        LatLng northCampus = new LatLng(43.7282, -79.6071);
        mMap.addMarker(new MarkerOptions().position(northCampus).title("Humber North Campus"));

        //Downtown campus
        LatLng downtownCampus = new LatLng(43.67002727262021, -79.38381026436184);
        mMap.addMarker(new MarkerOptions().position(downtownCampus).title("Humber Downtown Campus"));

        //Lakeshore campus
        LatLng lakeshoreCampus = new LatLng(43.59632559137166, -79.52011875565697);
        mMap.addMarker(new MarkerOptions().position(lakeshoreCampus).title("Humber Lakeshore Campus"));

        //Harbourfront
        LatLng harbourfront = new LatLng(43.6387, -79.3801);
        mMap.addMarker(new MarkerOptions().position(harbourfront).title("Harbourfront"));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(northCampus, 12));
    }
}