package com.example.simplenoteapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.Executor;

public class SimpleNote extends AppCompatActivity {
    ProgressBar progressBar;
    private static final String TAG = "main_activity";
    int LAUNCH_SECOND_ACTIVITY = 2;
    FloatingActionButton mFloatingActionButton;
    float dx, dy;

    RecyclerView recyclerView_View;
    RecyclerView.Adapter recyclerView_Adapter;
    RecyclerView.LayoutManager recyclerView_LayoutManager;
    ArrayList<Notes> loadNotesList;
    ArrayList<Notes> alarmList;

    //for alarm
    int alarm_result;

    Executor executor;
    SqlHelper dbHelper = new SqlHelper(this, executor);
    SqlViewModel sqlViewModel = new SqlViewModel(dbHelper);

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = (ProgressBar) findViewById(R.id.progressBar_cyclic);
        progressBar.setVisibility(View.VISIBLE);
        //get data from db and converted it into arraylist using non-ui thread(operation inside SqlHelper class)
        // and then pass it to  ui thread to load  the list in recyclerview
        while (true) {
            loadNotesList = sqlViewModel.listNotes();
            if (loadNotesList != null) {
                setRecyclerView_view(loadNotesList);
                break;
            }
        }
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
                        //Delete notes by swipe and notify the RecyclerView
//                        int position = viewHolder.getAdapterPosition();
//                        Notes delete_file = loadNotesList.get(position);
//                        int delete = delete_file.getSlno();
//                        dbHelper.deleteNotes(delete);
//                        recyclerView_View.removeViewAt(position);
//                        recyclerView_Adapter.notifyItemRemoved(position);
//                        recyclerView_Adapter.notifyItemRangeChanged(position, loadNotesList.size());
                    }

                    Paint paint;

                    public Paint getPaintObject() {

                        if (paint == null)
                            paint = new Paint();
                        return paint;
                    }

                    public static final float ALPHA_FULL = 1.0f;

                    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                            View itemView = viewHolder.itemView;
                            Paint p = getPaintObject();
                            if (dX > 0) {
                                p.setARGB(255, 255, 0, 0);
                                c.drawRect((float) itemView.getLeft(), (float) itemView.getTop(), dX,
                                        (float) itemView.getBottom(), p);
                            }
                            final float alpha = ALPHA_FULL - Math.abs(dX) / (float) viewHolder.itemView.getWidth();
                            viewHolder.itemView.setAlpha(alpha);
                            viewHolder.itemView.setTranslationX(dX);

                        } else {
                            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                        }
                    }
                };
        //using  fab to add new notes to list
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView_View);

        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    //set the recyclerview
    private void setRecyclerView_view(ArrayList<Notes> list) {
        progressBar.setVisibility(View.GONE);
        recyclerView_View = findViewById(R.id.recycle_view);
        recyclerView_View.setHasFixedSize(true);
        recyclerView_LayoutManager = new LinearLayoutManager(this);
        ((LinearLayoutManager) recyclerView_LayoutManager).setReverseLayout(true);
        recyclerView_Adapter = new Radapter(SimpleNote.this, list);
        recyclerView_View.setLayoutManager(recyclerView_LayoutManager);
        recyclerView_View.setAdapter(recyclerView_Adapter);
    }

    //set alarm based on alarmtime stored in db
//    @RequiresApi(api = Build.VERSION_CODES.M)
//    public void setAlarm() {//start series of alarms one by one
//        for (int i = 0; i < alarmList.size(); i++) {
//            AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//            Intent intent = new Intent(this, MyBroadCastReceiver.class);
//            Notes notes = alarmList.get(i);
//            String alarm_time = notes.getAlarmtime();
//            if (alarm_time != null) {
//                int slno = notes.getSlno();
//                String notification_Title = notes.getTitle();
//                String notification_Content = notes.getContent();
//                Log.d(TAG, "setAlarm: " + slno + " " + notification_Title + " " + notification_Content);
//                String[] s = alarm_time.split(":");
//                int hour = Integer.parseInt(s[0].trim());
//                int minute = Integer.parseInt(s[1].trim());
//                Calendar c = Calendar.getInstance();
//                c.set(Calendar.HOUR_OF_DAY, hour);
//                c.set(Calendar.MINUTE, minute);
//                c.set(Calendar.SECOND, 0);
//                intent.putExtra("notification_slno", slno);
//                intent.putExtra("notification_title", notification_Title);
//                intent.putExtra("notification_content", notification_Content);
//                int id = (int) System.currentTimeMillis();
//                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, id, intent, PendingIntent.FLAG_ONE_SHOT);
//                alarmMgr.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
//            }
//        }
//    }

    //guided to notes inputsection page
    public void addItem() {
        Intent create_intent = new Intent(SimpleNote.this, InputSection.class);
        startActivityForResult(create_intent, LAUNCH_SECOND_ACTIVITY);
    }

    //guided back from inputsection to main page
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        recyclerView_Adapter.notifyDataSetChanged();
        if (requestCode == LAUNCH_SECOND_ACTIVITY) {
            if (resultCode == Activity.RESULT_OK) {
                Log.d(TAG, "onActivityResult: getbacked");
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Log.d(TAG, "onActivityResult: " + "not getbacked");
            }
        }
    }
}


