package com.tecqza.handiwala;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
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

import com.glide.slider.library.SliderLayout;
import com.glide.slider.library.animations.DescriptionAnimation;
import com.glide.slider.library.slidertypes.BaseSliderView;
import com.glide.slider.library.slidertypes.DefaultSliderView;
import com.glide.slider.library.tricks.ViewPagerEx;
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
import java.util.HashMap;

public class CategoryFragment extends Fragment {

    Context context;
    ProcessDialog processDialog;
    SharedPrefs userSharedPrefs;
    View view;
    TextView total;
    RecyclerView product_recycler;
    BottomNavigationView bottomNavigationView;
    BadgeDrawable badgeDrawable;
    DatabaseHelper cart;
    String cat_id;
    String cat_name;
    TextView categoryName;

    public CategoryFragment(String cat_id, String cat_name) {
        this.cat_id = cat_id;
        this.cat_name = cat_name;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.category_fragment, container, false);

        ImageView back = view.findViewById(R.id.back);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                remove();
                getActivity().onBackPressed();
            }
        });

        context = getActivity();
        this.processDialog = new ProcessDialog(context, "");
        this.userSharedPrefs = new SharedPrefs(context, "USER");
        product_recycler = view.findViewById(R.id.product_recycler);
        categoryName = view.findViewById(R.id.categoryName);


        LoadCategories loadCategories = new LoadCategories();
        loadCategories.execute(cat_id);


        categoryName.setText(cat_name);


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


        return view;
    }


    class LoadCategories extends AsyncTask<String, Void, String> {

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
            String urls = context.getString(R.string.base_url).concat("products/");
            try {
                URL url = new URL(urls);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_Data = URLEncoder.encode("category", "UTF-8") + "=" + URLEncoder.encode(params[0], "UTF-8");

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
