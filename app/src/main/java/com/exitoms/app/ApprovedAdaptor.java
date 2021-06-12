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


public class ApprovedAdaptor extends RecyclerView.Adapter<ApprovedAdaptor.MyViewHolder> {
    private LayoutInflater inflater;
    Context ctx;
    private List<ApprovedModel> mApprovedModel = new ArrayList<>();
    ApprovedModel ApprovedModel;
    SharedPreferences sharedPreferences;
    Activity activity;
    FragmentTransaction ft;
    private Action action;

    public ApprovedAdaptor(Context ctx, Activity activity){
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

        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.approve_list, parent,false);
        return new ApprovedAdaptor.MyViewHolder(inflate);


    }


    @Override
    public void onBindViewHolder(ApprovedAdaptor.MyViewHolder holder, final int position) {
        ApprovedModel = mApprovedModel.get(position);
        holder.mID.setText(ApprovedModel.getmID());
        holder.Name.setText(ApprovedModel.getName());
        holder.TxnID.setText(ApprovedModel.getTxnID());
        holder.Cdate.setText(ApprovedModel.getCdate());

        if(ApprovedModel.getSatus().equalsIgnoreCase("Active"))
        {
            holder.Status.setBackground(ctx.getDrawable(R.drawable.green_box));
        }
        else if(ApprovedModel.getSatus().equalsIgnoreCase("Rejected"))
        {
            holder.Status.setBackground(ctx.getDrawable(R.drawable.danger));
        }else
        {
            holder.Status.setBackground(ctx.getDrawable(R.drawable.pending));
        }
        holder.Status.setText(ApprovedModel.getSatus());
        if(ApprovedModel.getShowDownLine().equalsIgnoreCase("true"))
            holder.Downline.setVisibility(View.VISIBLE);
        else
            holder.Downline.setVisibility(View.GONE);

        holder.Downline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mApprovedModel.get(position).getSatus().equalsIgnoreCase("Active"))
                {
                    Toast.makeText(ctx, "You cannot delete an active user!", Toast.LENGTH_SHORT).show();
                }
                else
                action.Delete(mApprovedModel.get(position).getID());

            }

        });
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
        LinearLayout Downline;

        public MyViewHolder(View itemView) {
            super(itemView);

            mID = (TextView) itemView.findViewById(R.id.apmm_mid);
            Name = (TextView) itemView.findViewById(R.id.apmm_name);
            TxnID = (TextView) itemView.findViewById(R.id.apmm_txnid);
            Cdate = (TextView) itemView.findViewById(R.id.apmm_cdate);
            Status = (TextView) itemView.findViewById(R.id.apmm_status  );
            Downline = itemView.findViewById(R.id.apmm_downline);
        }

    }


}