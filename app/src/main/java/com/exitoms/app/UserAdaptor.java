package com.exitoms.app;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.mateware.snacky.Snacky;
import lucifer.org.snackbartest.Icon;
import lucifer.org.snackbartest.MySnack;


public class UserAdaptor extends RecyclerView.Adapter<UserAdaptor.MyViewHolder> {
    private LayoutInflater inflater;
    Context ctx;
    private List<UserModel> mUserModel = new ArrayList<>();
    UserModel UserModel;
    SharedPreferences sharedPreferences;
    Activity activity;
    FragmentTransaction ft;
    private Action action;

    public UserAdaptor(Context ctx,Activity activity){
        this.ctx = ctx;
        this.activity = activity;
        this.action= ((Action)ctx);
    }

    public void renewItems(List<UserModel> list) {
        this.mUserModel = list;
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list, parent,false);
        return new UserAdaptor.MyViewHolder(inflate);


    }


    @Override
    public void onBindViewHolder(UserAdaptor.MyViewHolder holder, final int position) {
        UserModel = mUserModel.get(position);
        holder.ID.setText(UserModel.getID());
        holder.Name.setText(UserModel.getName());
        holder.Cname.setText(UserModel.getCname());
        holder.Cnumber.setText(UserModel.getCnumber());
        holder.Amount.setText("₹"+UserModel.getAmount());
        holder.Cdate.setText(UserModel.getCdate());

        if(UserModel.getSatus().equalsIgnoreCase("Active"))
        {
            holder.Status.setBackground(ctx.getDrawable(R.drawable.green_box));
        }
        else if(UserModel.getSatus().equalsIgnoreCase("Rejected"))
        {
            holder.Status.setBackground(ctx.getDrawable(R.drawable.danger));
        }else
        {
            holder.Status.setBackground(ctx.getDrawable(R.drawable.pending));
        }

        holder.Status.setText(UserModel.getSatus());
        holder.Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mUserModel.get(position).getSatus().equalsIgnoreCase("Active"))
                {
                    Toast.makeText(ctx, "You cannot delete an active user!", Toast.LENGTH_SHORT).show();
                }
                else
                action.Delete(mUserModel.get(position).getuID());

            }

        });
    }

    @Override
    public int getItemCount() {
        return mUserModel.size();
    }

    public void updateList(List<UserModel> list){
        mUserModel = list;
        notifyDataSetChanged();
    }

    public interface Action{
        void Delete(String uID);
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView ID,Name,Cname,Cnumber,Amount,Status,Cdate;
        LinearLayout Delete;

        public MyViewHolder(View itemView) {
            super(itemView);

            ID = (TextView) itemView.findViewById(R.id.su_actcode);
            Name = (TextView) itemView.findViewById(R.id.su_name);
            Cname = (TextView) itemView.findViewById(R.id.su_cname);
            Cnumber = (TextView) itemView.findViewById(R.id.su_cnumber);
            Amount = (TextView) itemView.findViewById(R.id.su_amount);
            Cdate = (TextView) itemView.findViewById(R.id.su_createdat);
            Status = (TextView) itemView.findViewById(R.id.su_status);
            Delete = itemView.findViewById(R.id.su_delete);
        }

    }


}