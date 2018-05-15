/*
This page fetches all record of timetable table and show them in listview. By clicking on listview, you can see all data of specific record on other page
 */
package com.example.dummy.campusnavforblind;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import static com.example.dummy.campusnavforblind.SQLiteHelper.DATABASE_NAME;


public class DisplaySQLiteDataActivity extends AppCompatActivity {

    SQLiteHelper sqLiteHelper; // instance of sqlite helper
    SQLiteDatabase sqLiteDatabase; // object of sqlite database
    Cursor cursor; // curson which will hold database record
    ListAdapter listAdapter ; // adapter to show single timetable record
    ListView LISTVIEW; // listview to show all timetable records

    // arraylist of all table records
    ArrayList<String> ID_Array;
    ArrayList<String> SUBJECT_Array;
    ArrayList<String> PROFESSOR_Array;
    ArrayList<String> ROOM_Array;
    ArrayList<String> DAY_Array;
    ArrayList<String> TYPE_Array;
    ArrayList<String> START_Array;
    ArrayList<String> END_Array;

    // adding all arraylist varibale to arraylist
    ArrayList<String> ListViewClickItemArray = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_sqlite_data); // setting layout file

        LISTVIEW = (ListView) findViewById(R.id.listView1); // listview object

        // instantiating arraylist object
        ID_Array = new ArrayList<String>();
        SUBJECT_Array = new ArrayList<String>();
        PROFESSOR_Array = new ArrayList<String>();
        ROOM_Array = new ArrayList<String>();
        DAY_Array = new ArrayList<String>();
        TYPE_Array = new ArrayList<String>();
        START_Array = new ArrayList<String>();
        END_Array = new ArrayList<String>();

       // getting database for timetable
        sqLiteHelper = new SQLiteHelper(getApplicationContext(), DATABASE_NAME, this, 1);

        // on clicking listview of timetable record
        LISTVIEW.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                // navigate to page that shows touched item's all data
                Intent intent = new Intent(getApplicationContext(),ShowSingleRecordActivity.class);

                intent.putExtra("ListViewClickedItemValue", ListViewClickItemArray.get(position).toString()); // sending id of touched item

                startActivity(intent);

            }
        });

    }

    @Override
    protected void onResume() {
        // show database data after you return from editing or deleting data
        ShowSQLiteDBdata() ;
        super.onResume();
    }

    private void ShowSQLiteDBdata() {

        // get timetable databse
        sqLiteDatabase = sqLiteHelper.getWritableDatabase();

        // getting all data of table one by one
        cursor = sqLiteDatabase.rawQuery("SELECT * FROM "+SQLiteHelper.TABLE_NAME+"", null);

        // clear all arraylist object
        ID_Array.clear();
        SUBJECT_Array.clear();
        PROFESSOR_Array.clear();
        ROOM_Array.clear();
        DAY_Array.clear();
        TYPE_Array.clear();
        START_Array.clear();
        END_Array.clear();

        // go to start of the table
        if (cursor.moveToFirst()) {
            // get all records till you reach to last record
            do {

                // add all fetched column value to arraylist object
                ID_Array.add(cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_ID)));
                ListViewClickItemArray.add(cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_ID)));
                SUBJECT_Array.add(cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_1)));
                PROFESSOR_Array.add(cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_2)));
                ROOM_Array.add(cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_3)));
                DAY_Array.add(cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_4)));
                TYPE_Array.add(cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_5)));
                START_Array.add(cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_6)));
                END_Array.add(cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_7)));


            } while (cursor.moveToNext());
        }

        // adding arraylist to listadapter
        listAdapter = new ListAdapter(DisplaySQLiteDataActivity.this,

                ID_Array,
                SUBJECT_Array,
                PROFESSOR_Array,
                ROOM_Array,
                DAY_Array,
                TYPE_Array,
                START_Array,
                END_Array
        );

        // adding listadapter to listview
        LISTVIEW.setAdapter(listAdapter);
        //close cursor
        cursor.close();
    }
}


