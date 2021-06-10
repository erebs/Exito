package com.exitoms.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class NotifyAdaptor extends RecyclerView.Adapter<NotifyAdaptor.MyViewHolder> {
    private LayoutInflater inflater;
    Context ctx;
    private List<NotifyModel> mNotifyModel = new ArrayList<>();
    NotifyModel NotifyModel;
    String vidID;
    SharedPreferences sharedPreferences;
    Context context;
    Fragment LocationFrag;
    FragmentTransaction ft;

    public NotifyAdaptor(Context ctx){
        this.ctx = ctx;

    }

    public void renewItems(List<NotifyModel> list) {
        this.mNotifyModel = list;
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_list, parent,false);
        return new NotifyAdaptor.MyViewHolder(inflate);


    }


    @Override
    public void onBindViewHolder(NotifyAdaptor.MyViewHolder holder, final int position) {
        NotifyModel = mNotifyModel.get(position);
        holder.Title.setText(NotifyModel.getTitle());
        holder.Date.setText(NotifyModel.getDate());
        holder.Content.setText(NotifyModel.getContent());
        holder.Layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }

        });
    }

    @Override
    public int getItemCount() {
        return mNotifyModel.size();
    }

    public void updateList(List<NotifyModel> list){
        mNotifyModel = list;
        notifyDataSetChanged();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView ID,Date,Title,Content;
        LinearLayout Layout;

        public MyViewHolder(View itemView) {
            super(itemView);

            Title = (TextView) itemView.findViewById(R.id.notify_title);
            Date = (TextView) itemView.findViewById(R.id.notify_date);
            Content = (TextView) itemView.findViewById(R.id.notify_content);
            Layout = itemView.findViewById(R.id.notify_layout);
        }

    }


}