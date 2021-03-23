package com.tecqza.handiwala;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

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

public class SearchFragment extends Fragment {

    Context context;
    ProcessDialog processDialog;
    SharedPrefs userSharedPrefs;
    View view;
    TextView total;
    RecyclerView product_recycler;
    BottomNavigationView bottomNavigationView;
    BadgeDrawable badgeDrawable;
    DatabaseHelper cart;

    String query;
    TextView categoryName;
    ArrayList<ProductModel>  products;

    public SearchFragment(String query,ArrayList<ProductModel> products) {
        this.products=products;
        this.query = query;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.category_fragment, container, false);

        ImageView back = view.findViewById(R.id.back);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        context = getActivity();
        this.processDialog = new ProcessDialog(context, "");
        this.userSharedPrefs = new SharedPrefs(context, "USER");
        product_recycler = view.findViewById(R.id.product_recycler);
        categoryName = view.findViewById(R.id.categoryName);

        categoryName.setText(query);


        bottomNavigationView = getActivity().findViewById(R.id.bottomNavigationView);
        badgeDrawable = bottomNavigationView.getOrCreateBadge(R.id.cart);
        cart = new DatabaseHelper(context);
        int count = cart.totalCartItems();
        badgeDrawable.setNumber(count);
        if (count > 0) {
            badgeDrawable.setVisible(true);
        } else {
            badgeDrawable.setVisible(false);
        }

        total = view.findViewById(R.id.total);
        int total_amt = cart.cartTotalAmt();
        total.setText("Rs. " + total_amt);

        ProductAdapter productAdapter = new ProductAdapter(context, products, total, badgeDrawable);
        product_recycler.setLayoutManager(new LinearLayoutManager(context));
        product_recycler.setAdapter(productAdapter);


        return view;
    }




}
