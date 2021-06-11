package com.exitoms.app;

import android.content.Context;
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

public class UserAdaptor extends RecyclerView.Adapter<UserAdaptor.MyViewHolder> {
    private LayoutInflater inflater;
    Context ctx;
    private List<UserModel> mUserModel = new ArrayList<>();
    UserModel UserModel;
    String vidID;
    SharedPreferences sharedPreferences;
    Context context;
    Fragment LocationFrag;
    FragmentTransaction ft;
    private Action action;

    public UserAdaptor(Context ctx){
        this.ctx = ctx;
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
        holder.Amount.setText(UserModel.getAmount());
        holder.Cdate.setText(UserModel.getCdate());
        holder.Status.setText(UserModel.getSatus());
        holder.Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
            Delete = itemView.findViewById(R.id.notify_layout);
        }

    }


}