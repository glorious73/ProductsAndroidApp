package com.example.glorious.sqlitecontentproviderslab7;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class MainActivity extends AppCompatActivity {

    // UI references.
    private Button btnAdd;
    private Button btnDelete;
    private Button btnView;
    private Button btnUpdate;
    private EditText edtName;
    private EditText edtCourse;
    private EditText edtYear;
    private EditText edtId;
    // Database handler object
    DBHandler db;
    // static variable for showing one or all students
    static boolean allStudents = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Set up the form
        edtName   = (EditText) findViewById(R.id.edtName);
        edtCourse = (EditText) findViewById(R.id.edtCourse);
        edtYear   = (EditText) findViewById(R.id.edtYear);
        edtId     = (EditText) findViewById(R.id.edtId);
        btnAdd    = (Button) findViewById(R.id.btnAddData);
        btnDelete = (Button) findViewById(R.id.btnDelete);
        btnView   = (Button) findViewById(R.id.btnV);
        btnUpdate = (Button) findViewById(R.id.btnUpdate);
    }

    /*--------------------------Essential methods-------------------------------*/
    public void newStudent(View view) {
        // DB
        db = new DBHandler(this, null, null, 1);
        // Attributes (Columns)
        String stdName="", crsName=""; int year=0;
        try {
            stdName = edtName.getText().toString();
            crsName = edtCourse.getText().toString();
            year    = Integer.parseInt(edtYear.getText().toString());
        } catch(Exception ex) {
            Toast.makeText(this, "Please fill \"Student name\", \"Course\", and \"Year level\" fields.", Toast.LENGTH_LONG).show();
            return;
        }
        // Add the student
        String added = db.addStudent(new Student(stdName, crsName, year));
        db.close();
        // Toast result
        Toast.makeText(this, added, Toast.LENGTH_LONG).show();
        // Clear fields
        clearFields();
    }

    public void searchStudent(View view) {
        // DB
        db = new DBHandler(this, null, null, 1);
        // Attributes (Columns)
        String stdName = edtName.getText().toString().isEmpty() ? "" : edtName.getText().toString();
        int stdId      = edtId.getText().toString().isEmpty() ? -1 : Integer.parseInt(edtId.getText().toString());
        Student student;
        // Try to find the student by name or Id (priority for id)
        if(stdId != -1) {
            student = db.lookupStudentById(stdId);
        } else {
            student = db.lookupStudentByName(stdName);
        }
        db.close();
        // If student exists, show student in a pop-up window
        if(student != null) {
            // one student flag
            MainActivity.allStudents = false;
            // save student to sharedPreferences to give data to pop up window
            saveStudentToSp(student);
            // Show student info in a pop-up window
            showPopup();
        } else {
            // Show "Student no found" pop-up window
            showPopup();
        }
        // Clear UI fields
        clearFields();
    }

    public void showAllStudents(View view) {
        // DB
        db = new DBHandler(this, null, null, 1);
        // allStudents flag
        MainActivity.allStudents = true;
        // Students
        Student[] students = db.lookupAllStudents();
        if(students != null) {
            // Save all to SP
            saveAllToSP(students);
            // Show them in a pop-up window
            showPopup();
        } else {
            // Show "Student no found" pop-up window
            showPopup();
        }


    }

    public void updateStudent(View view) {
        // Same as search, except for showing the values on the edit fields and calling update on DBHandler
        // DB
        db = new DBHandler(this, null, null, 1);
        // Attributes (Columns)
        String stdName="", crsName=""; int year=0, id=0;
        try {
            stdName = edtName.getText().toString();
            crsName = edtCourse.getText().toString();
            year    = Integer.parseInt(edtYear.getText().toString());
            id      = Integer.parseInt(edtId.getText().toString());
        } catch (Exception ex) {
            Toast.makeText(this, "Please fill all fields properly to update a student's record.", Toast.LENGTH_LONG).show();
            return;
        }
        // Try to update the student's record
        String updated = db.updateStudent(new Student(id, stdName, crsName, year));
        db.close();
        // Toast result to user
        Toast.makeText(this, updated, Toast.LENGTH_LONG).show();
        // Finally clear fields
        clearFields();
    }

    public void deleteStudent(View view) {
        // DB
        db = new DBHandler(this, null, null, 1);
        // Read student ID
        int id = -1;
        try {
            id = Integer.parseInt(edtId.getText().toString());
        } catch (Exception ex) {
            Toast.makeText(this, "Please fill the \"id\" field to delete a record.", Toast.LENGTH_LONG).show();
            return;
        }
        // Try to delete the student's record
        String deleted = db.deleteStudent(id);
        db.close();
        Toast.makeText(this, deleted, Toast.LENGTH_LONG).show();
        // Finally clear fields
        clearFields();
    }
    /*--------------------------Helper methods-------------------------------*/
    public void showPopup() {
        startActivity(new Intent(getApplicationContext(), PopActivity.class));
    }

    public void clearFields() {
        edtName.setText("");
        edtCourse.setText("");
        edtYear.setText("");
        edtId.setText("");
        edtId.setFocusable(true);
    }

    private void saveStudentToSp(Student std) {
        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        // save data in sharedPref
        editor.putString("stdName", std.getName());
        editor.putString("stdCourse", std.getCourse());
        editor.putInt("stdYear", std.getYear());
        editor.putInt("stdId", std.getID());
        // Commit
        editor.commit();
    }

    private void saveAllToSP(Student[] allStudents) {
        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        // Counter
        int i = 0;
        // Loop through all students and add them to SP
        for(Student student: allStudents) {
            // save data in sharedPref
            editor.putString("stdName" + i, student.getName());
            editor.putString("stdCourse" + i, student.getCourse());
            editor.putInt("stdYear" + i, student.getYear());
            editor.putInt("stdId" + i, student.getID());
            editor.commit();
            i++;
        }
        // Save the value of "i" to be used in pop-up window
        editor.putInt("counter", i);
        editor.commit();
    }
}

