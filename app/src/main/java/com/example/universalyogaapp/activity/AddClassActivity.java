package com.example.universalyogaapp.activity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.universalyogaapp.R;
import com.example.universalyogaapp.database.YogaDatabaseHelper;
import com.example.universalyogaapp.model.YogaCourse;

public class AddClassActivity extends AppCompatActivity {

    private Spinner dayOfWeekSpinner, timeSpinner, typeSpinner;
    private EditText priceEditText, teacherEditText, dateEditText, descriptionEditText;
    private YogaDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_class);

        dbHelper = new YogaDatabaseHelper(this);

        // Initialize views
        dayOfWeekSpinner = findViewById(R.id.dayOfWeekSpinner);
        timeSpinner = findViewById(R.id.timeSpinner);
        typeSpinner = findViewById(R.id.typeSpinner);
        priceEditText = findViewById(R.id.priceEditText);
        teacherEditText = findViewById(R.id.teacherEditText);
        dateEditText = findViewById(R.id.dateEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        Button addButton = findViewById(R.id.addButton);
        Button clearButton = findViewById(R.id.clearButton);

        // Populate Spinners
        ArrayAdapter<CharSequence> dayAdapter = ArrayAdapter.createFromResource(
                this, R.array.days_of_week, android.R.layout.simple_spinner_item);
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dayOfWeekSpinner.setAdapter(dayAdapter);

        ArrayAdapter<CharSequence> timeAdapter = ArrayAdapter.createFromResource(
                this, R.array.times_of_day, android.R.layout.simple_spinner_item);
        timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeSpinner.setAdapter(timeAdapter);

        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(
                this, R.array.class_types, android.R.layout.simple_spinner_item);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(typeAdapter);

        // Add button logic
        addButton.setOnClickListener(v -> addClassToDatabase());

        // Clear button logic
        clearButton.setOnClickListener(v -> clearFields());
    }

    private void addClassToDatabase() {
        String dayOfWeek = dayOfWeekSpinner.getSelectedItem().toString();
        String time = timeSpinner.getSelectedItem().toString();
        String type = typeSpinner.getSelectedItem().toString();
        String priceText = priceEditText.getText().toString();
        String teacher = teacherEditText.getText().toString();
        String date = dateEditText.getText().toString();
        String description = descriptionEditText.getText().toString();

        if (dayOfWeek.isEmpty() || time.isEmpty() || priceText.isEmpty() || teacher.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields.", Toast.LENGTH_SHORT).show();
            return;
        }


        double price = Double.parseDouble(priceText);

        // Set default values for capacity and duration if they are not provided
        int capacity = 10; // Default capacity

        YogaCourse course = new YogaCourse(
                0, // ID (auto-generated)
                dayOfWeek,
                time,
                10, // Default capacity
                price,
                price, type,
                description,
                teacher,
                date
        );

        long id = dbHelper.insertCourse(course);
        if (id != -1) {
            Toast.makeText(this, "Class added successfully!", Toast.LENGTH_SHORT).show();
            clearFields();
        } else {
            Toast.makeText(this, "Failed to add class.", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearFields() {
        priceEditText.setText("");
        teacherEditText.setText("");
        dateEditText.setText("");
        descriptionEditText.setText("");
        dayOfWeekSpinner.setSelection(0);
        timeSpinner.setSelection(0);
        typeSpinner.setSelection(0);
    }
}
