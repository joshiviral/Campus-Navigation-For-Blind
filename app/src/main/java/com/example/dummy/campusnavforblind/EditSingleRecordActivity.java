/*
This page edits specific timetable record
 */
package com.example.dummy.campusnavforblind;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import static com.example.dummy.campusnavforblind.SQLiteHelper.DATABASE_NAME;


public class EditSingleRecordActivity extends AppCompatActivity {

    EditText subject, professor,room,start,end; // edittext for subject name, professor name, room number, start time and end time
    Button update; // update button
    SQLiteDatabase sqLiteDatabase; // database object
    SQLiteHelper sqLiteHelper; // object of sqlitehelper
    Cursor cursor; // cursor to hold db records
    String IDholder,format;  // idholder fetched id of record for which you want to fetch data
    String SQLiteDataBaseQueryHolder ; // query
    SQLiteDatabase sqLiteDatabaseObj; //db object



    //Calendar object
    Calendar calendar;

    //spinner object for daysof the week and class type
    Spinner dateSpin,typeSpin;

    //spinner varibale to get previous seleted day and class type
    public String selectedDay,selectedClass;

    //varibales to hold lecture time
    private int CalendarHour, CalendarMinute;
    //object of timepicker
    TimePickerDialog timepickerdialog;

    //spinner data of weekdays and classtype
    String[] daysofWeek={"Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"};
    String[] classType = {"Lecture","Lab"};




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_single_record); // setting layout page


        // object of edit text of subject name, prof name, room number, start tiemand end time
        subject = (EditText) findViewById(R.id.editText);
        professor = (EditText) findViewById(R.id.professorName);
        room = (EditText)findViewById(R.id.roomNumber);
        start = (EditText)findViewById(R.id.startTime);
        end = (EditText)findViewById(R.id.endTime);


       // String pos = selectedDay;
        //update button object
        update = (Button) findViewById(R.id.buttonUpdate);

        // getting db of timetable
        sqLiteHelper = new SQLiteHelper(getApplicationContext(), DATABASE_NAME, this, 1);

        //spinner object of daytype and classtype
        dateSpin = (Spinner)findViewById(R.id.daySpinner);
        typeSpin = (Spinner)findViewById(R.id.typeSpinner);

        //getting databse
        sqLiteDatabase = sqLiteHelper.getWritableDatabase();

        // getting id of the record which you want to show on the page
        IDholder = getIntent().getStringExtra("EditID");

        // select record with given id and assign to cursor
        cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + SQLiteHelper.TABLE_NAME + " WHERE id = " + IDholder + "", null);

        // take cursor to start point
        if (cursor.moveToFirst()) {

            // traverse till you reach the end
            do {
                // getting all values of column and assign their value to variable
                selectedDay = cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_4));
                selectedClass = cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_5));
                subject.setText(cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_1)));
                professor.setText(cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_2)));
                room.setText(cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_3)));
                start.setText(cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_6)));
                end.setText(cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_7)));
            }
            while (cursor.moveToNext());

            //closing cursor
            cursor.close();

            // Toast.makeText(getApplicationContext(),selectedClass,Toast.LENGTH_SHORT).show();
            //Toast.makeText(getApplicationContext(),selectedDay,Toast.LENGTH_SHORT).show();

        }


        //spinner
        //day picker
        //Creating the arrayadapter with daysoftheweek
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,daysofWeek);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //adding arrayAdapter data to day spinner
        dateSpin.setAdapter(aa);

        int spinnerPosition = aa.getPosition(selectedDay); //getting position number of previously added day
        // Toast.makeText(getApplicationContext(),"day1="+selectedDay,Toast.LENGTH_SHORT).show();
        //  Toast.makeText(getApplicationContext(),"postion="+spinnerPosition,Toast.LENGTH_SHORT).show();
        dateSpin.setSelection(spinnerPosition); // setting previous lecture day as seleted

        //on clicking day spinner
        dateSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDay = daysofWeek[position]; // set day of lecture to selected id
                // Toast.makeText(getApplicationContext(),selectedDay,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        // arrayadapter for classtype
        ArrayAdapter bb = new ArrayAdapter(this,android.R.layout.simple_spinner_item,classType);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //adding classadapter to type spinner
        typeSpin.setAdapter(bb);
        int spinnerPosition1 = bb.getPosition(selectedClass); // getting position of previous class type
        // Toast.makeText(getApplicationContext(),"day1="+selectedDay,Toast.LENGTH_SHORT).show();
        // Toast.makeText(getApplicationContext(),selectedClass+"position="+spinnerPosition1,Toast.LENGTH_SHORT).show();

        typeSpin.setSelection(spinnerPosition1); // setting class spinner set to position

        //on clicking class type spinner
        typeSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedClass = classType[position]; // assigning class type to position name
                // Toast.makeText(getApplicationContext(),selectedClass,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // on clicking starttime picker
        start.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {

                // getting calender object with current hour and minute
                calendar = Calendar.getInstance();
                CalendarHour = calendar.get(Calendar.HOUR_OF_DAY);
                CalendarMinute = calendar.get(Calendar.MINUTE);


                timepickerdialog = new TimePickerDialog(EditSingleRecordActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {

                    // on selecting new time in time picker, change start time
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                if (hourOfDay == 0) {

                                    hourOfDay += 12;

                                    format = "AM";
                                }
                                else if (hourOfDay == 12) {

                                    format = "PM";

                                }
                                else if (hourOfDay > 12) {

                                    hourOfDay -= 12;

                                    format = "PM";

                                }
                                else {

                                    format = "AM";
                                }


                                // printing slected time to edittext
                                start.setText(hourOfDay + ":" + minute + format);
                            }
                        }, CalendarHour, CalendarMinute, false);
                timepickerdialog.show();

            }

        });

        // on clicking endtime picker
        end.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {

                // getting object of calender
                calendar = Calendar.getInstance();
                CalendarHour = calendar.get(Calendar.HOUR_OF_DAY);
                CalendarMinute = calendar.get(Calendar.MINUTE);


                //
                timepickerdialog = new TimePickerDialog(EditSingleRecordActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {

                    // on selecting time from time picker
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                if (hourOfDay == 0) {

                                    hourOfDay += 12;

                                    format = "AM";
                                }
                                else if (hourOfDay == 12) {

                                    format = "PM";

                                }
                                else if (hourOfDay > 12) {

                                    hourOfDay -= 12;

                                    format = "PM";

                                }
                                else {

                                    format = "AM";
                                }


                                // set endtime edittext to selected time
                                end.setText(hourOfDay + ":" + minute + format);
                            }
                        }, CalendarHour, CalendarMinute, false);
                timepickerdialog.show();

            }

        });



        // on clicking update button
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // getting value of all edittext
                String GetSubject = subject.getText().toString();
                String GetProfessor = professor.getText().toString();
                String GetRoom = room.getText().toString();
                String GetStart = start.getText().toString();
                String GetEnd = end.getText().toString();

                //open database
                OpenSQLiteDataBase();
                // query to update table with fetched value
                SQLiteDataBaseQueryHolder = "UPDATE " + SQLiteHelper.TABLE_NAME + " SET "+SQLiteHelper.Table_Column_1+" = '"+GetSubject+"' , "+SQLiteHelper.Table_Column_2+" = '"+GetProfessor+"' , "+SQLiteHelper.Table_Column_3+" = '"+GetRoom+"' , "+SQLiteHelper.Table_Column_4+" = '"+selectedDay+"' , "+SQLiteHelper.Table_Column_5+" = '"+selectedClass+"' , "+SQLiteHelper.Table_Column_6+" = '"+GetStart+"' , "+SQLiteHelper.Table_Column_7+" = '"+GetEnd+"' WHERE id = " + IDholder + "";

                //execute query
                sqLiteDatabaseObj.execSQL(SQLiteDataBaseQueryHolder);
                //close db
                sqLiteDatabase.close();

                // toast on success
                Toast.makeText(EditSingleRecordActivity.this,"Data Edit Successfully", Toast.LENGTH_LONG).show();

                // go to timetable home page
                finish();
               // Intent intent = new Intent(EditSingleRecordActivity.this, TimeTableHome.class);
              //  startActivity(intent);

              /*  Intent intent = new Intent(getApplicationContext(),ShowSingleRecordActivity.class);

                intent.putExtra("ListViewClickedItemValue", IDholder);

                startActivity(intent); */

            }
        });

    }

    @Override
    protected void onResume() {

        ShowSRecordInEditText();

        super.onResume();
    }
// method to show previously added data
    public void ShowSRecordInEditText() {

        // getting writeable database
        sqLiteDatabase = sqLiteHelper.getWritableDatabase();

        // getting id of the record which you you want to show
        IDholder = getIntent().getStringExtra("EditID");

        // select record from database with given id
        cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + SQLiteHelper.TABLE_NAME + " WHERE id = " + IDholder + "", null);

        // go to start of the table
        if (cursor.moveToFirst()) {

            // traverse to all column
            do {
                // getting all column values and set them to edit text and spinner value
                selectedDay = cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_4));
                selectedClass = cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_5));
                subject.setText(cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_1)));
                professor.setText(cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_2)));
                room.setText(cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_3)));
                start.setText(cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_6)));
                end.setText(cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_7)));
            }
            while (cursor.moveToNext());

            cursor.close();

            // Toast.makeText(getApplicationContext(),selectedClass,Toast.LENGTH_SHORT).show();
            // Toast.makeText(getApplicationContext(),selectedDay,Toast.LENGTH_SHORT).show();

        }
    }

    public void OpenSQLiteDataBase(){

        // open db
        sqLiteDatabaseObj = openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null);

    }

}

