package com.example.studolist.Adapter;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.studolist.Model.Events;
import com.example.studolist.R;
import com.example.studolist.Utils.DatabaseHelper;

import java.util.ArrayList;

public class EventRecyclerAdapter extends RecyclerView.Adapter<EventRecyclerAdapter.myviewHolder> {

    Context context;
    ArrayList<Events> arrayList;
    DatabaseHelper db;

    public EventRecyclerAdapter(Context context, ArrayList<Events> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public myviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_event_rowlayout,parent,false);
        return new myviewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myviewHolder holder, final int position) {
        final Events events = arrayList.get(position);
        holder.Event.setText(events.getEVENT());
        holder.dateText.setText(events.getDATE());
        holder.Time.setText(events.getTIME());
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCalendarEvent(events.getEVENT(),events.getDATE(),events.getTIME());
                arrayList.remove(position);
                notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class myviewHolder extends RecyclerView.ViewHolder{

        TextView dateText, Event, Time;
        Button delete;

        public myviewHolder(@NonNull View itemView) {
            super(itemView);

            dateText = itemView.findViewById(R.id.eventdatelayout);
            Event = itemView.findViewById(R.id.eventnamelayout);
            Time = itemView.findViewById(R.id.eventtimelayout);
            delete = itemView.findViewById(R.id.deleteEvent);

        }
    }


    private void deleteCalendarEvent(String event, String date, String time){
        db = new DatabaseHelper(context);
        SQLiteDatabase database = db.getWritableDatabase();
        db.deleteEvent(event, date, time, database);
        db.close();

    }
}
