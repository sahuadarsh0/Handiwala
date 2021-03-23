package com.tecqza.handiwala;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.razorpay.Checkout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class CartFragment extends Fragment {

    Context context;
    ProcessDialog processDialog;
    SharedPrefs userSharedPrefs;
    View view;
    TextView total;
    RecyclerView product_recycler;
    BottomNavigationView bottomNavigationView;
    BadgeDrawable badgeDrawable;
    DatabaseHelper cart;
    private ConstraintLayout check_out;

    public CartFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.cart_fragment, container, false);


        context = getActivity();
        this.processDialog = new ProcessDialog(context, "");
        this.userSharedPrefs = new SharedPrefs(context, "USER");

        product_recycler = view.findViewById(R.id.product_recycler);

        check_out = view.findViewById(R.id.check_out);
        ImageView back = view.findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        bottomNavigationView = getActivity().findViewById(R.id.bottomNavigationView);
        badgeDrawable = bottomNavigationView.getOrCreateBadge(R.id.cart);
        cart = new DatabaseHelper(context);
        int count = cart.totalCartItems();
        badgeDrawable.setNumber(count);
        if (count > 0) {
            badgeDrawable.setVisible(true);
            check_out.setVisibility(View.VISIBLE);
        } else {
            badgeDrawable.setVisible(false);
            check_out.setVisibility(View.GONE);
        }
        total = view.findViewById(R.id.total);
        int total_amt = cart.cartTotalAmt();
        total.setText(String.valueOf(total_amt));


        check_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cart.totalCartItems() > 0) {
                    Intent intent = new Intent(context, CheckoutActivity.class);
                    startActivity(intent);
                } else
                    Toast.makeText(context, "No Items in the list", Toast.LENGTH_SHORT).show();
            }
        });


        LoadCart loadCart = new LoadCart();
        loadCart.execute(cart.getCartProductsId());

        return view;
    }


    class LoadCart extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            processDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            processDialog.dismiss();

            try {
                JSONObject jsonObject = new JSONObject(s);
                if (jsonObject.getString("status").equals("true")) {

///////////////////////////////////////////////////////////////////////////////////////////////////////

                    JSONArray products = new JSONArray(jsonObject.getString("products"));
                    ArrayList<ProductModel> productList = ProductModel.fromJson(products);
                    ProductAdapter productAdapter = new ProductAdapter(context, productList, total, badgeDrawable);
                    product_recycler.setLayoutManager(new LinearLayoutManager(context));
                    product_recycler.setAdapter(productAdapter);

///////////////////////////////////////////////////////////////////////////////////////////////////////

                }

            } catch (JSONException ignored) {
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String urls = context.getString(R.string.base_url).concat("cart_products/");
            try {
                URL url = new URL(urls);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_Data = URLEncoder.encode("products_id", "UTF-8") + "=" + URLEncoder.encode(params[0], "UTF-8");

                bufferedWriter.write(post_Data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                String result = "", line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }
                return result;
            } catch (Exception e) {
                return e.toString();
            }
        }
    }


}
