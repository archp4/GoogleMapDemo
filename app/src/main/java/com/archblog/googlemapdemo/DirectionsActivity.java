package com.archblog.googlemapdemo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds; // Import LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;

public class DirectionsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LatLng currentLocation;
    private MapsFragment mapsFragment;
    private int pendingDestinationCode = 0; // To store the destination until location is ready

    Button igsDirections, northDirections, lakeshoreDirections;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_directions);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        igsDirections = findViewById(R.id.btn_downtown_directions);
        northDirections = findViewById(R.id.btn_north_directions);
        lakeshoreDirections = findViewById(R.id.btn_lakeshore_directions);

        // Initialize the map fragment
        mapsFragment = new MapsFragment(); // Using the default constructor
        replaceFragment(mapsFragment);

        // Get the SupportMapFragment and register for the map ready callback
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.directionsMapHolder);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Set up button listeners
        igsDirections.setOnClickListener(v -> {
            pendingDestinationCode = 1; // Humber Downtown
            getLastLocation();
        });
        northDirections.setOnClickListener(v -> {
            pendingDestinationCode = 2; // Humber North
            getLastLocation();
        });
        lakeshoreDirections.setOnClickListener(v -> {
            pendingDestinationCode = 3; // Humber Lakeshore
            getLastLocation();
        });

        // Request location permissions if not granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // If permissions are already granted, try to get the last location immediately
            getLastLocation();
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.directionsMapHolder, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;
        // Enable zoom controls
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        // Check for location permissions again before enabling My Location layer
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            // After map is ready and permissions are checked, try to get the last location
            getLastLocation();
        } else {
            Toast.makeText(this, "Location permission not granted. Cannot show current location.", Toast.LENGTH_LONG).show();
        }
    }

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permissions are not granted, request them
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return; // Exit to avoid crashing
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            if (googleMap != null) {
                                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15.0f));
                                googleMap.addMarker(new MarkerOptions().position(currentLocation).title("Current Location"));
                            }
                            // Now that we have the current location, if there's a pending destination, get directions
                            if (pendingDestinationCode != 0) {
                                getDirectionsToLocation(pendingDestinationCode);
                                pendingDestinationCode = 0; // Reset pending destination
                            }
                        } else {
                            Toast.makeText(DirectionsActivity.this, "Could not get current location. Please ensure location is enabled.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void getDirectionsToLocation(int destinationCode) {
        // This method should only be called when currentLocation is not null
        if (currentLocation == null) {
            Toast.makeText(this, "Current location not available. Please try again.", Toast.LENGTH_SHORT).show();
            return;
        }

        LatLng destination = null;
        String destinationName = "";
        switch (destinationCode) {
            case 1:
                destination = new LatLng(43.6698255, -79.3840565); // Humber Downtown
                destinationName = "Humber Downtown Campus";
                break;
            case 2:
                destination = new LatLng(43.7294772, -79.6913764); // Humber North
                destinationName = "Humber North Campus";
                break;
            case 3:
                destination = new LatLng(43.5961307, -79.5227571); // Humber Lakeshore
                destinationName = "Humber Lakeshore Campus";
                break;
        }

        if (destination != null && googleMap != null) {
            float[] distance = new float[1];
            Location.distanceBetween(currentLocation.latitude, currentLocation.longitude,
                    destination.latitude, destination.longitude, distance);

            // Check if the user is at the destination (within a small tolerance, e.g., 50 meters)
            if (distance[0] < 50) {
                Toast.makeText(this, "You are at your destination: " + destinationName, Toast.LENGTH_LONG).show();
                googleMap.clear();
                googleMap.addMarker(new MarkerOptions().position(currentLocation).title("You are here!"));
                googleMap.addMarker(new MarkerOptions().position(destination).title(destinationName));
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15.0f));
            } else {
                Toast.makeText(this, "Getting directions to " + destinationName, Toast.LENGTH_SHORT).show();
                // Clear previous markers and polylines
                googleMap.clear();
                googleMap.addMarker(new MarkerOptions().position(currentLocation).title("Your Location"));
                googleMap.addMarker(new MarkerOptions().position(destination).title(destinationName));
                googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(
                        new LatLngBounds.Builder()
                                .include(currentLocation)
                                .include(destination)
                                .build(), 100)); // Padding of 100 pixels

                // For drawing a simple line (polyline) between current and destination
                // For actual turn-by-turn directions, you would need Google Directions API
                googleMap.addPolyline(new PolylineOptions()
                        .add(currentLocation, destination)
                        .color(0xFF0000FF) // Blue color
                        .width(10));
            }
        } else if (destination == null) {
            Toast.makeText(this, "Invalid destination selected.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, try to get location and then process pending destination
                getLastLocation();
                if (googleMap != null) {
                    // If map is already ready, enable my location
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        googleMap.setMyLocationEnabled(true);
                        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                    }
                }
            } else {
                // Permission denied
                Toast.makeText(this, "Location permission is required to show directions.", Toast.LENGTH_LONG).show();
                pendingDestinationCode = 0; // Clear pending destination if permission is denied
            }
        }
    }
}
