package com.example.dummy.campusnavforblind.ReminderDatabasePackage;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

public class ReminderDbConfig {

    private ReminderDbConfig() {}

    public static final String CONTENT_AUTHORITY = "com.example.dummy.campusnavforblind"; // package authority

    public static final Uri REMINDER_URI = Uri.parse("content://com.example.dummy.campusnavforblind"); // uri for table data

    public static final String REMINDER_PATH = "reminder-path"; // rmeinder storage path

    public static final class ReminderEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(REMINDER_URI, REMINDER_PATH); // uri for db functions

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + REMINDER_PATH;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + REMINDER_PATH;

        public final static String TABLE_NAME = "reminder2"; // table name

        public final static String _ID = BaseColumns._ID; // primary key for table

        public static final String KEY_TITLE = "title"; // column title
        public static final String KEY_DATE = "date"; // column date
        public static final String KEY_TIME = "time"; // column time

    }

    // mathod to fetch data from column in cursor object
    public static String getColumnString(Cursor cursor, String columnName) {
        return cursor.getString( cursor.getColumnIndex(columnName) );
    }
}
