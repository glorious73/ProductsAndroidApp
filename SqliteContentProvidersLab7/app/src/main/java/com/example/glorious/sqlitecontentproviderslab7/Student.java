package com.example.glorious.sqlitecontentproviderslab7;

public class Student {
    // Attributes
    private String name;
    private String course;
    private int year;
    private int id;
    // Constructors
    public Student() {}
    public Student(String name, String course, int year) {
        this.name = name;
        this.course = course;
        this.year = year;
    }
    public Student(int id, String name, String course, int year) {
        this.name = name;
        this.course = course;
        this.year = year;
        this.id = id;
    }
    // Setters and accessors
    public void setName(String s) {
        this.name = s;
    }
    public void setCourse(String s) {
        this.course = s;
    }
    public void setYear(int i) {
        this.year = i;
    }
    public void setId(int i) {
        this.id = i;
    }
    public String getName() {
        return this.name;
    }
    public String getCourse() {
        return this.course;
    }
    public int getYear() {
        return this.year;
    }
    public int getID() {
        return this.id;
    }
}
