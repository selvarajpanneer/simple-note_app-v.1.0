package com.example.simplenoteapp;

import android.content.Context;

import com.example.simplenoteapp.Notes;

import java.util.ArrayList;

public interface AsyncTaskHelp {
    public void getList(ArrayList<Notes> notes);
    public void setAlarmList(ArrayList<Notes> notes);
//    public void deletTask1(int slno);

}
