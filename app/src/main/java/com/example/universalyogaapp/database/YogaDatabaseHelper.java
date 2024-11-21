package com.example.universalyogaapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.universalyogaapp.model.YogaCourse;
import com.example.universalyogaapp.util.FirebaseSyncHelper;

import java.util.ArrayList;
import java.util.List;

public class YogaDatabaseHelper extends SQLiteOpenHelper {

    // Database details
    private static final String DATABASE_NAME = "YogaClasses.db";
    private static final int DATABASE_VERSION = 4; // Incremented for schema changes

    // Table and column names for courses
    public static final String TABLE_COURSES = "yoga_courses";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_DAY = "day";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_CAPACITY = "capacity";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_TEACHER_NAME = "teacher";
    public static final String COLUMN_DATE = "date";

    private final Context context;

    public YogaDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableCourses = "CREATE TABLE " + TABLE_COURSES + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_DAY + " TEXT NOT NULL DEFAULT 'Unknown', " +
                COLUMN_TIME + " TEXT NOT NULL DEFAULT 'Unknown', " +
                COLUMN_CAPACITY + " INTEGER NOT NULL DEFAULT 10, " +
                COLUMN_PRICE + " REAL NOT NULL DEFAULT 0.0, " +
                COLUMN_TYPE + " TEXT NOT NULL DEFAULT 'General', " +
                COLUMN_DESCRIPTION + " TEXT, " +
                COLUMN_TEACHER_NAME + " TEXT NOT NULL DEFAULT 'Unknown', " +
                COLUMN_DATE + " TEXT NOT NULL DEFAULT '2024-01-01')";
        db.execSQL(createTableCourses);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < DATABASE_VERSION) {
            String tempTable = TABLE_COURSES + "_temp";
            // Backup old table
            db.execSQL("ALTER TABLE " + TABLE_COURSES + " RENAME TO " + tempTable);

            // Recreate the table with updated schema
            onCreate(db);

            // Restore data into the new table, ensuring all columns match
            db.execSQL("INSERT INTO " + TABLE_COURSES + " (" +
                    COLUMN_ID + ", " +
                    COLUMN_DAY + ", " +
                    COLUMN_TIME + ", " +
                    COLUMN_CAPACITY + ", " +
                    COLUMN_PRICE + ", " +
                    COLUMN_TYPE + ", " +
                    COLUMN_DESCRIPTION + ", " +
                    COLUMN_TEACHER_NAME + ", " +
                    COLUMN_DATE +
                    ") SELECT " +
                    COLUMN_ID + ", " +
                    COLUMN_DAY + ", " +
                    COLUMN_TIME + ", " +
                    COLUMN_CAPACITY + ", " +
                    COLUMN_PRICE + ", " +
                    COLUMN_TYPE + ", " +
                    COLUMN_DESCRIPTION + ", " +
                    "IFNULL(" + COLUMN_TEACHER_NAME + ", 'Unknown'), " + // Ensure default value for teacher
                    COLUMN_DATE +
                    " FROM " + tempTable);

            // Drop the temporary table
            db.execSQL("DROP TABLE " + tempTable);
        }
    }

    public long insertCourse(YogaCourse course) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        try {
            values.put(COLUMN_DAY, course.getDay());
            values.put(COLUMN_TIME, course.getTime());
            values.put(COLUMN_CAPACITY, course.getCapacity());
            values.put(COLUMN_PRICE, course.getPrice());
            values.put(COLUMN_TYPE, course.getType());
            values.put(COLUMN_DESCRIPTION, course.getDescription());
            values.put(COLUMN_TEACHER_NAME, course.getTeacherName() != null && !course.getTeacherName().isEmpty()
                    ? course.getTeacherName() : "Unknown"); // Ensure default value
            values.put(COLUMN_DATE, course.getDate());

            return db.insert(TABLE_COURSES, null, values);
        } catch (Exception e) {
            Log.e("YogaDatabaseHelper", "Error inserting course: " + e.getMessage());
            return -1;
        }
    }


public long insertOrUpdateCourse(YogaCourse course) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DAY, course.getDay());
        values.put(COLUMN_TIME, course.getTime());
        values.put(COLUMN_CAPACITY, course.getCapacity());
        values.put(COLUMN_PRICE, course.getPrice());
        values.put(COLUMN_TYPE, course.getType());
        values.put(COLUMN_DESCRIPTION, course.getDescription());
        values.put(COLUMN_TEACHER_NAME, course.getTeacherName());
        values.put(COLUMN_DATE, course.getDate());

        int rowsUpdated = db.update(TABLE_COURSES, values, COLUMN_ID + "=?", new String[]{String.valueOf(course.getId())});
        if (rowsUpdated == 0) {
            long newRowId = db.insert(TABLE_COURSES, null, values);
            if (newRowId != -1) {
                course.setId((int) newRowId);
            }
            FirebaseSyncHelper.uploadCourse(context, course);
            return newRowId;
        } else {
            FirebaseSyncHelper.uploadCourse(context, course);
            return rowsUpdated;
        }
    }

    public List<YogaCourse> searchClassesByFilters(String teacher, String date, String day) {
        List<YogaCourse> yogaClasses = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_COURSES + " WHERE 1=1"; // Always true condition
        List<String> args = new ArrayList<>();

        if (teacher != null && !teacher.isEmpty()) {
            query += " AND " + COLUMN_TEACHER_NAME + " LIKE ?";
            args.add("%" + teacher + "%");
        }

        if (date != null && !date.isEmpty()) {
            query += " AND " + COLUMN_DATE + " = ?";
            args.add(date);
        }

        if (day != null && !day.isEmpty()) {
            query += " AND " + COLUMN_DAY + " LIKE ?";
            args.add("%" + day + "%");
        }

        try (Cursor cursor = db.rawQuery(query, args.toArray(new String[0]))) {
            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                    String courseDay = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DAY));
                    String courseTime = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIME));
                    int capacity = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CAPACITY));
                    double price = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRICE));
                    String type = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE));
                    String description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION));
                    String teacherName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TEACHER_NAME));
                    String dateValue = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE));

                    yogaClasses.add(new YogaCourse(id, courseDay, courseTime, capacity, price, price, type, description, teacherName, dateValue));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("YogaDatabaseHelper", "Error searching classes: " + e.getMessage());
        }
        return yogaClasses;
    }


    public boolean deleteCourse(int courseId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int deletedRows = db.delete(TABLE_COURSES, COLUMN_ID + "=?", new String[]{String.valueOf(courseId)});
        return deletedRows > 0;
    }

    public Cursor getInstancesByCourseId(int courseId) {
        return null;
    }
}
