package com.exitoms.app;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


public class WhAdaptor extends RecyclerView.Adapter<WhAdaptor.MyViewHolder> {
    private LayoutInflater inflater;
    Context ctx;
    private List<ApprovedModel> mApprovedModel = new ArrayList<>();
    ApprovedModel ApprovedModel;
    SharedPreferences sharedPreferences;
    Activity activity;
    FragmentTransaction ft;
    private Action action;

    public WhAdaptor(Context ctx, Activity activity){
        this.ctx = ctx;
        this.activity = activity;
        this.action= ((Action)ctx);
    }

    public void renewItems(List<ApprovedModel> list) {
        this.mApprovedModel = list;
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.wallet_list, parent,false);
        return new WhAdaptor.MyViewHolder(inflate);


    }


    @Override
    public void onBindViewHolder(WhAdaptor.MyViewHolder holder, final int position) {
        ApprovedModel = mApprovedModel.get(position);
        holder.mID.setText(ApprovedModel.getmID());
        holder.Name.setText(ApprovedModel.getName());
        holder.TxnID.setText(ApprovedModel.getTxnID());
        holder.Cdate.setText(ApprovedModel.getCdate());

        if(ApprovedModel.getSatus().equalsIgnoreCase("Credit"))
        {
            holder.Status.setBackground(ctx.getDrawable(R.drawable.green_box));
        }
        else if(ApprovedModel.getSatus().equalsIgnoreCase("Debit"))
        {
            holder.Status.setBackground(ctx.getDrawable(R.drawable.danger));
        }else
        {
            holder.Status.setBackground(ctx.getDrawable(R.drawable.pending));
        }
        holder.Status.setText(ApprovedModel.getSatus());

    }

    @Override
    public int getItemCount() {
        return mApprovedModel.size();
    }

    public void updateList(List<ApprovedModel> list){
        mApprovedModel = list;
        notifyDataSetChanged();
    }

    public interface Action{
        void Delete(String uID);
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView mID,Name,TxnID,Status,Cdate;

        public MyViewHolder(View itemView) {
            super(itemView);

            mID = (TextView) itemView.findViewById(R.id.wh_mid);
            Name = (TextView) itemView.findViewById(R.id.wh_name);
            TxnID = (TextView) itemView.findViewById(R.id.wh_txnid);
            Cdate = (TextView) itemView.findViewById(R.id.wh_cdate);
            Status = (TextView) itemView.findViewById(R.id.wh_status  );
        }

    }


}