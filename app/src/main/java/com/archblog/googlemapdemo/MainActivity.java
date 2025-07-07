package com.archblog.googlemapdemo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    }


    public void toMiniMap(View view) {
        Intent miniMap = new Intent(this, MiniMap.class);
        startActivity(miniMap);
    }
    public void toDirections(View view) {
        Intent directionsIntent = new Intent(this, DirectionsActivity.class);
        startActivity(directionsIntent);
    }
    public void toMarkerDemo(View view) {
        Intent markerDemo = new Intent(this, MarkerDemo.class);
        startActivity(markerDemo);
    }
}

