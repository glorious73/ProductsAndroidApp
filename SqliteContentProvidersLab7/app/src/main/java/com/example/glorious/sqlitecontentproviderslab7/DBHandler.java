package com.example.glorious.sqlitecontentproviderslab7;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;

import java.util.Arrays;
import java.util.LinkedList;

public class DBHandler extends SQLiteOpenHelper {
    // Database attributes
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "studentsDB.db";
    // Student Table attributes
    private static final String TABLE_NAME = "STUDENTS";
    private static final String COLUMN_NAME = "studentName";
    private static final String COLUMN_COURSE = "courseName";
    private static final String COLUMN_YEAR = "yearLevel";
    private static final String COLUMN_ID = "_studentId";

    // Constructor override
    public DBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    // Create the table and initialize the DB with it
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Write the statement to make the students table
        String CREATE_STUDENTS_TABLE
                = "CREATE TABLE " + TABLE_NAME + " (" + COLUMN_ID + " INTEGER PRIMARY KEY,"
                  + COLUMN_NAME + " TEXT," + COLUMN_COURSE + " TEXT," + COLUMN_YEAR + " INTEGER)";
        // Execute create table
        db.execSQL(CREATE_STUDENTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // No need to be implemented --> Just call onCreate(db);
        onCreate(db);
    }

    public String addStudent(Student student) {
        // First, lookup student
        Student tmpStudent = lookupStudentByName(student.getName());
        // Now, if student exists, display the "student exists" and retrieve the value of the student
        if(tmpStudent != null) {
            return "Student \"" + student.getName() + "\" already exists. Operation aborted.";
        } else {
            // Else, add the student
            ContentValues values = new ContentValues();
            // No need to put ID since https://www.sqlite.org/autoinc.html (read documentation)
            values.put(COLUMN_NAME, student.getName());
            values.put(COLUMN_COURSE, student.getCourse());
            values.put(COLUMN_YEAR, student.getYear());
            SQLiteDatabase db = this.getWritableDatabase();
            db.insert(TABLE_NAME, null, values);
            db.close();
            return "added student \"" + student.getName() + "\" successfully";
        }
    }

    public Student lookupStudentByName(String name) {
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_NAME + " = \"" + name + "\"";
        return lookupStudent(query);
    }

    public Student lookupStudentById(int id) {
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = " + id;
        return lookupStudent(query);
    }

    private Student lookupStudent(String query) {
        // 1. Get reference to DB
        SQLiteDatabase db = this.getWritableDatabase();
        // 2. Execute the query (returns a Cursor object)
        Cursor cursor = db.rawQuery(query, null);
        // 3. Check if student was found (either student or null)
        Student student;
        if(cursor.moveToFirst()) {
            cursor.moveToFirst();
            student = new Student(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2), Integer.parseInt(cursor.getString(3)));
        } else {
            student = null;
        }
        return student;
    }

    public Student[] lookupAllStudents() {
        // 1. Initialize list of all students
        LinkedList<Student> students = new LinkedList<>();
        // 2. Execute query
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        // Add all students to list
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                int id        = Integer.parseInt(cursor.getString(0));
                String name   = cursor.getString(1);
                String course = cursor.getString(2);
                int year      = Integer.parseInt(cursor.getString(3));
                students.add(new Student(id, name, course, year));
                cursor.moveToNext();
            }
        }
        return students.toArray(new Student[students.size()]);
    }

    public String updateStudent(Student student) {
        // 1. Find the student with the respective name
        Student tmpStudent = lookupStudentById(student.getID());
        // 2. If student found, update student with given info (and set result of operation to true)
        if(tmpStudent != null) {
            // 2.1 Update student data
            ContentValues values = new ContentValues();
            values.put(COLUMN_ID, student.getID()); // Matched in the sqlite table to update data.
            values.put(COLUMN_NAME, student.getName());
            values.put(COLUMN_COURSE, student.getCourse());
            values.put(COLUMN_YEAR, student.getYear());
            // 2.2 Execute REPLACE query using replace() method
            SQLiteDatabase db = this.getWritableDatabase();
            db.replace(TABLE_NAME, null, values);
            db.close();
            return "Student " + student.getName() + "\'s data was updated successfully.";
        }
        return "Student " + student.getName() + " was not found.";
    }

    public String deleteStudent(int studentID) {
        // 1. Lookup student
        Student tmpStudent = lookupStudentById(studentID);
        // 2. If student found, delete student (and set result of operation to true)
        if(tmpStudent != null) {
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[] {String.valueOf(tmpStudent.getID())});
            db.close();
            return "Student " + tmpStudent.getName() + "\'s record was deleted successfully";
        }
        return "Student was not found.";
    }
}
