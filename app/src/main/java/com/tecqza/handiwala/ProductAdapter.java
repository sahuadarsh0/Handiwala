package com.tecqza.handiwala;

import android.content.Context;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.badge.BadgeDrawable;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;

import java.util.ArrayList;

import okhttp3.internal.Internal;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    Context context;
    ArrayList<ProductModel> product_list;
    DatabaseHelper cart;
    TextView total;
    BadgeDrawable badgeDrawable;

    public ProductAdapter(Context context, ArrayList<ProductModel> products, TextView total, BadgeDrawable badgeDrawable) {
        this.context = context;
        this.product_list = products;
        this.total = total;
        this.badgeDrawable = badgeDrawable;
        cart = new DatabaseHelper(context);

    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_list_item, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {

        ProductModel product = product_list.get(position);
        holder.product.setVisibility(View.GONE);
        holder.customizable.setVisibility(View.GONE);
        holder.stock_out.setVisibility(View.GONE);

        int item_count = cart.totalQtyOfItems(Integer.parseInt(product_list.get(position).id));
        holder.item_count.setText(String.valueOf(item_count));


        int qty = cart.totalQtyOfItems(Integer.parseInt(product_list.get(position).id));


        if (qty > 0) {
            holder.item_add_linearLayout.setVisibility(View.GONE);
            holder.qty.setText(String.valueOf(qty));
            holder.item_add_sub_linearLayout.setVisibility(View.VISIBLE);
        } else {
            holder.item_add_linearLayout.setVisibility(View.VISIBLE);
            holder.qty.setText("1");
            holder.item_add_sub_linearLayout.setVisibility(View.GONE);
        }


        ArrayList<VarietiesModel> varieties_list = new ArrayList<>();

        try {
            if (product_list.get(position).varieties.equals("NA")) {
                holder.product.setVisibility(View.GONE);
            } else {
                holder.product.setVisibility(View.VISIBLE);
                JSONArray varietiesArray = new JSONArray(product_list.get(position).varieties);
                varieties_list = VarietiesModel.fromJson(varietiesArray);
            }

        } catch (Exception e) {

        }
        ArrayList<VarietiesModel> finalVarieties_list = varieties_list;
        String image_path = context.getString(R.string.file_base_url) + "products/" + product.image;
        Picasso.get().load(image_path).placeholder(R.drawable.handi_logo).into(holder.image);

        try {
            holder.name.setText(product.name);
            holder.qty.setText(varieties_list.get(0).name);
            holder.pamount.setText("Rs. "+varieties_list.get(0).mrp);
            holder.amount.setText("Rs. "+varieties_list.get(0).selling_price);
            holder.id.setText(product.id);
            holder.desc.setText(product.product_description);

            if (varieties_list.get(0).mrp.equals(varieties_list.get(0).selling_price)) {
                holder.pamount.setVisibility(View.INVISIBLE);
                holder.pamount.setText("");
            }

        } catch (Exception ignored) {

        }

        holder.pamount.setPaintFlags(holder.pamount.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

//        if (Integer.parseInt(product.cqty) > 0) {
//            holder.item_add_linearLayout.setVisibility(View.GONE);
//            holder.item_add_sub_linearLayout.setVisibility(View.VISIBLE);
//            holder.item_count.setText(product.cqty);
//        }

        if (finalVarieties_list.size() > 1) {
            holder.customizable.setVisibility(View.VISIBLE);
        } else {
            holder.customizable.setVisibility(View.GONE);
        }

        holder.item_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (finalVarieties_list.size() > 1) {

                    AppCompatActivity activity = (AppCompatActivity) v.getContext();

                    VarietyBottom varietyBottom = new VarietyBottom(context, holder.name.getText().toString(), holder.desc.getText().toString(), product_list.get(position), holder.item_count, total);
                    varietyBottom.show(activity.getSupportFragmentManager(),
                            "add_photo_dialog_fragment");

                } else {

                    cart.increaseQty(Integer.parseInt(product_list.get(position).id));
                    int item_count = cart.totalQtyOfItems(Integer.parseInt(product_list.get(position).id));
                    holder.item_count.setText(String.valueOf(item_count));
                    updateCart();

                }
                updateCart();
            }
        });

        holder.item_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cart.decreaseQty(Integer.parseInt(product_list.get(position).id));

                int item_count = cart.totalQtyOfItems(Integer.parseInt(product_list.get(position).id));
                if (item_count > 0) {
                    holder.item_count.setText(String.valueOf(item_count));
                    holder.item_add_linearLayout.setVisibility(View.GONE);
                    holder.item_add_sub_linearLayout.setVisibility(View.VISIBLE);
                } else {
                    holder.item_add_linearLayout.setVisibility(View.VISIBLE);
                    holder.item_add_sub_linearLayout.setVisibility(View.GONE);
                }

                updateCart();
            }
        });
//
        holder.item_add_linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.item_add_linearLayout.setVisibility(View.GONE);
                holder.item_add_sub_linearLayout.setVisibility(View.VISIBLE);

                cart.insertData(
                        Integer.parseInt(product_list.get(position).id),
                        Integer.parseInt(finalVarieties_list.get(0).id),
                        1,
                        Integer.parseInt(finalVarieties_list.get(0).selling_price)
                );

                if (finalVarieties_list.size() > 1) {

                    AppCompatActivity activity = (AppCompatActivity) v.getContext();
                    VarietyBottom varietyBottom = new VarietyBottom(context, holder.name.getText().toString(), holder.desc.getText().toString(), product_list.get(position), holder.item_count, total);
                    varietyBottom.show(activity.getSupportFragmentManager(),
                            "add_photo_dialog_fragment");

                } else {

                    int item_count = cart.totalQtyOfItems(Integer.parseInt(product_list.get(position).id));
                    holder.item_count.setText(String.valueOf(item_count));
                }
                updateCart();
            }
        });


        if (product_list.get(position).in_stock.equals("0")) {
            holder.stock_out.setVisibility(View.VISIBLE);
            holder.item_minus.setClickable(false);
            holder.item_plus.setClickable(false);
            holder.item_add_linearLayout.setClickable(false);
        } else {
            holder.stock_out.setVisibility(View.GONE);
        }


    }

    @Override
    public int getItemCount() {
        return product_list.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {


        TextView name, qty, pamount, amount, id, desc, item_count, customizable;
        ImageView image, item_minus, item_plus;
        LinearLayout item_add_linearLayout, item_add_sub_linearLayout;
        ConstraintLayout product, stock_out;
        CardView cardView;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image);
            product = itemView.findViewById(R.id.product);


            cardView = itemView.findViewById(R.id.cardView);
            stock_out = itemView.findViewById(R.id.stock_out);
            name = itemView.findViewById(R.id.name);
            qty = itemView.findViewById(R.id.qty);
            pamount = itemView.findViewById(R.id.pamount);
            amount = itemView.findViewById(R.id.amount);
            id = itemView.findViewById(R.id.id);
            desc = itemView.findViewById(R.id.desc);
            image = itemView.findViewById(R.id.image);
            item_add_linearLayout = itemView.findViewById(R.id.item_add_linearLayout);
            item_add_sub_linearLayout = itemView.findViewById(R.id.item_add_sub_linearLayout);
            item_count = itemView.findViewById(R.id.item_count);

            item_plus = itemView.findViewById(R.id.item_plus);
            item_minus = itemView.findViewById(R.id.item_minus);
            customizable = itemView.findViewById(R.id.customizable);
        }
    }

    public void updateList(ArrayList<ProductModel> newList) {
        product_list = new ArrayList<>();
        product_list.addAll(newList);
        notifyDataSetChanged();
    }

    void updateCart() {
        int count = cart.totalCartItems();
        total.setText(String.valueOf(cart.cartTotalAmt()));
        badgeDrawable.setNumber(count);
        if (count > 0) {
            badgeDrawable.setVisible(true);
        } else {
            badgeDrawable.setVisible(false);
        }
    }
}
