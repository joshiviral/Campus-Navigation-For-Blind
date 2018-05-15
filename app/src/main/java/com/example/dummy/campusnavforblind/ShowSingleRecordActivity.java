package com.example.dummy.campusnavforblind;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import static com.example.dummy.campusnavforblind.SQLiteHelper.DATABASE_NAME;


public class ShowSingleRecordActivity extends AppCompatActivity {

    String IDholder; // variable that holds id for which we should ask in database
    TextView id, subject, professor, room, day, type,start,end; // textview objects
    SQLiteDatabase sqLiteDatabase; // object of sqlite db
    SQLiteHelper sqLiteHelper; // object of datbasehelper clas
    Cursor cursor; // cursor object
    Button Delete, Edit; // button for delete and edit
    SQLiteDatabase sqLiteDatabaseObj;
    String SQLiteDataBaseQueryHolder ; // sqlite query holder

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_single_record); // set layout page

        // object for all textview where we will show our fetched value
        id = (TextView) findViewById(R.id.textViewID);
        subject = (TextView) findViewById(R.id.textViewSubject);
        professor = (TextView) findViewById(R.id.textViewProfessor);
        room = (TextView)findViewById(R.id.textViewRoom);

        day = (TextView)findViewById(R.id.textViewDay);
        type = (TextView)findViewById(R.id.textViewType);
        start = (TextView)findViewById(R.id.textViewStart);
        end = (TextView)findViewById(R.id.textViewEnd);


        // object for delete and edit button
        Delete = (Button)findViewById(R.id.buttonDelete);
        Edit = (Button)findViewById(R.id.buttonEdit);

        //getting database of timetable
        sqLiteHelper = new SQLiteHelper(getApplicationContext(), DATABASE_NAME, this, 1);

        // on clicking delete button
        Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //ask for user confirmation
                AlertDialog.Builder builder = new AlertDialog.Builder(ShowSingleRecordActivity.this);

                builder.setTitle("Confirm");
                builder.setMessage("Are you sure?");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                        // if user confirms deletion
                        OpenSQLiteDataBase();//open db

                        SQLiteDataBaseQueryHolder = "DELETE FROM "+SQLiteHelper.TABLE_NAME+" WHERE id = "+IDholder+"";//define query

                        sqLiteDatabase.execSQL(SQLiteDataBaseQueryHolder); // execute delete query

                        sqLiteDatabase.close(); // close db

                        finish(); // finish activity


                        dialog.dismiss();

                    }
                });

                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // if user deny, do nothing
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();



            }
        });

        // on clicking edit button, send record id to edit timetable page
        Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(),EditSingleRecordActivity.class);

                intent.putExtra("EditID", IDholder);

                startActivity(intent);

            }
        });

    }

    @Override
    protected void onResume() {

        ShowSingleRecordInTextView();

        super.onResume();
    }

    // method to show all data of selected record
    public void ShowSingleRecordInTextView() {

        sqLiteDatabase = sqLiteHelper.getWritableDatabase(); // open db

        IDholder = getIntent().getStringExtra("ListViewClickedItemValue"); //  get id of the record

        // execute query to select all columns of specific record
        cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + SQLiteHelper.TABLE_NAME + " WHERE id = " + IDholder + "", null);

        // go to first record
        if (cursor.moveToFirst()) {

            do {
                // set textview to their appropriate fetched column value
                id.setText(cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_ID)));
                subject.setText(cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_1)));
                professor.setText(cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_2)));
                room.setText(cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_3)));
                day.setText(cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_4)));
                type.setText(cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_5)));
                start.setText(cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_6)));
                end.setText(cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_7)));

            }
            while (cursor.moveToNext());

            cursor.close();

        }
    }
    // method to open db
    public void OpenSQLiteDataBase(){

        sqLiteDatabaseObj = openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null);

    }
}
