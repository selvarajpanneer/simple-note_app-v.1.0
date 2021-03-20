package com.example.simplenoteapp;

import android.content.Context;
import android.util.Log;

public class AddOrUpdateOperation{
    private static final String TAG ="ADD OR UPDATE OPERATION" ;
    Context context;
    AsyncTaskAddOrUpdate asyncTaskAddOrUpdate;
    Boolean flag;
    InputSection inputSection=new InputSection();
    public AddOrUpdateOperation(AsyncTaskAddOrUpdate asyncTaskAddOrUpdate,Context context){
        this.asyncTaskAddOrUpdate =asyncTaskAddOrUpdate;
        this.context=context;
    }
public void update(int slno,String title,String content,String alarmtime){
    Log.d(TAG, "update: get inside the update");
    new Thread()
    {
        public void run() {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "run: non-ui thread");
            flag =new SqlHelper(context).update_Notes(slno, title, content, alarmtime);
            inputSection.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "run: Pass the value to ui thread");
                  asyncTaskAddOrUpdate.asyncAddOrUpdate(flag);
                }
            });
        }
    }.start();
}
    public void add(String title,String content,String creationtime,String alarmtime){
        Log.d(TAG, "add: get inside the add");
        new Thread()
        {
            public void run() {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "run: non-ui thread");
                flag =new SqlHelper(context).add_Notes(title, content, creationtime, alarmtime);
                inputSection.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        asyncTaskAddOrUpdate.asyncAddOrUpdate(flag);
                    }
                });
            }
        }.start();
    }
}
