package com.example.dummy.campusnavforblind;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelper extends SQLiteOpenHelper {

    static String DATABASE_NAME="TIMETABLE"; // db name

    public static final String TABLE_NAME="table2"; // table name

    // table columns
    public static final String Table_Column_ID="id";
    public static final String Table_Column_1="subject";
    public static final String Table_Column_2="professor";
    public static final String Table_Column_3="room";
    public static final String Table_Column_4="day";
    public static final String Table_Column_5="type";
    public static final String Table_Column_6="start";
    public static final String Table_Column_7="end";

    public SQLiteHelper(Context context1, String databaseName, Context context, int i) {

        // if first time, build db
        super(context, DATABASE_NAME, null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase database) {

        // on create, create table
        String CREATE_TABLE="CREATE TABLE IF NOT EXISTS "+TABLE_NAME+" ("+Table_Column_ID+" INTEGER PRIMARY KEY, "+Table_Column_1+" VARCHAR, "+Table_Column_2+" VARCHAR, "+Table_Column_3+" VARCHAR, "+Table_Column_4+" VARCHAR, "+Table_Column_5+" VARCHAR, "+Table_Column_6+" VARCHAR, "+Table_Column_7+" VARCHAR)";
        database.execSQL(CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on update, drop exixting table
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);

    }



}
