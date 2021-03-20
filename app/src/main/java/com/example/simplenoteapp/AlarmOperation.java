package com.example.simplenoteapp;

import android.content.Context;

import java.util.ArrayList;

public class AlarmOperation {
    Context context;
    private AsyncTaskHelp asyncTaskHelp;
    private ArrayList<Notes> al;
    SimpleNote simpleNote = new SimpleNote();

    public AlarmOperation(AsyncTaskHelp asyncTaskHelp, Context context) {
        this.asyncTaskHelp = asyncTaskHelp;
        this.context = context;
    }

    public void get_alarm() {
        new Thread() {
            @Override
            public void run() {
                al = new SqlHelper(context).alarmList_Notes();
                simpleNote.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        asyncTaskHelp.setAlarmList(al);
                    }
                });
            }
        }.start();
    }
}
