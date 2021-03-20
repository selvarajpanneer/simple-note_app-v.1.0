package com.example.simplenoteapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.core.os.HandlerCompat;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;


public class SqlHelper extends SQLiteOpenHelper {
    private static final String TAG = NotesContract.NotesEntry.TABLE_NAME;
    //If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 5;
    public static final String DATABASE_NAME = "notes";
    public static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + NotesContract.NotesEntry.TABLE_NAME + " (" +
            NotesContract.NotesEntry.SERIAL_NO + " INTEGER PRIMARY KEY, " +
            NotesContract.NotesEntry.TITLE + " VARCHAR(255) NOT NULL," +
            NotesContract.NotesEntry.CONTENT + " TEXT DEFAULT NULL," +
            NotesContract.NotesEntry.CREATION_TIME + " VARCHAR(255) DEFAULT NULL," +
            NotesContract.NotesEntry.DELETE_FLAG + " TINYINT DEFAULT 0," +
            NotesContract.NotesEntry.ALARM_TIME + " TIMESTAMP DEFAULT NULL," +
            NotesContract.NotesEntry.ALARM_ACTIVE + " TINYINT DEFAULT 0," +
            NotesContract.NotesEntry.ALARM_REPETITION + " VARCHAR(255) DEFAULT NULL," +
            NotesContract.NotesEntry.LAST_MODIFIED_TIME + " TIMESTAMP DEFAULT NULL," +
            NotesContract.NotesEntry.REMINDER_PRIORITY + " TINYINT   NOT NULL DEFAULT 0," +
            NotesContract.NotesEntry.CATEGORIES + " VARCHAR(255) DEFAULT NULL);";

    public SqlHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);
        Log.d(TAG, "onCreate:  created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + NotesContract.NotesEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    //create noteslist for recyclerview
    public ArrayList<Notes> list_Notes() {
        final String sql = "select * from " + NotesContract.NotesEntry.TABLE_NAME + " WHERE " + NotesContract.NotesEntry.DELETE_FLAG + "= 0" + "  ORDER BY " + NotesContract.NotesEntry.LAST_MODIFIED_TIME;
        final ArrayList<Notes> storeNotes = new ArrayList<>();
        final SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        Log.d(TAG, "listNotes:  list created");
        if (cursor.moveToFirst()) {
            do {
                int slno = cursor.getInt(0);
                String title = cursor.getString(1);
                String content = cursor.getString(2);
                String ctime = cursor.getString(3);
                Long ltime = cursor.getLong(8);
                storeNotes.add(new Notes(slno, title, content, ctime, ltime));
            } while (cursor.moveToNext());
        }
        cursor.close();
        Log.d(TAG, "list_Notes: " + storeNotes);
        return storeNotes;
    }


    //    create noteslist with alarmtime
    public ArrayList<Notes> alarmList_Notes() {
        final String sql = "select * from " + NotesContract.NotesEntry.TABLE_NAME + "  WHERE " + NotesContract.NotesEntry.ALARM_TIME + " IS NOT NULL AND " + NotesContract.NotesEntry.ALARM_ACTIVE + " = 1 " +
                " AND " + NotesContract.NotesEntry.DELETE_FLAG + " =0" + " ORDER BY " + NotesContract.NotesEntry.ALARM_TIME;
        final ArrayList<Notes> storeNotes = new ArrayList<>();
        final SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                int slno = cursor.getInt(0);
                String title = cursor.getString(1);
                String content = cursor.getString(2);
                String ctime = cursor.getString(3);
                String alarmtime = cursor.getString(5);
                Long ltime = cursor.getLong(7);
                storeNotes.add(new Notes(slno, title, content, ctime, alarmtime, ltime));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return storeNotes;
    }

    //add values to notes table with alarm time
    public boolean add_Notes(String title, String content, String creation_time, String Alarmtime) {
        final String alarmActive;
        if (Alarmtime != null) {
            alarmActive = "1";
        } else {
            alarmActive = "0";
        }
        ContentValues values = new ContentValues();
        values.put(NotesContract.NotesEntry.TITLE, title);
        values.put(NotesContract.NotesEntry.CONTENT, content);
        values.put(NotesContract.NotesEntry.CREATION_TIME, creation_time);
        values.put(NotesContract.NotesEntry.LAST_MODIFIED_TIME, System.currentTimeMillis());
        values.put(NotesContract.NotesEntry.ALARM_TIME, Alarmtime);
        values.put(NotesContract.NotesEntry.ALARM_ACTIVE, alarmActive);
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(NotesContract.NotesEntry.TABLE_NAME, null, values);
        db.close();
        Log.d(TAG, "addNotes: notes added");
        return true;
    }

    //update values in notes table
    public boolean update_Notes(int slno, String title, String content, String Alarmtime) {
        final String alarmActive;
        if (Alarmtime != null) {
            alarmActive = "1";
        } else {
            alarmActive = "0";
        }
        String sl = String.valueOf(slno);
        ContentValues cv = new ContentValues();
        cv.put(NotesContract.NotesEntry.TITLE, title);
        cv.put(NotesContract.NotesEntry.CONTENT, content);
        cv.put(NotesContract.NotesEntry.LAST_MODIFIED_TIME, System.currentTimeMillis());
        cv.put(NotesContract.NotesEntry.ALARM_TIME, Alarmtime);
        cv.put(NotesContract.NotesEntry.ALARM_ACTIVE, alarmActive);
        SQLiteDatabase db = this.getWritableDatabase();
        db.update(NotesContract.NotesEntry.TABLE_NAME, cv, "Serial_No = ?", new String[]{sl});
        db.close();
        return true;
    }

    //delete notes based on serial no just
    public Integer delete_Notes(int slno) {
        final SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + NotesContract.NotesEntry.TABLE_NAME + " SET " + NotesContract.NotesEntry.DELETE_FLAG + "= 1" + " WHERE Serial_No = " + slno);
        db.close();
        Log.d(TAG, "deleteNotes: notes deleted");
        return slno;
    }

    //cancel reminder Alarm
//    public void cancel_Alarm(int serialno) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        db.execSQL("UPDATE " + NotesContract.NotesEntry.TABLE_NAME + " SET " + NotesContract.NotesEntry.ALARM_TIME + "= NULL" + " WHERE Serial_No = " + serialno);
//        db.close();
//    }

    //deactivate alarm
    public boolean deactivate_Alarm(int serialno) {
        final SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + NotesContract.NotesEntry.TABLE_NAME + " SET " + NotesContract.NotesEntry.ALARM_ACTIVE + "= 0" + " WHERE Serial_No = " + serialno);
        db.close();
        return true;
    }
}

