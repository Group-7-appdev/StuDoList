package com.example.studolist;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class Calendar_view extends AppCompatActivity {

    CustomCalendar customCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_view);

        customCalendar = (CustomCalendar)findViewById(R.id.calendar_view);

    }
}