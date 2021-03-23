package com.tecqza.handiwala;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

public class HomeFragment extends Fragment implements BaseSliderView.OnSliderClickListener,
        ViewPagerEx.OnPageChangeListener {

    Context context;
    ProcessDialog processDialog;
    SharedPrefs userSharedPrefs;
    View view;
    TextView view_all, total;
    private SliderLayout mDemoSlider;
    RecyclerView categories_recycler;
    RecyclerView product_recycler;
    CategoryAdapter categoryAdapter;
    BottomNavigationView bottomNavigationView;
    BadgeDrawable badgeDrawable;
    HashMap<String, String> url_maps = new HashMap<>();
    DatabaseHelper cart;
    EditText search_bar;

    public HomeFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.home_fragment, container, false);

        context = getActivity();
        this.processDialog = new ProcessDialog(context, "");
        this.userSharedPrefs = new SharedPrefs(context, "USER");
        LoadHomeFragment loadHomeFragment = new LoadHomeFragment();
        loadHomeFragment.execute();

        if (userSharedPrefs.getSharedPrefs("id") == null)
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new SettingsFragment()).addToBackStack(null).commit();

        total = view.findViewById(R.id.total);
        mDemoSlider = view.findViewById(R.id.slider);
        view_all = view.findViewById(R.id.view_all);
        search_bar = view.findViewById(R.id.search_bar);

        view_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
//
        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);

        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mDemoSlider.setCustomAnimation(new DescriptionAnimation());
        mDemoSlider.setDuration(4000);
        mDemoSlider.addOnPageChangeListener(this);
        mDemoSlider.stopCyclingWhenTouch(false);


        product_recycler = view.findViewById(R.id.product_recycler);
        categories_recycler = view.findViewById(R.id.category_grid);


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


        search_bar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch();
                    return true;
                }

                return false;
            }
        });

        return view;
    }

    private void performSearch() {

        String input = search_bar.getText().toString().toLowerCase();

        SearchProduct product = new SearchProduct();
        product.execute(input);


    }

    @Override
    public void onSliderClick(BaseSliderView slider) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    class LoadHomeFragment extends AsyncTask<String, Void, String> {

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

                    JSONArray categoryJsonArray = new JSONArray(jsonObject.getString("categories"));
                    ArrayList<CategoryModel> categories = CategoryModel.fromJson(categoryJsonArray);
                    categoryAdapter = new CategoryAdapter(context, categories);
                    categories_recycler.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                    categories_recycler.setAdapter(categoryAdapter);

///////////////////////////////////////////////////////////////////////////////////////////////////////

                    JSONArray sliderJsonArray = new JSONArray(jsonObject.getString("sliders"));

                    JSONObject json_data = new JSONObject();
                    for (int i = 0; i < sliderJsonArray.length(); i++) {
                        json_data = sliderJsonArray.getJSONObject(i);
                        String str = context.getResources().getString(R.string.file_base_url) + "slider/" + json_data.getString("image");
                        url_maps.put("", str);
                        for (String name : url_maps.keySet()) {
                            DefaultSliderView defaultSliderView = new DefaultSliderView(context);
                            // initialize a SliderLayout
                            defaultSliderView.setProgressBarVisible(true)
                                    .image(url_maps.get(name));
                            mDemoSlider.addSlider(defaultSliderView);

                        }
                    }

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
                }

            } catch (JSONException ignored) {
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String urls = context.getString(R.string.base_url).concat("home_fragment/");
            try {
                URL url = new URL(urls);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
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


    class SearchProduct extends AsyncTask<String, Void, String> {

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
                    if (productList.size() > 0)
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new SearchFragment(search_bar.getText().toString(), productList)).addToBackStack(null).commit();
                    else
                        Toast.makeText(context, "No Products Found", Toast.LENGTH_SHORT).show();


///////////////////////////////////////////////////////////////////////////////////////////////////////

                }

            } catch (JSONException ignored) {
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String urls = context.getString(R.string.base_url).concat("search_product/");
            try {
                URL url = new URL(urls);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_Data = URLEncoder.encode("query", "UTF-8") + "=" + URLEncoder.encode(params[0], "UTF-8");

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
