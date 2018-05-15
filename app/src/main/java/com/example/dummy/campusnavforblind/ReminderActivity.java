package com.example.dummy.campusnavforblind;


import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;


import com.example.dummy.campusnavforblind.ReminderDatabasePackage.ReminderDbConfig;



public class ReminderActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private FloatingActionButton addButton; // floating button object
    AlarmAdapter alarmAdapter;  // object of alarm adapter
    ListView reminderListView;  // listview for reminder list

    private static final int REMINDER_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder); // set layout file


        //object for list view and add reminder button
        reminderListView = (ListView) findViewById(R.id.listView);
        addButton = (FloatingActionButton) findViewById(R.id.addButton);

        //starting reminder data adapter
        alarmAdapter = new AlarmAdapter(ReminderActivity.this, null);
        reminderListView.setAdapter(alarmAdapter); // adding adapter to listview

        // on clicking add reminder button, go to add reminder page.
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), AddReminderActivity.class);
                startActivity(intent);
            }
        });

        //onclicking listview, pass id and uri to add reminder page for fetching purpose
        reminderListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                Intent intent = new Intent(ReminderActivity.this, AddReminderActivity.class);

                Uri currentUri = ContentUris.withAppendedId(ReminderDbConfig.ReminderEntry.CONTENT_URI,id);
                intent.setData(currentUri);

                startActivity(intent);

            }
        });

        // call this method to fetch data of reminder from db
        getSupportLoaderManager().initLoader(REMINDER_ID, null, this);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        //definign variables
        String[] projection = {
                ReminderDbConfig.ReminderEntry._ID,
                ReminderDbConfig.ReminderEntry.KEY_TITLE,
                ReminderDbConfig.ReminderEntry.KEY_DATE,
                ReminderDbConfig.ReminderEntry.KEY_TIME

        };

        // return cursor data
        return new CursorLoader(this,   // Parent activity context
                ReminderDbConfig.ReminderEntry.CONTENT_URI,   // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        alarmAdapter.swapCursor(cursor);
        }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // on resetting data, update list
        alarmAdapter.swapCursor(null);
        }
}
