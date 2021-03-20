package com.example.simplenoteapp;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;


public class InputSection extends AppCompatActivity implements AsyncTaskAddOrUpdate {

    private static final String TAG = "INPUT SECTION";
    EditText mtitle;
    EditText mcontent;
    String title, content;
    String alarmtime = null;
    int slno;
    boolean flag = false;
    Intent return_intent;
    ProgressBar progressBar;
    Context context;
    SqlHelper sqlHelper = new SqlHelper(this);
    AsyncTaskHelp asyncTaskHelp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_storage_layout);
        progressBar = (ProgressBar) findViewById(R.id.progressBar_inputsection);
        mtitle = findViewById(R.id.title_of_note);
        mcontent = findViewById(R.id.content_of_note);
        context = getApplicationContext();
        //asyncTaskHelp = (AsyncTaskHelp) new ListOperation(this);
        Intent intent = getIntent();
        slno = intent.getIntExtra("edit_slno", 0);
        title = intent.getStringExtra("edit_title");
        content = intent.getStringExtra("edit_content");
        mtitle.setText(title);
        mcontent.setText(content);
    }

    //menu for save icon
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_menu, menu);
        return true;
    }

    @Override
    public void asyncAddOrUpdate(Boolean b) {
        Log.d(TAG, "asyncAddOrUpdate: result"+b);
        progressBar.setVisibility(View.GONE);
        return_intent = new Intent();
        setResult(Activity.RESULT_OK, return_intent);
        finish();
    }

    //icons for operations like save, backspace, reminder
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item1:
                reminderTimePicker();
                return true;
            case R.id.item2:
                backToMainPage();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void reminderTimePicker() {
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(InputSection.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                //Take the alarm time
                alarmtime = selectedHour + ":" + selectedMinute;
            }
        }, hour, minute, false);
        mTimePicker.show();
    }


    public void backToMainPage() {

        //get the input from views
        title = mtitle.getText().toString().trim();
        content = mcontent.getText().toString().trim();
        Log.d(TAG, "backToMainPage: title & content " + title + " " + content);
        String creation_time = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
        if(TextUtils.isEmpty(mtitle.getText())&&TextUtils.isEmpty(mcontent.getText())) {
            return_intent = new Intent();
            setResult(Activity.RESULT_OK, return_intent);
            finish();
        } else if (!TextUtils.isEmpty(mtitle.getText())) {
                progressBar.setVisibility(View.VISIBLE);
                if (slno != 0) {
                    // updating existing rows with alarm time
                    new AddOrUpdateOperation(this, context).update(slno, title, content, alarmtime);
                    Log.d(TAG, "backToMainPage: UPDATE STARTS HERE");
                } else {
                    //create new row in notes with alarmtime
//
                    new AddOrUpdateOperation(this, context).add(title, content, creation_time, alarmtime);
                }
            } else {
                mtitle.setError("title is mandatory");
            }

    }
}

