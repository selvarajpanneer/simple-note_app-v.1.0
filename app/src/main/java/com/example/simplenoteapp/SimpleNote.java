package com.example.simplenoteapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.Executor;


public class SimpleNote extends AppCompatActivity implements AsyncTaskHelp {

    private static final String TAG = "main_activity";
    int LAUNCH_SECOND_ACTIVITY = 2;
    FloatingActionButton mFloatingActionButton;
    float dx, dy;

    RecyclerView recyclerView_View;
    RecyclerView.Adapter recyclerView_Adapter;
    RecyclerView.LayoutManager recyclerView_LayoutManager;
    ArrayList<Notes> loadNotesList;
    ArrayList<Notes> alarmList;
    boolean loaded = false;
    Executor executor;
    //    ExecutorService executorService= Executors.newFixedThreadPool(4);
//    Handler mainThreadHandler = HandlerCompat.createAsync(Looper.getMainLooper());
    //for alarm
    int alarm_result;
    ProgressBar progressBar;
    Context context;
    AsyncTaskHelp asyncTaskHelp;
    Delete_Task deleteTask;
    DeleteOperation deleteOperation;
    AlertDialog.Builder alertDialog;
    int position, serialnum;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        alertDialog = new AlertDialog.Builder(this);
        progressBar.setVisibility(View.VISIBLE);
        if (loaded == false) {
//            new ListOperation(executorService,context,mainThreadHandler).getthatlist();
            new ListOperation(this, SimpleNote.this).list_get();
            new AlarmOperation(this, SimpleNote.this).get_alarm();
        }
    }

    //guided to notes input page
    public void addItem() {
        Intent create_intent = new Intent(SimpleNote.this, InputSection.class);
        recyclerView_View.setAlpha(0);
        startActivityForResult(create_intent, LAUNCH_SECOND_ACTIVITY);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: " + loaded);
        if (loaded == true) {
            recyclerView_View.setAlpha(0);
            progressBar.setVisibility(View.VISIBLE);
//            new ListOperation(executorService,context,mainThreadHandler).getthatlist();
            new ListOperation(this, SimpleNote.this).list_get();
            new AlarmOperation(this, SimpleNote.this).get_alarm();
        }
    }

    //guided back from inputsection to main page
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LAUNCH_SECOND_ACTIVITY) {
            if (resultCode == Activity.RESULT_OK) {
                Log.d(TAG, "onActivityResult: getbacked");
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Log.d(TAG, "onActivityResult: " + "not getbacked");
            }
        }
    }

    @Override
    public void getList(ArrayList<Notes> notes) {
        progressBar.setVisibility(View.GONE);
        loaded = true;
        loadNotesList = notes;
        //set Recyclerview
        recyclerView_View = findViewById(R.id.recycle_view);
        recyclerView_View.setHasFixedSize(true);
        recyclerView_LayoutManager = new LinearLayoutManager(context);
        ((LinearLayoutManager) recyclerView_LayoutManager).setReverseLayout(true);
        recyclerView_Adapter = new Radapter(SimpleNote.this, notes);
        recyclerView_View.setLayoutManager(recyclerView_LayoutManager);
        recyclerView_View.setAdapter(recyclerView_Adapter);
        recyclerView_View.setAlpha(1);
        //swipe delete
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(RecyclerView recyclerView,
                                          RecyclerView.ViewHolder viewHolder,
                                          RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                        alertDialog
                                .setTitle("Delete ")
                                .setMessage("You are about to delete this note, please confirm")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Log.d(TAG, "onClick: " + which);
                                        position = viewHolder.getAdapterPosition();
                                        Notes delete_file = notes.get(position);
                                        Log.d(TAG, "onClick: position of arraylist " + position);
                                        serialnum = delete_file.getSlno();
                                        deletenotes(serialnum);
                                    }
                                })
                                .setNegativeButton("CANCEL", null)
                                .show();
                    }
                };
//        //using  fab to add new notes to list
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView_View);

        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        mFloatingActionButton.setOnClickListener(v -> addItem());
    }

    public void deletenotes(int serial_num) {
        progressBar.setVisibility(View.VISIBLE);
        new DeleteOperation(this, SimpleNote.this).del(serial_num);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void setAlarmList(ArrayList<Notes> notes) {
        if (notes.size() != 0) {
            for (int i = 0; i < notes.size(); i++) {
                AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(this, MyBroadCastReceiver.class);
                Notes note = notes.get(i);
                String alarm_time = note.getAlarmtime();
                if (alarm_time != null) {
                    int slno = note.getSlno();
                    String notification_Title = note.getTitle();
                    String notification_Content = note.getContent();
                    Log.d(TAG, "setAlarm: " + slno + " " + notification_Title + " " + notification_Content);
                    String[] s = alarm_time.split(":");
                    int hour = Integer.parseInt(s[0].trim());
                    int minute = Integer.parseInt(s[1].trim());
                    Calendar c = Calendar.getInstance();
                    c.set(Calendar.HOUR_OF_DAY, hour);
                    c.set(Calendar.MINUTE, minute);
                    c.set(Calendar.SECOND, 0);
                    intent.putExtra("notification_slno", slno);
                    intent.putExtra("notification_title", notification_Title);
                    intent.putExtra("notification_content", notification_Content);
                    int id = (int) System.currentTimeMillis();
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(this, id, intent, PendingIntent.FLAG_ONE_SHOT);
                    alarmMgr.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
                }
            }
        }
    }

    @Override
    public void deletTask1(ArrayList<Notes> al) {
        Log.d(TAG, "deletTask1: is get in");
        progressBar.setVisibility(View.GONE);
        recyclerView_View.removeViewAt(position);
        recyclerView_Adapter.notifyItemRemoved(position);
        recyclerView_Adapter.notifyItemRangeChanged(position, loadNotesList.size());
        getList(al);
    }
}


