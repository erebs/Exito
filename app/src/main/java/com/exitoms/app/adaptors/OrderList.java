package com.exitoms.app.adaptors;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.exitoms.app.OrderDetailsActivity;
import com.exitoms.app.R;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import com.exitoms.app.models.OrderDetails;

import java.util.ArrayList;
import java.util.List;

public class OrderList extends RecyclerView.Adapter<OrderList.MyViewHolder> {
    private LayoutInflater inflater;
    Context ctx;
    private List<OrderDetails> mOrderDetails = new ArrayList<>();
    OrderDetails OrderDetails;
    String vidID;
    SharedPreferences sharedPreferences;
    Context context;
    Fragment LocationFrag;
    FragmentTransaction ft;

    public OrderList(Context ctx){
        this.ctx = ctx;

    }

    public void renewItems(List<OrderDetails> list) {
        this.mOrderDetails = list;
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_list, parent,false);
        return new OrderList.MyViewHolder(inflate);


    }


    @Override
    public void onBindViewHolder(OrderList.MyViewHolder holder, final int position) {
        OrderDetails = mOrderDetails.get(position);
        holder.ID.setText("#WAS"+OrderDetails.getID()+" order by "+OrderDetails.getCustomer());
        holder.Amount.setText(OrderDetails.getAmount());
        holder.Date.setText("Ordered on "+OrderDetails.getDate());
        holder.Card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(v.getContext(), OrderDetailsActivity.class);
                i.putExtra("orderID",mOrderDetails.get(position).getID());
                v.getContext().startActivity(i);

            }

        });
    }

    @Override
    public int getItemCount() {
        return mOrderDetails.size();
    }

    public void updateList(List<OrderDetails> list){
        mOrderDetails = list;
        notifyDataSetChanged();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView ID,Date,Amount;
        LinearLayout Card;

        public MyViewHolder(View itemView) {
            super(itemView);

            ID = (TextView) itemView.findViewById(R.id.list_order_id);
            Amount = (TextView) itemView.findViewById(R.id.list_order_amount);
            Date = (TextView) itemView.findViewById(R.id.list_order_date);
            Card = itemView.findViewById(R.id.list_layout);
        }

    }


}