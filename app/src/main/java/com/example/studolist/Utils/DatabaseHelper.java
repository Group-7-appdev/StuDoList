package com.example.studolist.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.studolist.Model.ToDoModel;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private SQLiteDatabase db;

    //notes
    private static final String DATABASE_NAME = "STUDO_DATABASE";
    private static final String TABLE_NAME = "STUDO_TABLE";
    private static final String COL_1 = "ID";
    private static final String COL_2 = "TASK";
    private static final String COL_3 = "STATUS";


    //Calendar
        public static final String EVENT_TABLE_NAME = "EVENTS_TABLE";
        public static final String EVENT = "EVENT";
        public static final String TIME = "TIME";
        public static final String DATE = "DATE";
        public static final String MONTH = "MONTH";
        public static final String YEAR = "YEAR";


    public DatabaseHelper(@Nullable Context context ) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //notes table
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT , TASK TEXT, STATUS INTEGER)");
        //Calendar table
        db.execSQL("CREATE TABLE IF NOT EXISTS "+ EVENT_TABLE_NAME +"(ID INTEGER PRIMARY KEY AUTOINCREMENT , EVENT TEXT, TIME TEXT, DATE TEXT, MONTH TEXT, YEAR TEXT)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+EVENT_TABLE_NAME);
        onCreate(db);

    }

    public void insertTask(ToDoModel model){
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_2, model.getTask());
        values.put(COL_3, 0);
        db.insert(TABLE_NAME, null, values);
    }

    public void updateTask(int id , String task){
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_2, task);
        db.update(TABLE_NAME, values, "ID=?", new String[]{String.valueOf(id)});
    }


    public void updateStatus(int id, int status){
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_3, status);
        db.update(TABLE_NAME, values, "ID=?", new String[]{String.valueOf(id)});
    }

    public void deleteTask(int id){
        db = this.getWritableDatabase();
        db.delete(TABLE_NAME, "ID=?", new String[]{String.valueOf(id)});
    }

    public List<ToDoModel> getAllTask(){
        db = this.getWritableDatabase();
        Cursor cursor = null;
        List<ToDoModel> modelList = new ArrayList<>();

        db.beginTransaction();
            try{
                cursor = db.query(TABLE_NAME, null,null,null,null,null,null);
                if(cursor != null){
                    if(cursor.moveToFirst()){
                        do {

                            ToDoModel task = new ToDoModel();
                            task.setId(cursor.getInt(cursor.getColumnIndex(COL_1)));
                            task.setTask(cursor.getString(cursor.getColumnIndex(COL_2)));
                            task.setStatus(cursor.getInt(cursor.getColumnIndex(COL_3)));
                            modelList.add(task);

                        }while (cursor.moveToNext());
                    }
                }
            }finally {
                db.endTransaction();
                cursor.close();
            }
            return modelList;
    }

    //Calendar Code

    public void saveEvent(String event, String time, String date, String month, String year, SQLiteDatabase database){
        ContentValues contentValues = new ContentValues();
        contentValues.put(EVENT,event);
        contentValues.put(TIME,time);
        contentValues.put(DATE,date);
        contentValues.put(MONTH,month);
        contentValues.put(YEAR,year);

        database.insert(EVENT_TABLE_NAME, null, contentValues);

    }

    public Cursor readEvents(String date, SQLiteDatabase database){
        String[] Projections = {EVENT, TIME, DATE, MONTH, YEAR};
        String Selection = DATE + "=?";
        String[] SelectionArgs = {date};

        return database.query(EVENT_TABLE_NAME, Projections, Selection, SelectionArgs, null, null, null);
    }

    public Cursor readEventsperMonths(String month, String year, SQLiteDatabase database){
        String[] Projections = {EVENT, TIME, DATE, MONTH, YEAR};
        String Selection = MONTH + "=? and " + YEAR +"=?";
        String[] SelectionArgs = {month, year};

        return database.query(EVENT_TABLE_NAME, Projections, Selection, SelectionArgs, null, null, null);
    }

    public void deleteEvent(String event, String date, String time, SQLiteDatabase database){
        String selection = EVENT +"=? and "+ DATE +"=? and "+ TIME + "=?";
        String[] selectionArgs = {event, date, time};
        database.delete(EVENT_TABLE_NAME, selection, selectionArgs);

    }
}

