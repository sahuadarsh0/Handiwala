package com.tecqza.handiwala;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class OrderPlacedFragment extends Fragment {

    Context context;
    ProcessDialog processDialog;
    SharedPrefs userSharePrefs;
    View view;
    String order_id;
    TextView cart_total;
    TextView delivery_boy_name, delivery_boy_number;
    ConstraintLayout delivery;

    public OrderPlacedFragment(Context context, String order_id) {
        this.context = context;
        processDialog = new ProcessDialog(context, "Processing");
        userSharePrefs = new SharedPrefs(context, "USER");
        this.order_id = order_id;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.order_placed_fragment, container, false);
        cart_total = view.findViewById(R.id.cart_total);


        ImageView back = view.findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        delivery = view.findViewById(R.id.delivery);
        delivery_boy_name = view.findViewById(R.id.delivery_boy_name);
        delivery_boy_number = view.findViewById(R.id.delivery_boy_number);
        delivery.setVisibility(View.GONE);


        GetOrderDetails getOrderDetails = new GetOrderDetails();
        getOrderDetails.execute(order_id, userSharePrefs.getSharedPrefs("id"));
        return view;
    }

    class GetOrderDetails extends AsyncTask<String, Void, String> {

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
                    OrderModel order = new OrderModel(new JSONObject(jsonObject.getString("order")));

                    cart_total.setText("Total : Rs. " + order.total);


                    JSONArray itemsJsonArray = new JSONArray(jsonObject.getString("ordered_items"));
                    if (!jsonObject.getString("delivery_boy").equals("")) {
                        if (!jsonObject.getString("delivery_boy").equals("na")) {
                            JSONArray deliveryJsonArray = new JSONArray(jsonObject.getString("delivery_boy"));

                            if (deliveryJsonArray.length() > 0) {
                                delivery.setVisibility(View.VISIBLE);
                                JSONObject boy = deliveryJsonArray.getJSONObject(0);
                                delivery_boy_name.setText(boy.getString("name"));
                                delivery_boy_number.setText(boy.getString("mobile"));
                            }
                        }
                    } else
                        delivery.setVisibility(View.GONE);


                    ArrayList<OrderedItemModel> itemsList = OrderedItemModel.fromJson(itemsJsonArray);

                    RecyclerView recyclerView = view.findViewById(R.id.ordered_item_recyclerview);
                    recyclerView.clearOnScrollListeners();
                    recyclerView.setNestedScrollingEnabled(true);
                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    recyclerView.setAdapter(new OrderedItemAdapter(itemsList, context));


                } else {
                    Toast.makeText(context, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String urls = context.getString(R.string.base_url).concat("order_details/");
            try {
                URL url = new URL(urls);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_Data = URLEncoder.encode("order_id", "UTF-8") + "=" + URLEncoder.encode(params[0], "UTF-8") + "&" +
                        URLEncoder.encode("customer", "UTF-8") + "=" + URLEncoder.encode(params[1], "UTF-8");

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
