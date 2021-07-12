package com.exitoms.app;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


public class RequestAdaptor extends RecyclerView.Adapter<RequestAdaptor.MyViewHolder> {
    private LayoutInflater inflater;
    Context ctx;
    private List<RequestModel> mRequestModel = new ArrayList<>();
    RequestModel RequestModel;
    SharedPreferences sharedPreferences;
    Activity activity;
    FragmentTransaction ft;
    private Action action;

    public RequestAdaptor(Context ctx, Activity activity){
        this.ctx = ctx;
        this.activity = activity;
        this.action= ((Action)ctx);
    }

    public void renewItems(List<RequestModel> list) {
        this.mRequestModel = list;
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.wrequest_list, parent,false);
        return new RequestAdaptor.MyViewHolder(inflate);


    }


    @Override
    public void onBindViewHolder(RequestAdaptor.MyViewHolder holder, final int position) {
        RequestModel = mRequestModel.get(position);
        holder.ID.setText("WRN"+RequestModel.getID());
        holder.Wamt.setText("₹"+RequestModel.getWAmt());
        holder.Ramt.setText("₹"+RequestModel.getRAmt());
        holder.Tds.setText("₹"+RequestModel.getTds());
        holder.Cmt.setText(RequestModel.getCmt());
        holder.Rdate.setText(RequestModel.getRdate());

        if(RequestModel.getSatus().equalsIgnoreCase("Accepted"))
        {
            holder.Status.setBackground(ctx.getDrawable(R.drawable.green_box));
        }
        else if(RequestModel.getSatus().equalsIgnoreCase("Rejected"))
        {
            holder.Status.setBackground(ctx.getDrawable(R.drawable.danger));
        }else
        {
            holder.Status.setBackground(ctx.getDrawable(R.drawable.pending));
        }

        holder.Status.setText(RequestModel.getSatus());

    }

    @Override
    public int getItemCount() {
        return mRequestModel.size();
    }

    public void updateList(List<RequestModel> list){
        mRequestModel = list;
        notifyDataSetChanged();
    }

    public interface Action{
        void Delete(String uID);
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView ID,Wamt,Ramt,Tds,Cmt,Status,Rdate;
        LinearLayout Delete;

        public MyViewHolder(View itemView) {
            super(itemView);

            ID = (TextView) itemView.findViewById(R.id.wr_id);
            Wamt = (TextView) itemView.findViewById(R.id.wr_wamt);
            Ramt = (TextView) itemView.findViewById(R.id.wr_ramt);
            Tds = (TextView) itemView.findViewById(R.id.wr_tds);
            Cmt = (TextView) itemView.findViewById(R.id.wr_cmt);
            Rdate = (TextView) itemView.findViewById(R.id.wr_rdate);
            Status = (TextView) itemView.findViewById(R.id.wr_status);
        }

    }


}