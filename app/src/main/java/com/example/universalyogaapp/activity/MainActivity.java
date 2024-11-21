package com.example.universalyogaapp.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;
import android.widget.Toast;

import com.example.universalyogaapp.R;
import com.example.universalyogaapp.util.FirebaseSyncHelper;
import com.example.universalyogaapp.util.NetworkUtils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button addClassButton = findViewById(R.id.addClassButton);
        Button viewClassesButton = findViewById(R.id.viewClassesButton);
        Button searchButton = findViewById(R.id.searchButton); // Add search button reference

        // Set click listeners for buttons
        addClassButton.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, AddClassActivity.class)));
        viewClassesButton.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ViewClassesActivity.class)));

        // Navigate to SearchActivity on Search Button click
        searchButton.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, SearchActivity.class)));

        // Start Firebase listener if the network is available
        if (NetworkUtils.isNetworkAvailable(this)) {
            FirebaseSyncHelper.startFirebaseListener(this);
        } else {
            Toast.makeText(this, "No network connection. Firebase listener not started.", Toast.LENGTH_SHORT).show();
        }
    }
}
