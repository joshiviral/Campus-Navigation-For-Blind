package com.example.dummy.campusnavforblind;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private Context context;
    private List<Notification> notificationList;

    // passing data to notification adapter class, to define listview
    public NotificationAdapter(Context context, List<Notification> notificationList) {
        this.context = context;
        this.notificationList = notificationList;
    }

    // creating inflator for notification list
    @Override
    public NotificationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context); // defining inflator
        View view = inflater.inflate(R.layout.notification_list, null); // passing layout for list
        return new NotificationViewHolder(view);
    }

    // method to assign data to holder
    @Override
    public void onBindViewHolder(NotificationViewHolder holder, int position) {
        //get id of list
        Notification notificationData = notificationList.get(position);
        holder.textViewTitle.setText(notificationData.getTitle());
        holder.textViewShortDesc.setText(notificationData.getShortdesc());

    }

    @Override
    public int getItemCount() {
        //returns list size
        return notificationList.size();
    }

    class NotificationViewHolder extends RecyclerView.ViewHolder {

        TextView textViewTitle, textViewShortDesc; // textview to store notification title and body

        public NotificationViewHolder(View itemView) {
            super(itemView);

            textViewTitle = itemView.findViewById(R.id.textViewTitle);// get titletextview object
            textViewShortDesc = itemView.findViewById(R.id.textViewShortDesc); //get bodytextiew object

        }
    }
}
