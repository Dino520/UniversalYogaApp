package com.example.universalyogaapp.model;

public class YogaCourse {
    private int id;
    private String day;
    private String time;
    private int capacity;
    private double price;
    private String type;
    private String description;
    private String teacherName;
    private String date;

    // Constructor with all parameters
    public YogaCourse(int id, String day, String time, int capacity, double price, double v, String type, String description, String teacherName, String date) {
        this.id = id;
        this.day = day;
        this.time = time;
        this.capacity = capacity;
        this.price = price;
        this.type = type;
        this.description = description;
        this.teacherName = teacherName;
        this.date = date;
    }

    // Add getter and setter methods for each field
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
