package com.example.simplenoteapp;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TimePicker;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;
import java.util.concurrent.Executor;


public class InputSection extends AppCompatActivity {

    private static final String TAG = "INPUT SECTION";
    EditText mtitle;
    EditText mcontent;
    String title, content;
    String Alarmtime = null;
    int slno;
    Executor executor;
    //sql instantiation to handle db
    SqlHelper inputdbHelper = new SqlHelper(this, executor);
//    SqlHelper inputdbHelper = new SqlHelper(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_storage_layout);

        mtitle = findViewById(R.id.title_of_note);
        mcontent = findViewById(R.id.content_of_note);

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
                Alarmtime = selectedHour + ":" + selectedMinute;
            }
        }, hour, minute, false);
        mTimePicker.show();
    }

    public void backToMainPage() {
        //get the input from views
        title = mtitle.getText().toString().trim();
        content = mcontent.getText().toString().trim();
        if (!title.isEmpty()) {
            if (!content.isEmpty()) {
                String creation_time = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
                if (slno != 0) {//update alarmtime in existing rows of notes
                    //  need to write code for perform db operation using non ui thread
                } else {//create new row in notes with alarmtime
                    //   need to write code for perform db operation using non ui thread
                }
            } else {
                Log.d(TAG, "INPUTSECTION notes: content is empty");
            }
        } else {
            Log.d(TAG, "INPUTSECTICON notes: title is empty");
        }
        Intent return_intent = new Intent();
        setResult(Activity.RESULT_OK, return_intent);
        finish();
    }
}


