package com.example.universalyogaapp.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.example.universalyogaapp.database.YogaDatabaseHelper;
import com.example.universalyogaapp.model.YogaCourse;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class FirebaseSyncHelper {

    private static final DatabaseReference databaseRef = FirebaseDatabase.getInstance("https://universal-yoga-app-52459-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("yoga_courses");

    public static void startFirebaseListener(Context context) {
        YogaDatabaseHelper dbHelper = new YogaDatabaseHelper(context);

        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot courseSnapshot : dataSnapshot.getChildren()) {
                    try {
                        Map<String, Object> courseData = (Map<String, Object>) courseSnapshot.getValue();
                        if (courseData != null) {
                            String teacherName = (String) courseData.getOrDefault("teacherName", "Unknown");

                            double price = 0;
                            YogaCourse course = new YogaCourse(
                                    Integer.parseInt(courseSnapshot.getKey()),
                                    (String) courseData.getOrDefault("day", "Unknown"),
                                    (String) courseData.getOrDefault("time", "Unknown"),
                                    ((Long) courseData.getOrDefault("capacity", 10)).intValue(),
                                    (double) courseData.getOrDefault("price", 0.0),
                                    price, (String) courseData.getOrDefault("type", "General"),
                                    (String) courseData.getOrDefault("description", ""),
                                    teacherName,
                                    (String) courseData.getOrDefault("date", "2024-01-01")
                            );

                            // Insert or update the course in SQLite
                            dbHelper.insertOrUpdateCourse(course);
                        }
                    } catch (Exception e) {
                        Log.e("FirebaseSyncHelper", "Error syncing data: " + e.getMessage());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FirebaseSyncHelper", "Sync cancelled: " + databaseError.getMessage());
            }
        });
    }


    public static void uploadCourse(Context context, YogaCourse course) {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            Toast.makeText(context, "No network connection. Please try again later.", Toast.LENGTH_SHORT).show();
            return;
        }

        String courseId = String.valueOf(course.getId());
        Map<String, Object> courseData = new HashMap<>();
        courseData.put("day", course.getDay());
        courseData.put("time", course.getTime());
        courseData.put("capacity", course.getCapacity());
        courseData.put("price", course.getPrice());
        courseData.put("type", course.getType());
        courseData.put("description", course.getDescription());
        courseData.put("teacherName", course.getTeacherName());
        courseData.put("date", course.getDate());

        databaseRef.child(courseId).setValue(courseData)
                .addOnSuccessListener(aVoid -> Toast.makeText(context, "Data synchronized successfully.", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(context, "Failed to synchronize data: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    public static void deleteCourseFromFirebase(Context context, int courseId) {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            Toast.makeText(context, "No network connection. Please try again later.", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference courseRef = databaseRef.child(String.valueOf(courseId));
        courseRef.removeValue()
                .addOnSuccessListener(aVoid -> Toast.makeText(context, "Course deleted successfully from Firebase.", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(context, "Failed to delete course from Firebase: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    public static void uploadAllClassesToFirebase(Context context) {
        YogaDatabaseHelper dbHelper = new YogaDatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.query(YogaDatabaseHelper.TABLE_COURSES, null, null, null, null, null, null);

            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String day = cursor.getString(cursor.getColumnIndexOrThrow("day"));
                String time = cursor.getString(cursor.getColumnIndexOrThrow("time"));
                int capacity = cursor.getInt(cursor.getColumnIndexOrThrow("capacity"));
                double price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));
                String type = cursor.getString(cursor.getColumnIndexOrThrow("type"));
                String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                String teacherName = cursor.getString(cursor.getColumnIndexOrThrow("teacher"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));

                YogaCourse course = new YogaCourse(id, day, time, capacity, price, price, type, description, teacherName, date);
                uploadCourse(context, course);
            }

        } catch (Exception e) {
            Log.e("FirebaseSyncHelper", "Error uploading all classes: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        Toast.makeText(context, "All classes uploaded to Firebase.", Toast.LENGTH_SHORT).show();
    }
}