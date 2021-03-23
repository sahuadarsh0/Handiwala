package com.tecqza.handiwala;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.json.JSONArray;

import java.util.ArrayList;

public class VarietyBottom extends BottomSheetDialogFragment {


    Context context;
    String product_name;
    String product_desc;
    ProductModel product;
    ArrayList<VarietiesModel> varieties_list;
    DatabaseHelper cart;
    TextView item_count;
    TextView total;
    SharedPrefs userSharePrefs;
    TextView tv_item_count;
    TextView tv_total;
    private int selected_pos = 0;

    public VarietyBottom(Context context, String product_name, String product_desc, ProductModel product, TextView item_count, TextView total) {
        this.context = context;
        this.product_name = product_name;
        this.product_desc = product_desc;
        this.product = product;
        this.item_count = item_count;
        this.total = total;
        cart = new DatabaseHelper(context);
        userSharePrefs = new SharedPrefs(context, "USER");
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.bottom_variety_fragment, container, false);

        ListView listView = view.findViewById(R.id.varieties);

        LinearLayout linearLayout = view.findViewById(R.id.linearLayout);
        ImageView dismiss = view.findViewById(R.id.dismiss);

        TextView tv_product_name = view.findViewById(R.id.product_name);
        TextView tv_product_desc = view.findViewById(R.id.product_desc);
        tv_total = view.findViewById(R.id.total);

        ImageView item_minus = view.findViewById(R.id.item_minus);
        ImageView item_plus = view.findViewById(R.id.item_plus);
        tv_item_count = view.findViewById(R.id.item_count);

        setTotalOfProduct();
        item_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Product product = getCartProduct();
                if (product.counter > 0) {

                    int qty = Integer.parseInt(product.qty);

                    cart.increaseQty(Integer.parseInt(product.id));

                    item_count.setText(String.valueOf(qty));
                    tv_item_count.setText(String.valueOf(qty));
                    setTotalOfProduct();
                }
            }
        });
        item_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Product product = getCartProduct();
                if (product.counter > 0) {

                    int qty = Integer.parseInt(product.qty);
                    if (--qty < 1) {
                        cart.deleteWhereProduct(Integer.parseInt(product.id));
                    } else {
                        cart.decreaseQty(Integer.parseInt(product.id));
                        setTotalOfProduct();
                    }
                    item_count.setText(String.valueOf(qty));
                    tv_item_count.setText(String.valueOf(qty));
                }
            }
        });

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        tv_product_name.setText(product_name);
        tv_product_desc.setText(product_desc);

        try {
            JSONArray varietiesArray = new JSONArray(product.varieties);
            varieties_list = VarietiesModel.fromJson(varietiesArray);
            selected_pos = varietyPosition();

            Product product = getCartProduct();
            if (product.counter > 0) {

                item_count.setText(Integer.parseInt(product.qty));
                tv_item_count.setText(Integer.parseInt(product.qty));

            } else {
                insertData(0);
                setTotalOfProduct();
            }


        } catch (Exception e) {

        }

        MyAdapter arrayAdapter = new MyAdapter(varieties_list);
        listView.setAdapter(arrayAdapter);

        getCartProduct();

        return view;
    }


    private class MyAdapter extends BaseAdapter {

        private ArrayList<VarietiesModel> arrayList;

        private MyAdapter(ArrayList<VarietiesModel> arrayList) {
            this.arrayList = arrayList;
        }

        @Override
        public int getCount() {
            return arrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return arrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View view, ViewGroup parent) {
            ViewHolder holder;
            if (view == null) {
                view = View.inflate(getContext(), R.layout.item_variety, null);
                holder = new ViewHolder();

                holder.item_variety_layout = view.findViewById(R.id.item_variety_layout);
                holder.product_price = view.findViewById(R.id.product_price);
                holder.radioButton = view.findViewById(R.id.qty);
                holder.vid = view.findViewById(R.id.id);

                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }


            holder.product_price.setText(arrayList.get(position).selling_price);
            holder.radioButton.setText(arrayList.get(position).name);
            holder.vid.setText(arrayList.get(position).id);

            holder.radioButton.setChecked(position == selected_pos);
            holder.radioButton.setTag(position);
            holder.item_variety_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selected_pos = (Integer) v.findViewById(R.id.qty).getTag();
                    notifyDataSetChanged();

                    if(cart.isVarietyExist(Integer.parseInt(arrayList.get(position).id))){

                    }else{
                        cart.deleteWhereProduct(Integer.parseInt(product.id));
                        insertData(position);
                    }
                    setTotalOfProduct();
                }
            });

            holder.radioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selected_pos = (Integer) v.findViewById(R.id.qty).getTag();
                    notifyDataSetChanged();


                    if(cart.isVarietyExist(Integer.parseInt(arrayList.get(position).id))){

                    }else{
                        cart.deleteWhereProduct(Integer.parseInt(product.id));
                        insertData(position);
                    }
                    setTotalOfProduct();
                }
            });


            return view;

        }

        class ViewHolder {
            ConstraintLayout item_variety_layout;
            RadioButton radioButton;
            TextView product_price, vid;

        }


    }

    public void insertData(int position) {
        cart.insertData(
                Integer.parseInt(product.id),
                Integer.parseInt(varieties_list.get(position).id),
                1,
                Integer.parseInt(varieties_list.get(position).selling_price)
        );
    }

    public int varietyPosition() {

        Product product1 = getCartProduct();
        int pos = 0;
        for (VarietiesModel product : varieties_list) {
            if (product.id.equals(product1.vid)) {
                return pos;
            } else {
                pos++;
            }
        }

        return 0;
    }

    public void setTotalOfProduct() {

        Product product1 = getCartProduct();

        try {
            int qty = Integer.parseInt(product1.qty);
            float amount = Float.parseFloat(product1.selling_price);
            float total = qty * amount;


           item_count.setText(String.valueOf(qty));
            tv_item_count.setText(String.valueOf(qty));

            tv_total.setText(String.valueOf(total));

        } catch (Exception e) {
            tv_item_count.setText("0");
            tv_total.setText("0.0");
        }
    }

    public Product getCartProduct() {
        int productCounter = 0;
        String id = null, qty = null, vid = null, selling_price = null;
        Cursor res = cart.getAllData();

        if (res.getCount() > 0) {
            while (res.moveToNext()) {
                productCounter++;
                id = res.getString(1);
                vid = res.getString(2);
                qty = res.getString(3);
                selling_price = res.getString(4);

            }
        }
        Product product = new Product(res, productCounter, id, qty, vid, selling_price);
        return product;
    }

    class Product {
        Cursor cursor;
        int counter;
        String id, qty, vid, selling_price;

        Product(Cursor cursor, int counter, String id, String qty, String vid, String selling_price) {
            this.cursor = cursor;
            this.counter = counter;
            this.id = id;
            this.qty = qty;
            this.vid = vid;
            this.selling_price = selling_price;
        }


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        total.setText(String.valueOf(cart.cartTotalAmt()));
    }
}