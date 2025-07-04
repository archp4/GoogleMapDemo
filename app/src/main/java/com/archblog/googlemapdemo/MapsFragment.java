package com.archblog.googlemapdemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsFragment extends Fragment {
    private int selectedLocation;
    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(@NonNull GoogleMap googleMap) {
            LatLng selectedValue = new LatLng(-34, 151);
            String title = "Default Location";
            String snippet = "Default Location";
            float zoomLevel = 15.0f;

            LatLng north = new LatLng(43.7294772, -79.6913764);
            LatLng lakeshore = new LatLng(43.5961307, -79.5227571);
            LatLng igs = new LatLng(43.6698255, -79.3840565);;

            switch (selectedLocation){
                case 1:
                    selectedValue = igs;
                    title = "Humber Downtown Campus";
                    snippet = "Located in Downtown Toronto";
                    break;
                case 2:
                    selectedValue = north;
                    title = "Humber North Campus";
                    snippet = "Located in North Etobicoke";
                    break;
                case 3:
                    selectedValue = lakeshore;
                    title = "Humber Lakeshore Campus";
                    snippet = "Located near Lake Ontario in South Etobicoke";
                    break;
            }
            googleMap.addMarker(new MarkerOptions().position(selectedValue).title(title).snippet(snippet));
            // googleMap.moveCamera(CameraUpdateFactory.newLatLng(selectedValue));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(selectedValue, zoomLevel));
            googleMap.getUiSettings().setZoomControlsEnabled(true);
        }
    };

    public MapsFragment(int position){
        this.selectedLocation = position;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    public int getSelectedLocation() {
        this.selectedLocation = 1;
        return selectedLocation;
    }
    public void setSelectedLocation(int position) {
        this.selectedLocation = position;

    }
}