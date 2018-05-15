package com.example.dummy.campusnavforblind;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.example.dummy.campusnavforblind.SQLiteHelper.DATABASE_NAME;


/**
 * Created by vkoth on 2018-03-06.
 */

public class TimeTableHome extends AppCompatActivity {

    Button add,view,delete; // object of button

    SQLiteDatabase sqLiteDatabaseObj; // db object
    String SQLiteDataBaseQueryHolder; // query holder
    SQLiteHelper sqLiteHelper; // sqlite helper class object

    String time;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable_home);

        // object of add, view and delete button
        add = (Button)findViewById(R.id.addTimetable);
        view = (Button)findViewById(R.id.viewTimetable);
        delete = (Button)findViewById(R.id.deleteTimetable);


        // selecting timetable database
        sqLiteHelper = new SQLiteHelper(getApplicationContext(), DATABASE_NAME, this, 1);


        // on clicking add button, go to add timetable page
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(TimeTableHome.this, AddTimetable.class);
                startActivity(intent);
            }
        });

        // on clicking view button, go to view timetable page
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(TimeTableHome.this, DisplaySQLiteDataActivity.class);
                startActivity(intent);
            }
        });

        // on clicking delete button
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // ask for user confirmation
                AlertDialog.Builder builder = new AlertDialog.Builder(TimeTableHome.this);

                builder.setTitle("Confirm");
                builder.setMessage("Are you sure?");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                // if user confirms

                        SQLiteDataBaseBuild(); // method to build db
                        DeleteSQLiteDatabase(); // method to delete table
                        dialog.dismiss();

                    }
                });

                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // if user denies, do nothing
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();


            }
        });

    }

    // method to create db
    public void SQLiteDataBaseBuild(){
        // create database
        sqLiteDatabaseObj = openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null);
    }

    // method to create table
    public void SQLiteTableBuild(){
        sqLiteDatabaseObj.execSQL("CREATE TABLE IF NOT EXISTS "+SQLiteHelper.TABLE_NAME+" ("+SQLiteHelper.Table_Column_ID+" INTEGER PRIMARY KEY, "+SQLiteHelper.Table_Column_1+" VARCHAR, "+SQLiteHelper.Table_Column_2+" VARCHAR, "+SQLiteHelper.Table_Column_3+" VARCHAR, "+SQLiteHelper.Table_Column_4+" VARCHAR, "+SQLiteHelper.Table_Column_5+" VARCHAR, "+SQLiteHelper.Table_Column_6+" VARCHAR, "+SQLiteHelper.Table_Column_7+" VARCHAR)");
    }

    // method to delete whole table
    public void DeleteSQLiteDatabase(){

        SQLiteDataBaseQueryHolder = "DELETE FROM "+SQLiteHelper.TABLE_NAME+" "; // delete query

        sqLiteDatabaseObj.execSQL(SQLiteDataBaseQueryHolder); // execute delete query

        sqLiteDatabaseObj.close();

        // give success message
        Toast.makeText(TimeTableHome.this,"Timetable deleted Successfully", Toast.LENGTH_LONG).show();


    }

}

