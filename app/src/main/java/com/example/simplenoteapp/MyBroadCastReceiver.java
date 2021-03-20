package com.example.simplenoteapp;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import androidx.core.app.NotificationCompat;



public class MyBroadCastReceiver extends BroadcastReceiver {

    private static final String TAG = "Broadcast receiver";
    private SqlHelper dpHelper;
    Context broadcast_context;
    int serial_no;
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intent1 = intent;
        broadcast_context=context;
        serial_no = intent1.getIntExtra("notification_slno", 0);
        String title = intent1.getStringExtra("notification_title");
        String content = intent1.getStringExtra("notification_content");
        NotificationService notificationHelper = new NotificationService(context);
        NotificationCompat.Builder nb = notificationHelper.getChannelNotification(title, content);
        Intent intent2 = new Intent(context, InputSection.class);
        intent2.putExtra("edit_slno", serial_no);
        intent2.putExtra("edit_title", title);
        intent2.putExtra("edit_content", content);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                intent2, PendingIntent.FLAG_UPDATE_CURRENT);
        nb.setContentIntent(contentIntent);
        notificationHelper.getManager().notify(1, nb.build());
        //db operation using asynctask
        new PerformDbOp().execute(new String[]{});
    }

    public SqlHelper getDpHelper(Context context) {
        if (dpHelper == null) {
            dpHelper = new SqlHelper(context);
        }
        return dpHelper;
    }
private class PerformDbOp extends AsyncTask<String, Void, Boolean> {

    @Override
    protected Boolean doInBackground(String... strings) {
        Boolean flag =getDpHelper(broadcast_context).deactivate_Alarm(serial_no);
        return flag;
    }

    @Override
    protected void onPostExecute(Boolean flag) {
        Boolean flag_success=flag;
        if(flag_success==true){
            Log.d(TAG, "onPostExecute: "+flag_success);
        }
        else {
            Log.d(TAG, "onPostExecute: "+flag_success);
        }
    }
}
}
