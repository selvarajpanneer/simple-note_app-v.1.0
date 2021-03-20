package com.example.simplenoteapp;

import android.content.Context;
import android.os.Handler;

import com.example.simplenoteapp.AsyncTaskHelp;
import com.example.simplenoteapp.Notes;
import com.example.simplenoteapp.SimpleNote;
import com.example.simplenoteapp.SqlHelper;

import java.util.ArrayList;
import java.util.concurrent.Executor;

public class DeleteOperation {
//    private final Executor executor;
    Context context;
    Handler handler;
    AsyncTaskHelp asyncTaskHelp;
    int  s;
    ArrayList<Notes> notes;
    SimpleNote simpleNote = new SimpleNote();
    public DeleteOperation(AsyncTaskHelp asyncTaskHelp, Context context){
        this.asyncTaskHelp =asyncTaskHelp;
        this.context=context;
    }

    public void del(int slno){
        new Thread()
        {
            public void run() {
                notes= new SqlHelper(context).delete_Notes(slno);
                simpleNote.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        asyncTaskHelp.deletTask1(notes);
                    }
                });
            }

        }.start();
    }
    }


