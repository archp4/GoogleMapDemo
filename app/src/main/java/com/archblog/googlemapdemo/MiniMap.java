package com.archblog.googlemapdemo;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MiniMap extends AppCompatActivity {


    Button igs,north,lakeshore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mini_map);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        igs=findViewById(R.id.btn_downtown);
        north=findViewById(R.id.btn_north);
        lakeshore=findViewById(R.id.btn_lakeshore);
        changeLocation(1);
        igs.setOnClickListener((View v)->{
            changeLocation(1);
        });
        north.setOnClickListener((View v)->{
            changeLocation(2);
        });
        lakeshore.setOnClickListener((View v)->{
            changeLocation(3);
        });
    }

    private void changeLocation(int position){
        replaceFragment(new MapsFragment(position));
    }
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.miniMapHolder, fragment);
        fragmentTransaction.commit();
    }


}