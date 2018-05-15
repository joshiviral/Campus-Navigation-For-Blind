package com.example.dummy.campusnavforblind;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import com.example.dummy.campusnavforblind.ReminderDatabasePackage.ReminderDbConfig;
import com.example.dummy.campusnavforblind.ReminderDatabasePackage.ReminderDbHelper;
import com.example.dummy.campusnavforblind.ReminderServicePackage.ReminderScheduler;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;

public class AddReminderActivity extends AppCompatActivity implements
        TimePickerDialog.OnTimeSetListener,
        DatePickerDialog.OnDateSetListener, LoaderManager.LoaderCallbacks<Cursor>{

    private static final int CURRENT_REMINDER_ID = 0;  // varibale that holds id of an reminder which already exists


    SQLiteDatabase sqLiteDatabaseObj; // obejct for database
    ReminderDbHelper sqliteHelper; // object of reinderdbhelper
    Cursor cursor; // curson object that holds current data


    private EditText reminderTitle; // object varibale for reminder title
    private TextView rDate, rTime; // object varibale for reminder date and time

    private Calendar calendar; // objetc of calender
    private int rYear, rMonth, rHour, rMinute, rDay; // variables that holds reminder timedata

    // varibale that hold data of already existing reminder
    private String Title;
    private String Time;
    private String Date;

    private Uri reminderUri; // uri of reminder that already exist in system


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder);

        //object of reminder title,date and time
        reminderTitle = (EditText) findViewById(R.id.reminderTitle);
        rDate = (TextView) findViewById(R.id.reminderDate);
        rTime = (TextView) findViewById(R.id.reminderTime);


        // get reminder uri that already exist.
        Intent intent = getIntent();
        reminderUri = intent.getData();



        if (reminderUri == null) {
            // if uri is null, then set title of add reminder page to add reminder
            setTitle(getString(R.string.editor_activity_title_new_reminder));
            invalidateOptionsMenu();
        } else {

            // if uri exist, then set title of add reminder page to edit
            setTitle(getString(R.string.editor_activity_title_edit_reminder));
            getLoaderManager().initLoader(CURRENT_REMINDER_ID, null, this); // load data from database for existing reminder using uri.
        }




        // varibale that holds current date and time
        calendar = Calendar.getInstance();
        rHour = calendar.get(Calendar.HOUR_OF_DAY);
        rMinute = calendar.get(Calendar.MINUTE);
        rYear = calendar.get(Calendar.YEAR);
        rMonth = calendar.get(Calendar.MONTH) + 1;
        rDay = calendar.get(Calendar.DATE);

        // varibale which we use to set calender and time picker to current date and time
        Date = rDay + "/" + rMonth + "/" + rYear;
        Time = rHour + ":" + rMinute;

        // Setup Reminder Title EditText
        reminderTitle.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Title = s.toString().trim(); // set title for reminder to placeholder
                reminderTitle.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Setup textview of date picker and timepicker to current date and time
        rDate.setText(Date);
        rTime.setText(Time);


        }


        // On clicking time picker
    public void setTime(View v){

        //get time picker
        Calendar now = Calendar.getInstance();
        TimePickerDialog tpd = TimePickerDialog.newInstance(this, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), false);
        tpd.setThemeDark(false);
        tpd.show(getFragmentManager(), "Timepickerdialog"); // get fragment of timepicker
    }

    // On clicking date picker
    public void setDate(View v){
// get fragment of datepicker
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.show(getFragmentManager(), "Datepickerdialog");
    }


    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {

        // on setting time on timepicker
        rHour = hourOfDay;
        rMinute = minute;
        if (minute < 10) {
            Time = hourOfDay + ":" + "0" + minute;
        } else {
            Time = hourOfDay + ":" + minute;
        }
        rTime.setText(Time); // update timetext from current time to selected time
    }

    // Obtain date from date picker
    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        monthOfYear ++;
        rDay = dayOfMonth;
        rMonth = monthOfYear;
        rYear = year;
        Date = dayOfMonth + "/" + monthOfYear + "/" + year;
        rDate.setText(Date); // update datetext from curren to selected date.
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // setting menu for the page
        getMenuInflater().inflate(R.menu.menu_add_reminder, menu);
        return true;
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (reminderUri == null) {
            MenuItem menuItem = menu.findItem(R.id.delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            // on clicking save button on menu bar
            case R.id.save:

                if (reminderTitle.getText().toString().length() == 0) {
                    // give error message if reminder title is blank.
                    reminderTitle.setError("Reminder Title cannot be blank!");
                } else {

                    // add reminder data to content values
                    ContentValues values = new ContentValues();

                    values.put(ReminderDbConfig.ReminderEntry.KEY_TITLE, Title);
                    values.put(ReminderDbConfig.ReminderEntry.KEY_DATE, Date);
                    values.put(ReminderDbConfig.ReminderEntry.KEY_TIME, Time);


                    // setup calender for enetred value by user
                    calendar.set(Calendar.MONTH, --rMonth);
                    calendar.set(Calendar.YEAR, rYear);
                    calendar.set(Calendar.DAY_OF_MONTH, rDay);
                    calendar.set(Calendar.HOUR_OF_DAY, rHour);
                    calendar.set(Calendar.MINUTE, rMinute);
                    calendar.set(Calendar.SECOND, 0);

                    long timer = calendar.getTimeInMillis(); // get remaining time in milis from current time to rmeinder time.


                    if (reminderUri == null) {

                        // if uri is null, means its a first time insertion reminder


                        // add reminder data
                        Uri newUri = getContentResolver().insert(ReminderDbConfig.ReminderEntry.CONTENT_URI, values);
                        int insert_id = 0;

                        if (newUri == null) {
                            // if reminder was no added
                            Toast.makeText(this, "Error on adding/saving reminder", Toast.LENGTH_SHORT).show();
                        } else {

                            // if reminder was added
                            Toast.makeText(this, "Reminder added/saved", Toast.LENGTH_SHORT).show();

                            // get data of newly entered reminder form database.
                            sqLiteDatabaseObj = openOrCreateDatabase("remindermodule.db", Context.MODE_PRIVATE, null);
                            sqliteHelper = new ReminderDbHelper(this);
                            sqLiteDatabaseObj = sqliteHelper.getWritableDatabase();

                            cursor = sqLiteDatabaseObj.rawQuery("SELECT * FROM " + ReminderDbConfig.ReminderEntry.TABLE_NAME + " ", null);
                            // take cursor to last to get id of newly entered reminder
                            if (cursor.moveToLast()) {

                                insert_id = cursor.getInt(0);
                                cursor.close();

                            }
                            // Toast.makeText(this, "ID="+record_id, Toast.LENGTH_SHORT).show();
                            // assign new reminder for newly entered reminder data.
                            reminderUri = ContentUris.withAppendedId(ReminderDbConfig.ReminderEntry.CONTENT_URI, insert_id);
                        }

                    } else {

                        // for already existing reminder

                        // update reminder data
                        int rowsUpdate = getContentResolver().update(reminderUri, values, null, null);


                        if (rowsUpdate == 0) {
                            // message on error on updating reminder data
                            Toast.makeText(this, "Error on updaing error",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                           // message of succession of update
                            Toast.makeText(this, getString(R.string.editor_update_reminder_successful),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    // add new scheduler for reminder.
                    new ReminderScheduler().setAlarm(getApplicationContext(), timer, reminderUri);
finish();
                    // navigate to home page
                   // Intent i = new Intent(AddReminderActivity.this, ActivityHomePage.class);
                   // startActivity(i);
                }
                return true;

            case R.id.delete:

                // on clicking delete button

                // ask user for confirmation
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.delete_dialog_msg);
                builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (reminderUri != null) {
                        // on clicking delete button of popup alert

                            // delete reminder data from database
                            int rowsDeleted = getContentResolver().delete(reminderUri, null, null);

                            if (rowsDeleted == 0) {
                                // error message if record was not deleted
                                Toast.makeText(AddReminderActivity.this, "Error on deleting reminder",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                               // success message if record was deleted
                                Toast.makeText(AddReminderActivity.this, getString(R.string.editor_delete_reminder_successful),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                        finish();
                        // navigate to home page
                        //Intent i = new Intent(AddReminderActivity.this, ActivityHomePage.class);
                       // startActivity(i);
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if cancel button was clicked. dismiss popup alert.
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                    }
                });


                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                return true;

                }

        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        }




    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        // laod data from database if you are editing exisitng reminder data.
        String[] projection = {
                ReminderDbConfig.ReminderEntry._ID,
                ReminderDbConfig.ReminderEntry.KEY_TITLE,
                ReminderDbConfig.ReminderEntry.KEY_DATE,
                ReminderDbConfig.ReminderEntry.KEY_TIME,

        };

       // load data from database for given reminder uri.
        return new CursorLoader(this, reminderUri, projection, null, null, null);
    }

    // after completing loading
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        // if got no record, return
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // if got record
        if (cursor.moveToFirst()) {
            int columnTitle = cursor.getColumnIndex(ReminderDbConfig.ReminderEntry.KEY_TITLE);
            int ColumnDate = cursor.getColumnIndex(ReminderDbConfig.ReminderEntry.KEY_DATE);
            int ColumnTime = cursor.getColumnIndex(ReminderDbConfig.ReminderEntry.KEY_TIME);


            //  get reminder title,date and time
            String title = cursor.getString(columnTitle);
            String date = cursor.getString(ColumnDate);
            String time = cursor.getString(ColumnTime);

            // set reminder title, date and time to fetched value
            reminderTitle.setText(title);
            rDate.setText(date);
            rTime.setText(time);

        }


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

}
