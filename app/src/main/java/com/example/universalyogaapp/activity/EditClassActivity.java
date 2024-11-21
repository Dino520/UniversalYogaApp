package com.example.universalyogaapp.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.universalyogaapp.R;
import com.example.universalyogaapp.database.YogaDatabaseHelper;
import com.example.universalyogaapp.util.FirebaseSyncHelper;

public class EditClassActivity extends AppCompatActivity {

    private EditText dayInput, timeInput, typeInput;
    private Button updateButton, deleteButton;
    private int courseId;
    private YogaDatabaseHelper dbHelper;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_class);

        dbHelper = new YogaDatabaseHelper(this);

        // Initialize input fields
        dayInput = findViewById(R.id.dayInput);
        timeInput = findViewById(R.id.timeInput);
        typeInput = findViewById(R.id.typeInput);

        // Initialize buttons
        updateButton = findViewById(R.id.updateButton);
        deleteButton = findViewById(R.id.deleteButton);

        // Get courseId from intent
        courseId = getIntent().getIntExtra("courseId", -1);

        // Set onClickListeners
        updateButton.setOnClickListener(v -> updateClassDetails());
        deleteButton.setOnClickListener(v -> showDeleteConfirmationDialog());
    }

    // Method to update class details
    private void updateClassDetails() {
        String day = dayInput.getText().toString().trim();
        String time = timeInput.getText().toString().trim();
        String type = typeInput.getText().toString().trim();

        if (day.isEmpty() || time.isEmpty() || type.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("day", day);
        values.put("time", time);
        values.put("type", type);

        int rowsUpdated = db.update("yoga_courses", values, "id=?", new String[]{String.valueOf(courseId)});
        if (rowsUpdated > 0) {
            Toast.makeText(this, "Class updated successfully!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error updating class.", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to show delete confirmation dialog
    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Class")
                .setMessage("Are you sure you want to delete this class?")
                .setPositiveButton("Yes", (dialog, which) -> deleteClass())
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    // Method to delete the class
    private void deleteClass() {
        // Delete the course from the local SQLite database
        boolean isDeleted = dbHelper.deleteCourse(courseId);

        if (isDeleted) {
            Toast.makeText(this, "Class deleted successfully!", Toast.LENGTH_SHORT).show();

            // Delete the course from Firebase
            FirebaseSyncHelper.deleteCourseFromFirebase(this, courseId);

            // Finish the activity and go back to the previous screen
            finish();
        } else {
            Toast.makeText(this, "Error deleting class.", Toast.LENGTH_SHORT).show();
        }
    }
}
