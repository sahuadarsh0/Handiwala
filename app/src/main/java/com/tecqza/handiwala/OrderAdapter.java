package com.tecqza.handiwala;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    ArrayList<OrderModel> orders;
    Context context;
    public OrderAdapter(ArrayList<OrderModel> orders, Context context){
        this.orders=orders;
        this.context=context;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.order_item_list, parent, false);
        return new OrderAdapter.OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, final int position) {
        holder.order_id.setText(orders.get(position).id);
        holder.items.setText(orders.get(position).items);
        holder.order_date.setText((orders.get(position).cdt).substring(8,10)+"/"+(orders.get(position).cdt).substring(5,7)+"/"+(orders.get(position).cdt).substring(0,4));
        holder.order_time.setText((orders.get(position).cdt).substring(10));
        holder.status.setText(orders.get(position).status);
        holder.total_amount.setText("Rs. "+orders.get(position).total);

        holder.order_history_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatActivity activity=(AppCompatActivity) view.getContext();
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new OrderPlacedFragment(context, orders.get(position).id)).addToBackStack(null).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView order_id, items, order_date, order_time, status, total_amount;
        CardView order_history_card;
        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            order_history_card=itemView.findViewById(R.id.order_history_card);
            order_id=itemView.findViewById(R.id.order_id);
            items=itemView.findViewById(R.id.items);
            order_date=itemView.findViewById(R.id.order_date);
            order_time=itemView.findViewById(R.id.order_time);
            status=itemView.findViewById(R.id.status);
            total_amount=itemView.findViewById(R.id.total_amount);

        }
    }
}
