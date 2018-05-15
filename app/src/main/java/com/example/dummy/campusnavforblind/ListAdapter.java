/*
this page is adpater class for view timetable's listview
 */
package com.example.dummy.campusnavforblind;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ListAdapter extends BaseAdapter {

    // arraylist object initialization
    Context context;
    ArrayList<String> ID;
    ArrayList<String> SUBJECT;
    ArrayList<String> PROFESSOR;
    ArrayList<String> ROOM;
    ArrayList<String> DAY;
    ArrayList<String> TYPE;
    ArrayList<String> START;
    ArrayList<String> END;


    // constructor
    public ListAdapter(
            Context context2,
            ArrayList<String> id,
            ArrayList<String> subject,
            ArrayList<String> professor,
            ArrayList<String> room,
            ArrayList<String> day,
            ArrayList<String> type,
            ArrayList<String> start,
            ArrayList<String> end
    )
    {

        // constructor assignment
        this.context = context2;
        this.ID = id;
        this.SUBJECT = subject;
        this.PROFESSOR = professor;
        this.ROOM = room;
        this.DAY = day;
        this.TYPE = type;
        this.START = start;
        this.END = end;
    }

    // returns colunt of table records
    public int getCount() {
        return ID.size();
    }

    // gives item with position specified
    public Object getItem(int position) {
        return null;
    }

    // gives item position
    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View child, ViewGroup parent) {

        // holder object
        Holder holder;

        // layout inflater
        LayoutInflater layoutInflater;

        // if no previous inflator
        if (child == null) {
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            // inflate with item layout
            child = layoutInflater.inflate(R.layout.items, null);

            holder = new Holder();

            // getting all element of list and assign to holder object
            holder.ID_TextView = (TextView) child.findViewById(R.id.textViewID);
            holder.SUBJECT_TextView = (TextView) child.findViewById(R.id.textViewSUBJECT);
            holder.PROFESSOR_TextView = (TextView) child.findViewById(R.id.textViewPROFESSOR);
            holder.ROOM_TextView = (TextView) child.findViewById(R.id.textViewROOM);
            holder.DAY_TextView = (TextView) child.findViewById(R.id.textViewDAY);
            holder.TYPE_TextView = (TextView) child.findViewById(R.id.textViewTYPE);
            holder.START_TextView = (TextView) child.findViewById(R.id.textViewSTART);
            holder.END_TextView = (TextView) child.findViewById(R.id.textViewEND);


            child.setTag(holder);

        } else {

            holder = (Holder) child.getTag();
        }
        // assign holder object to item layout
        holder.ID_TextView.setText(ID.get(position));
        holder.SUBJECT_TextView.setText(SUBJECT.get(position));
        holder.PROFESSOR_TextView.setText(PROFESSOR.get(position));
        holder.ROOM_TextView.setText(ROOM.get(position));
        holder.DAY_TextView.setText(DAY.get(position));
        holder.TYPE_TextView.setText(TYPE.get(position));
        holder.START_TextView.setText(START.get(position));
        holder.END_TextView.setText(END.get(position));
        return child;
    }

    public class Holder {

        // variable of holder
        TextView ID_TextView;
        TextView SUBJECT_TextView;
        TextView PROFESSOR_TextView;
        TextView ROOM_TextView;
        TextView DAY_TextView;
        TextView TYPE_TextView;
        TextView START_TextView;
        TextView END_TextView;

    }

}

