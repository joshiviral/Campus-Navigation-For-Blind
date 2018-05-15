/*
This is a adapter file for reminder page. It will take alarm_item as layout file.
Reference:
 */
package com.example.dummy.campusnavforblind;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.example.dummy.campusnavforblind.ReminderDatabasePackage.ReminderDbConfig;


public class AlarmAdapter extends CursorAdapter {


    private TextView Titletv, DateTimetv; // textview for reminder title, date-time
    private ImageView letterImage; // imageview for letter image
    private ColorGenerator colorGenerator = ColorGenerator.DEFAULT; // color for letter image
    private TextDrawable textDrawable;

    public AlarmAdapter(Context context, Cursor c) {
        super(context, c, 0 );
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.alarm_items, parent, false); // gettting layout file
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        Titletv = (TextView) view.findViewById(R.id.remind_title); // textview for title
        DateTimetv = (TextView) view.findViewById(R.id.remind_date_time); // textview for date-time

        letterImage = (ImageView) view.findViewById(R.id.letter_image); // imageview for letter image

        // getting data from cursor and assigning to variables
        int titleColumnIndex = cursor.getColumnIndex(ReminderDbConfig.ReminderEntry.KEY_TITLE);  // assigning reminder title
        int dateColumnIndex = cursor.getColumnIndex(ReminderDbConfig.ReminderEntry.KEY_DATE); // assigning reminder date
        int timeColumnIndex = cursor.getColumnIndex(ReminderDbConfig.ReminderEntry.KEY_TIME); // assigning rmeinder time


        //assigning reminder title, date and time
        String title = cursor.getString(titleColumnIndex);
        String date = cursor.getString(dateColumnIndex);
        String time = cursor.getString(timeColumnIndex);



        // setting title of reminder
        setReminderTitle(title);


        if (date != null){
            // if date is not null, set date time
            String dateTime = date + " " + time;
            setReminderDateTime(dateTime);
        }else{
            // if date is null
            DateTimetv.setText("Date not set");
        }



    }

    // Setting reminder title
    public void setReminderTitle(String title) {
        Titletv.setText(title); // set reminder title as title
        String letter = "A";

        //getting first letter of title
        if(title != null && !title.isEmpty()) {
            letter = title.substring(0, 1);
        }

        // setting random color for letter image
        int color = colorGenerator.getRandomColor();

        // making round letter image with random color in background, and first letter of title
        textDrawable = TextDrawable.builder()
                .buildRound(letter, color);
        letterImage.setImageDrawable(textDrawable);
    }

    // Setting reminder datetime of reminder
    public void setReminderDateTime(String datetime) {
        DateTimetv.setText(datetime);
    }

}
