package com.example.dummy.campusnavforblind.ReminderDatabasePackage;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

public class ReminderQueryProvider extends ContentProvider {

    public static final String LOG_TAG = ReminderQueryProvider.class.getSimpleName();

    private static final int REMINDER = 100; // id for insertion

    private static final int REMINDER_ID = 101; // id for view,delete and edit


    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH); // uri matcher object to compare new uri with existing one

    static {

        sUriMatcher.addURI(ReminderDbConfig.CONTENT_AUTHORITY, ReminderDbConfig.REMINDER_PATH, REMINDER); // for insertion

        sUriMatcher.addURI(ReminderDbConfig.CONTENT_AUTHORITY, ReminderDbConfig.REMINDER_PATH + "/#", REMINDER_ID); // for view,edit and insertion

    }

    private ReminderDbHelper reminderDbHelper; // database helper class object

    @Override
    public boolean onCreate() {
        // on create, make instance of a helper class
        reminderDbHelper = new ReminderDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase database = reminderDbHelper.getReadableDatabase(); // get database

        Cursor cursor = null; // cursor to hold data resulting from query

        int match = sUriMatcher.match(uri); // match object for table uri
        switch (match) {
            case REMINDER:
                cursor = database.query(ReminderDbConfig.ReminderEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder); // query for view and insert
                break;
            case REMINDER_ID:
                selection = ReminderDbConfig.ReminderEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };  // query for edit and delete

                cursor = database.query(ReminderDbConfig.ReminderEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder); // execute query for edit nad delete
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }


        cursor.setNotificationUri(getContext().getContentResolver(), uri); // finalize execution
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case REMINDER:
                return ReminderDbConfig.ReminderEntry.CONTENT_LIST_TYPE; // retunn if required for viewing and insertion
            case REMINDER_ID:
                return ReminderDbConfig.ReminderEntry.CONTENT_ITEM_TYPE; // return if required for edit and delete
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    // method to insert data in reminder table
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case REMINDER:
                return insertReminder(uri, contentValues); // if insertion,call this method

            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri); // show error if failed
        }
    }

    // method call when insertion is required
    private Uri insertReminder(Uri uri, ContentValues values) {

        SQLiteDatabase database = reminderDbHelper.getWritableDatabase(); // get database

        long id = database.insert(ReminderDbConfig.ReminderEntry.TABLE_NAME, null, values); // insert value in db

        // if failed to return, retun from here
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // if inserted successfully, return new uri
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }


    // method for delete reminder purpose
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase database = reminderDbHelper.getWritableDatabase(); // get database

        int rowsDeleted; // count for total raws deleted

        final int match = sUriMatcher.match(uri); // take rmeinder uri
        switch (match) {
            case REMINDER:
                rowsDeleted = database.delete(ReminderDbConfig.ReminderEntry.TABLE_NAME, selection, selectionArgs); // delete for whole db
                break;
            case REMINDER_ID:
                selection = ReminderDbConfig.ReminderEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(ReminderDbConfig.ReminderEntry.TABLE_NAME, selection, selectionArgs); // delete query when selected entry is seletd
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        // if number of raws deleted are greater then 0, return count
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    //  method to update reminder table
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri); // get uri
        switch (match) {
            case REMINDER:
                return updateReminder(uri, contentValues, selection, selectionArgs); // query for simple update purpose
            case REMINDER_ID:
                selection = ReminderDbConfig.ReminderEntry._ID + "=?"; // selection part in update query
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateReminder(uri, contentValues, selection, selectionArgs); // call update method to update table
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    // method call when edit is required
    private int updateReminder(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        // if no records found, return
        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = reminderDbHelper.getWritableDatabase(); //get databse

        int rowsUpdated = database.update(ReminderDbConfig.ReminderEntry.TABLE_NAME, values, selection, selectionArgs); // execute query to update table

        // if data updated on table, return data
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }
}
