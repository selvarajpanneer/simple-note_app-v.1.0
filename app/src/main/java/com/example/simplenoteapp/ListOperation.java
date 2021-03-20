package com.example.simplenoteapp;


import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;


public class ListOperation {
//    private final ExecutorService executorService;
    Context context;
    Handler handler;
    private static final String TAG = "LIST OPERATION";
    AsyncTaskHelp asyncTaskHelp;
    ArrayList<Notes> al;
    SimpleNote simpleNote = new SimpleNote();

    public ListOperation(AsyncTaskHelp asyncTaskHelp, Context context) {
        this.asyncTaskHelp = asyncTaskHelp;
        this.context = context;
    }
//    public ListOperation(ExecutorService executorService, Context context, Handler handler){
//        this.executorService=executorService;
//        this.context=context;
//        this.handler=handler;
//    }
    //not working need to check
//    SqlHelper sqlHelper = new SqlHelper(context);
//public void getthatlist(){
//        executorService.execute(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                Log.d(TAG, "run: non-ui thread");
//                al = sqlHelper.list_Notes();
//
//                   handler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            asyncTaskHelp.getList(al);
//                        }
//                    });
//            }
//        });
//}
    public void list_get() {
        new Thread() {
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "run: non-ui thread");
                al = new SqlHelper(context).list_Notes();
                simpleNote.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "run: runOnui thread and list " + al);
                        asyncTaskHelp.getList(al);
                    }
                });
            }
        }.start();
    }
}
