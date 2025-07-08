package com.archblog.googlemapdemo;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.Manifest;
import android.content.pm.PackageManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class DirectionsActivity extends AppCompatActivity {

    private FusedLocationProviderClient fusedLocationClient;
    private final int LOCATION_PERMISSION_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_directions);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets sb = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(sb.left, sb.top, sb.right, sb.bottom);
            return insets;
        });

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        Button downtown = findViewById(R.id.btn_downtown_directions);
        Button north = findViewById(R.id.btn_north_directions);
        Button lakeshore = findViewById(R.id.btn_lakeshore_directions);

        downtown.setOnClickListener(v -> openDirections(43.6698255, -79.3840565));
        north.setOnClickListener(v -> openDirections(43.7294772, -79.6913764));
        lakeshore.setOnClickListener(v -> openDirections(43.5961307, -79.5227571));
    }

    private void openDirections(double destLat, double destLng) {
        // Use device current location as origin
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                double originLat = location.getLatitude();
                double originLng = location.getLongitude();

                String uri = "https://www.google.com/maps/dir/?api=1"
                        + "&origin=" + originLat + "," + originLng
                        + "&destination=" + destLat + "," + destLng
                        + "&travelmode=driving";

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setPackage("com.google.android.apps.maps"); // Optional: force open in Google Maps app
                startActivity(intent);
            } else {
                Toast.makeText(this, "Unable to fetch location", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_CODE && grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission granted, try again.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Location permission denied", Toast.LENGTH_LONG).show();
        }
    }
}
