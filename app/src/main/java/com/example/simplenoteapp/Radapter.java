package com.example.simplenoteapp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;


public class Radapter extends RecyclerView.Adapter<Radapter.ViewHolder> {
    private static final String TAG = "ADAPTER";
    private ArrayList<Notes> mNoteList;
    private Context rContext;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView1, textView2, textView3, textView4;
        private View itemLayout;


        public ViewHolder(View v) {
            super(v);
            textView1 = v.findViewById(R.id.title_view);
            textView2 = v.findViewById(R.id.content_view);
            textView3 = v.findViewById(R.id.last_modified_time_view);
            textView4 = v.findViewById(R.id.creation_time_view);
            itemLayout = v.findViewById(R.id.single_item);
        }
    }

    public Radapter(Context context, ArrayList<Notes> list) {
        rContext = context;
        mNoteList = list;
    }

    @NonNull
    @Override
    public Radapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_single_item_layout, parent, false);
        ViewHolder vh = new ViewHolder(v1);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull Radapter.ViewHolder holder, int position) {
        Notes nl = mNoteList.get(position);
        int slno = nl.getSlno();
        String title = nl.getTitle();
        String content = nl.getContent();
        String c_time = nl.getCreation_time();
        Long l_time = nl.getLast_modified_time();
        //String t = checkTimePeriod(l_time);
        holder.textView1.setText(title);
        holder.textView2.setText(content);
       // holder.textView3.setText(t);
        holder.textView4.setText(c_time);

        holder.itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent edit_intent = new Intent(rContext, InputSection.class);
                edit_intent.putExtra("edit_slno", slno);
                edit_intent.putExtra("edit_title", title);
                edit_intent.putExtra("edit_content", content);
                rContext.startActivity(edit_intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        if (mNoteList != null) {
            Log.d(TAG, "getItemCount: mNoteList not null");
            return mNoteList.size();
        } else
            return 0;
    }
}