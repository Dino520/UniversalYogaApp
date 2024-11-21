package com.example.universalyogaapp.activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.universalyogaapp.R;
import com.example.universalyogaapp.database.YogaDatabaseHelper;
import com.example.universalyogaapp.model.YogaCourse;
import com.example.universalyogaapp.util.FirebaseSyncHelper;

import java.util.ArrayList;

public class ViewClassesActivity extends AppCompatActivity {

    private ListView classListView;
    private ArrayAdapter<String> adapter;
    private ArrayList<YogaCourse> courseList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_classes);

        classListView = findViewById(R.id.classListView);
        Button uploadButton = findViewById(R.id.uploadButton);

        // Load classes from SQLite database
        loadClasses();

        // Set onClickListener for the list items
        classListView.setOnItemClickListener((parent, view, position, id) -> {
            YogaCourse selectedCourse = courseList.get(position);
            Intent intent = new Intent(ViewClassesActivity.this, EditClassActivity.class);
            intent.putExtra("courseId", selectedCourse.getId());
            startActivity(intent);
        });

        // Set onClickListener for the upload button
        uploadButton.setOnClickListener(v -> FirebaseSyncHelper.uploadAllClassesToFirebase(ViewClassesActivity.this));
    }

    private void loadClasses() {
        YogaDatabaseHelper dbHelper = new YogaDatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(YogaDatabaseHelper.TABLE_COURSES, null, null, null, null, null, null);

        courseList = new ArrayList<>();
        ArrayList<String> courseNames = new ArrayList<>();

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String day = cursor.getString(cursor.getColumnIndexOrThrow("day"));
            String time = cursor.getString(cursor.getColumnIndexOrThrow("time"));
            int capacity = cursor.getInt(cursor.getColumnIndexOrThrow("capacity"));
            int duration = cursor.getInt(cursor.getColumnIndexOrThrow("duration"));
            double price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));
            String type = cursor.getString(cursor.getColumnIndexOrThrow("type"));
            String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
            String teacherName = cursor.getString(cursor.getColumnIndexOrThrow("teacher")); // Added
            String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));           // Added

            YogaCourse course = new YogaCourse(id, day, time, capacity, duration, price, type, description, teacherName, date);
            courseList.add(course);
            courseNames.add(type + " - " + day + " - " + time + " (" + teacherName + ")");
        }

        cursor.close();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, courseNames);
        classListView.setAdapter(adapter);
    }


    private void viewInstances(int courseId) {
        YogaDatabaseHelper dbHelper = new YogaDatabaseHelper(this);
        Cursor cursor = dbHelper.getInstancesByCourseId(courseId);
        ArrayList<String> instanceDetails = new ArrayList<>();

        while (cursor.moveToNext()) {
            String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
            String teacher = cursor.getString(cursor.getColumnIndexOrThrow("teacher"));
            String comments = cursor.getString(cursor.getColumnIndexOrThrow("comments"));
            instanceDetails.add(date + " - " + teacher + (comments.isEmpty() ? "" : " (" + comments + ")"));
        }

        cursor.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, instanceDetails);
        ListView instanceListView = findViewById(R.id.instanceListView);
        instanceListView.setAdapter(adapter);
    }

}
