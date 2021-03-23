package com.tecqza.handiwala;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class OrderedItemAdapter extends RecyclerView.Adapter<OrderedItemAdapter.OrderedItemViewHolder> {
    ArrayList<OrderedItemModel> items;
    Context context;
    public OrderedItemAdapter(ArrayList<OrderedItemModel> items, Context context){
        this.items=items;
        this.context=context;
    }

    @NonNull
    @Override
    public OrderedItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.ordered_item_list_1, parent, false);
        return new OrderedItemAdapter.OrderedItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderedItemViewHolder holder, int position) {
        holder.name.setText(items.get(position).product_name+" ("+items.get(position).variety_name+") ");
        holder.qty_amt.setText(items.get(position).qty+" X "+items.get(position).amount);
        holder.total_amount.setText("Rs. "+items.get(position).total_amount);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class OrderedItemViewHolder extends RecyclerView.ViewHolder {
        TextView name, qty_amt, total_amount;
        public OrderedItemViewHolder(@NonNull View itemView) {
            super(itemView);

            name=itemView.findViewById(R.id.name);
            qty_amt=itemView.findViewById(R.id.qty_amt);
            total_amount=itemView.findViewById(R.id.total_amount);
        }
    }
}

