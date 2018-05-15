package com.example.dummy.campusnavforblind;


import android.app.TimePickerDialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

public class AddTimetable extends AppCompatActivity {

    SQLiteDatabase sqLiteDatabaseObj; //database object
    EditText subject, professor,room,start,end;   //edit text of suject naem,professor name, room number, start time, end time
    String subjectHolder, professorHolder,roomHolder,format,startHolder,endHolder, SQLiteDataBaseQueryHolder;
    Button EnterData; // object variable for add button
    Boolean EditTextEmptyHold; // boolean to check if any field is empty or not
    Calendar calendar; // calender object
    Spinner dateSpin,typeSpin; // date and class type sipnner

    //spinner varibale data with fix value
    String selectedDay = "Monday";
    String selectedClass = "Lecture";

    private int CalendarHour, CalendarMinute; // varibale to hold seleted hour and minute
    TimePickerDialog timepickerdialog; // object for time picker


    String[] daysofWeek={"Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"}; //spinner data for dayas
    String[] classType = {"Lecture","Lab"}; //spinner data for class type

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_timetable);

        EnterData = (Button)findViewById(R.id.add); // object of add timetable button

        // object of edit text of subject,professor name, room number,start time and end time
        subject = (EditText)findViewById(R.id.editText);
        professor = (EditText)findViewById(R.id.professorName);
        room = (EditText)findViewById(R.id.roomNumber);
        start = (EditText)findViewById(R.id.startTime);
        end = (EditText)findViewById(R.id.endTime);

        //oject of spinner days and class type
        dateSpin = (Spinner)findViewById(R.id.daySpinner);
        typeSpin = (Spinner)findViewById(R.id.typeSpinner);



        //spinner code-----------------------------

        // code for day picker

        // arrayadapter of days name
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,daysofWeek);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        //adding arrayadapter data on days spinner
        dateSpin.setAdapter(aa);

        // on clicking days spinner
        dateSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDay = daysofWeek[position]; // get id of selected day
                // Toast.makeText(getApplicationContext(),selectedDay,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        // arrayadapter for class type
        ArrayAdapter bb = new ArrayAdapter(this,android.R.layout.simple_spinner_item,classType);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        //adding arrayadapter to class type spinner
        typeSpin.setAdapter(bb);

        // on clicking class type spinner
        typeSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedClass = classType[position]; // get id of selected class type
                // Toast.makeText(getApplicationContext(),selectedClass,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // on clicking start time picker
        start.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {

                // get current hours and minute
                calendar = Calendar.getInstance();
                CalendarHour = calendar.get(Calendar.HOUR_OF_DAY);
                CalendarMinute = calendar.get(Calendar.MINUTE);


                // set timetext edittext to user selected time
                timepickerdialog = new TimePickerDialog(AddTimetable.this,
                        new TimePickerDialog.OnTimeSetListener() {

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

                                start.setText(hourOfDay + ":" + minute + format); // set start time to selected time
                            }
                        }, CalendarHour, CalendarMinute, false);
                timepickerdialog.show();

            }

        });

        // clicking on end time picker
        end.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {

                // getting current hours and minutes
                calendar = Calendar.getInstance();
                CalendarHour = calendar.get(Calendar.HOUR_OF_DAY);
                CalendarMinute = calendar.get(Calendar.MINUTE);


                timepickerdialog = new TimePickerDialog(AddTimetable.this,
                        new TimePickerDialog.OnTimeSetListener() {

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


                                end.setText(hourOfDay + ":" + minute + format); // setting end time to user selected end time
                            }
                        }, CalendarHour, CalendarMinute, false);
                timepickerdialog.show();

            }

        });




        // on clicking add timetable record button
        EnterData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                SQLiteDataBaseBuild(); // method to  build database object

                SQLiteTableBuild(); // method to  building table in to database

                CheckEditTextStatus(); // method to check if edittext are empty or not

                InsertDataIntoSQLiteDatabase(); // method to insert timetable data to database

                EmptyEditTextAfterDataInsert(); // method to empty all after insertion fields


            }
        });


    }

    public void SQLiteDataBaseBuild(){

        // create database if not exists
        sqLiteDatabaseObj = openOrCreateDatabase(SQLiteHelper.DATABASE_NAME, Context.MODE_PRIVATE, null);

    }

    public void SQLiteTableBuild(){

        // build table if not exist which will hold out timetable data named, subject name, prof name, class time, room number, start time and end time
        sqLiteDatabaseObj.execSQL("CREATE TABLE IF NOT EXISTS "+SQLiteHelper.TABLE_NAME+" ("+SQLiteHelper.Table_Column_ID+" " +
                "INTEGER PRIMARY KEY, "+SQLiteHelper.Table_Column_1+" VARCHAR, "+SQLiteHelper.Table_Column_2+" VARCHAR, "+
                SQLiteHelper.Table_Column_3+" VARCHAR, "+SQLiteHelper.Table_Column_4+" VARCHAR, "+SQLiteHelper.Table_Column_5+" VARCHAR, "+
                SQLiteHelper.Table_Column_6+" VARCHAR, "+SQLiteHelper.Table_Column_7+" VARCHAR)");

    }

    public void CheckEditTextStatus(){

        // get values of subject, prof name, rokm number, start time and end time edittext
        subjectHolder = subject.getText().toString() ;
        professorHolder = professor.getText().toString();
        roomHolder = room.getText().toString();
        startHolder = start.getText().toString();
        endHolder = end.getText().toString();



        if(TextUtils.isEmpty(subjectHolder) || TextUtils.isEmpty(professorHolder) || TextUtils.isEmpty(roomHolder) || TextUtils.isEmpty(startHolder) || TextUtils.isEmpty(endHolder)){
        // if any of the field is empty return false with error
            EditTextEmptyHold = false ;

        }
        else {
        // if not field is empty, pass datas
            EditTextEmptyHold = true ;
        }
    }

    public void InsertDataIntoSQLiteDatabase(){

        if(EditTextEmptyHold == true)
        {

            // insert all data to database table
            SQLiteDataBaseQueryHolder = "INSERT INTO "+SQLiteHelper.TABLE_NAME+" (subject,professor,room,day,type,start,end) VALUES('"+subjectHolder+"','"+professorHolder+"','"+roomHolder+"','"+selectedDay+"','"+selectedClass+"','"+startHolder+"','"+endHolder+"');";

            sqLiteDatabaseObj.execSQL(SQLiteDataBaseQueryHolder);

            sqLiteDatabaseObj.close();

            // message on successful insertion
            Toast.makeText(AddTimetable.this,"Data Inserted Successfully", Toast.LENGTH_LONG).show();

        }
        else {

            // message if any of the text filed is empty.
            Toast.makeText(AddTimetable.this,"Please Fill All The Required Fields.", Toast.LENGTH_LONG).show();

        }

    }

    public void EmptyEditTextAfterDataInsert(){

        // clear all edit text filed to clear after insertion
        subject.getText().clear();
        professor.getText().clear();
        room.getText().clear();
        start.getText().clear();
        end.getText().clear();

    }

}
