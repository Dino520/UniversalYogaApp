package com.example.universalyogaapp.activity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.universalyogaapp.R;
import com.example.universalyogaapp.adapter.YogaClassAdapter;
import com.example.universalyogaapp.database.YogaDatabaseHelper;
import com.example.universalyogaapp.model.YogaCourse;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private YogaDatabaseHelper dbHelper;
    private EditText searchInputTeacher, searchInputDate, searchInputDay;
    private RecyclerView resultRecyclerView;
    private YogaClassAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Initialize database helper
        dbHelper = new YogaDatabaseHelper(this);

        // Initialize views
        searchInputTeacher = findViewById(R.id.searchInputTeacher);
        searchInputDate = findViewById(R.id.searchInputDate);
        searchInputDay = findViewById(R.id.searchInputDay);
        Button searchButton = findViewById(R.id.searchButton);
        resultRecyclerView = findViewById(R.id.resultRecyclerView); // Correct ID

        // Initialize RecyclerView and Adapter
        adapter = new YogaClassAdapter(new ArrayList<>());
        resultRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        resultRecyclerView.setAdapter(adapter);

        // Set click listener for search button
        searchButton.setOnClickListener(v -> performSearch());
    }

    private void performSearch() {
        String teacher = searchInputTeacher.getText().toString().trim();
        String date = searchInputDate.getText().toString().trim();
        String dayOfWeek = searchInputDay.getText().toString().trim();

        List<YogaCourse> results = dbHelper.searchClassesByFilters(teacher, date, dayOfWeek);

        if (results == null || results.isEmpty()) {
            Toast.makeText(this, "No results found.", Toast.LENGTH_SHORT).show();
            adapter.updateData(new ArrayList<>()); // Clear existing data
        } else {
            adapter.updateData(results); // Update adapter with new results
        }
    }

}
