package com.example.glorious.sqlitecontentproviderslab7;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class PopActivity extends Activity {

    TextView txtStudentInfo;
    SharedPreferences sharedPref;
    String studentName;
    String studentCourse;
    int yearLevel;
    int studentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop);
        // Data for one student or all of them
        if(MainActivity.allStudents) {
            showAllStudents();
        } else {
            showOneStudent();
        }
        // Display them in the Pop up window
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width  = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int)(width*0.8), (int)(height*0.6));
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = -20;
        getWindow().setAttributes(params);
        // Close pop-up button
        Button btnClose = (Button) findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearSP();
                finish(); // close pop-up window
            }
        });
    }

    private void showOneStudent() {
        // Retrieve student's data using shared preferences
        sharedPref     = PreferenceManager.getDefaultSharedPreferences(this);
        studentName    = sharedPref.getString("stdName", "NOT FOUND");
        studentCourse  = sharedPref.getString("stdCourse", "NOT FOUND");
        yearLevel      = sharedPref.getInt("stdYear", 0);
        studentId      = sharedPref.getInt("stdId", 0);
        txtStudentInfo = (TextView) findViewById(R.id.txtStudentInfo);
        // Set text based on finding the student
        if(studentName.equals("NOT FOUND")) {
            txtStudentInfo.setText("Student not found. Please try again with a different name or ID.");
        } else {
            txtStudentInfo.setText("Student name:\t" + studentName + "\nStudent ID:\t" + studentId + "\nCourse:\t" + studentCourse + "\nYear Level:\t" + yearLevel);
        }
    }

    private void showAllStudents() {
        // Retrieve student's data using shared preferences
        sharedPref       = PreferenceManager.getDefaultSharedPreferences(this);
        int counterLimit = sharedPref.getInt("counter", 0); // number of students
        txtStudentInfo = (TextView) findViewById(R.id.txtStudentInfo); // to show
        if(counterLimit == 0) {
            txtStudentInfo.setText("Student DB is empty.");
            return;
        }
        // Loop through students, retrieve their values and show them.
        String studentsResult = "";
        for(int i = 0; i<counterLimit; i++) {
            studentName    = sharedPref.getString("stdName" + i, "NOT FOUND");
            studentCourse  = sharedPref.getString("stdCourse" + i, "NOT FOUND");
            yearLevel      = sharedPref.getInt("stdYear" + i, 0);
            studentId      = sharedPref.getInt("stdId" + i, 0);
            studentsResult += "ID:\t" + studentId + "\nStudent Name:\t" + studentName
                              + "\nYear level:\t" + yearLevel + "\nCourse:\t" + studentCourse
                              + "\n-----------------------\n";
        }
        // Now show them on pop-up window
        txtStudentInfo.setText(studentsResult);
    }

    private void clearSP() {
        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        // Clear Shared Preferences data
        editor.clear();
        editor.commit();
    }

}
