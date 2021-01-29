package com.example.studolist;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studolist.Adapter.EventRecyclerAdapter;
import com.example.studolist.Adapter.GridAdapter;
import com.example.studolist.Model.Events;
import com.example.studolist.Utils.DatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class CustomCalendar extends LinearLayout {

    ImageButton next, prev;
    TextView CurrentDate;
    GridView gridView;


    private static final int MAX_CALENDAR_DAYS= 42;
    Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
    Context context;
    SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd yyyy", Locale.ENGLISH);
    SimpleDateFormat monthsFormat = new SimpleDateFormat("MMMM", Locale.ENGLISH);
    SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.ENGLISH);
    SimpleDateFormat eventDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

    GridAdapter gridAdapter;
    AlertDialog alertDialog;

    List<Date> dates = new ArrayList<>();
    List<Events> eventsList = new ArrayList<>();

    DatabaseHelper db;



    public CustomCalendar(Context context) {
        super(context);
    }

    public CustomCalendar(final Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        InitializeLayout();
        setUpCalendar();

        prev.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.MONTH, -1);
                setUpCalendar();

            }
        });

        next.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.MONTH, 1);
                setUpCalendar();
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(true);
                final View addView = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_new_event, null);
                final EditText eventName = addView.findViewById(R.id.eventName);
                final TextView eventTime = addView.findViewById(R.id.eventTime);
                ImageButton setTime = addView.findViewById(R.id.setEventTime);
                Button addEvent = addView.findViewById(R.id.addEvent);

                setTime.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Calendar calendar = Calendar.getInstance();
                        int hours = calendar.get(Calendar.HOUR_OF_DAY);
                        int minute = calendar.get(Calendar.MINUTE);
                        TimePickerDialog timePickerDialog = new TimePickerDialog(addView.getContext(), R.style.Theme_AppCompat_Dialog,
                                new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                        Calendar c = Calendar.getInstance();
                                        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                        c.set(Calendar.MINUTE, minute);
                                        c.setTimeZone(TimeZone.getDefault());
                                        SimpleDateFormat hFormat = new SimpleDateFormat("K:mm a", Locale.ENGLISH);
                                        String event_time = hFormat.format(c.getTime());
                                        eventTime.setText(event_time);
                                    }
                                },hours, minute, false);
                                    timePickerDialog.show();
                    }
                });

                final String date = eventDateFormat.format(dates.get(position));
                final String months = monthsFormat.format(dates.get(position));
                final String year = yearFormat.format(dates.get(position));

                addEvent.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        saveEvent(eventName.getText().toString(), eventTime.getText().toString(), date, months, year);
                        setUpCalendar();
                        alertDialog.dismiss();
                    }
                });

                builder.setView(addView);
                alertDialog = builder.create();
                alertDialog.show();
            }
        });

        //eventrecycler
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String date = eventDateFormat.format(dates.get(position));

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(true);
                View showView = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_events_layout, null);


                //showeventrowlayout
                RecyclerView recyclerView = showView.findViewById(R.id.recyclerViewEvent);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(showView.getContext());
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setHasFixedSize(true);
                EventRecyclerAdapter eventRecyclerAdapter = new EventRecyclerAdapter(showView.getContext(),
                        CollectEventByDate(date));
                recyclerView.setAdapter(eventRecyclerAdapter);
                eventRecyclerAdapter.notifyDataSetChanged();

                builder.setView(showView);
                alertDialog = builder.create();
                alertDialog.show();
                alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        setUpCalendar();
                    }
                });





                return true;
            }
        });



    }
    //eventrecycleradapter
    private ArrayList<Events> CollectEventByDate(String date){
        ArrayList<Events> arrayList = new ArrayList<>();
        db = new DatabaseHelper(context);
        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = db.readEvents(date, database);
        while (cursor.moveToNext()){
            String event = cursor.getString(cursor.getColumnIndex(DatabaseHelper.EVENT));
            String time = cursor.getString(cursor.getColumnIndex(DatabaseHelper.TIME));
            String Date = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DATE));
            String month = cursor.getString(cursor.getColumnIndex(DatabaseHelper.MONTH));
            String Year = cursor.getString(cursor.getColumnIndex(DatabaseHelper.YEAR));
            Events events = new Events(event, time, Date, month, Year);
            arrayList.add(events);
        }
        cursor.close();
        db.close();

        return  arrayList;
    }

    public CustomCalendar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void saveEvent(String event, String time, String date, String month, String year){
        db = new DatabaseHelper(context);
        SQLiteDatabase database = db.getWritableDatabase();
        db.saveEvent(event, time, date, month, year, database);
        db.close();
        Toast.makeText(context, "Event Saved", Toast.LENGTH_SHORT).show();

    }

    private void InitializeLayout(){
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.activity_custom_calendar, this);
        next = view.findViewById(R.id.nextButton);
        prev = view.findViewById(R.id.previousButton);
        CurrentDate = view.findViewById(R.id.currentDate);
        gridView = view.findViewById(R.id.gridView);
    }

    private void setUpCalendar(){
        String currentDate = dateFormat.format(calendar.getTime());
        CurrentDate.setText(currentDate);
        dates.clear();
        Calendar monthCalendar = (Calendar)calendar.clone();
        monthCalendar.set(Calendar.DAY_OF_MONTH,1);
        int FirstDayOfMonth = monthCalendar.get(Calendar.DAY_OF_WEEK)-1;
        monthCalendar.add(Calendar.DAY_OF_MONTH, -FirstDayOfMonth);
        CollectEventsPerMonths(monthsFormat.format(calendar.getTime()), yearFormat.format(calendar.getTime()));

        while (dates.size() < MAX_CALENDAR_DAYS){
            dates.add(monthCalendar.getTime());
            monthCalendar.add(Calendar.DAY_OF_MONTH, 1);

        }
        gridAdapter = new GridAdapter(context, dates, calendar, eventsList);
        gridView.setAdapter(gridAdapter);




    }

    private void CollectEventsPerMonths(String Month, String year){
        eventsList.clear();
        db = new DatabaseHelper(context);
        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = db.readEventsperMonths(Month, year, database);
        while(cursor.moveToNext()){
            String event = cursor.getString(cursor.getColumnIndex(DatabaseHelper.EVENT));
            String time = cursor.getString(cursor.getColumnIndex(DatabaseHelper.TIME));
            String date = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DATE));
            String month = cursor.getString(cursor.getColumnIndex(DatabaseHelper.MONTH));
            String Year = cursor.getString(cursor.getColumnIndex(DatabaseHelper.YEAR));
            Events events = new Events(event, time, date, month, Year);
            eventsList.add(events);
        }
        cursor.close();
        db.close();
    }


}
