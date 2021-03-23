package com.tecqza.handiwala;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

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

public class CheckoutActivity extends AppCompatActivity implements PaymentResultListener {


    Dialog payment_mode_dialog;
    DatabaseHelper cart;
    SharedPrefs userSharedPrefs;
    ProcessDialog processDialog;
    Context context;
    TextView change;
    String payment;
    EditText address;
    float total_amt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);


        context = this;
        cart = new DatabaseHelper(this);
        this.processDialog = new ProcessDialog(this, "");
        this.userSharedPrefs = new SharedPrefs(this, "USER");

        TextView order_now = findViewById(R.id.order_now);
        address = findViewById(R.id.address);
        EditText landmark = findViewById(R.id.landmark);
        ImageView back = findViewById(R.id.back);


        address.setText(userSharedPrefs.getSharedPrefs("address"));
        landmark.setText(userSharedPrefs.getSharedPrefs("landmark"));
        payment_mode_dialog = new Dialog(this);
        payment_mode_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        payment_mode_dialog.setContentView(R.layout.payment_mode_dialog);
        Window window = payment_mode_dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.setBackgroundDrawableResource(R.color.semi_transparent);
        payment_mode_dialog.setCancelable(true);

        TextView cod = payment_mode_dialog.findViewById(R.id.cod);
        TextView pay_now = payment_mode_dialog.findViewById(R.id.pay_now);
        change = findViewById(R.id.change);

        order_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                payment_mode_dialog.show();
            }
        });

        total_amt = cart.cartTotalAmt();
        cod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                payment_mode_dialog.dismiss();
                SubmitData data = new SubmitData();
                data.execute(cart.getCartProducts(), userSharedPrefs.getSharedPrefs("id"), address.getText().toString(), String.valueOf(total_amt), "na");
            }
        });


        pay_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                payment_mode_dialog.dismiss();
                startPayment((total_amt * 100) + "");
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        Checkout.preload(this);


        disableEditText(landmark);
        disableEditText(address);

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (change.getText().toString().equals("Save")) {

                    change.setText("Change");
                    disableEditText(landmark);
                    disableEditText(address);

                } else if (change.getText().toString().equals("Change")) {
                    change.setText("Save");

                    enableEditText(address);
                    address.setSelection(address.getText().length());
                    address.requestFocus();
                    enableEditText(landmark);
                    enableEditText(address);

                }
            }
        });


    }


    class SubmitData extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            processDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                if (jsonObject.getString("status").equals("missing")) {
                    builder.setTitle("Missing Values");
                    StringBuffer errors = new StringBuffer();
                    JSONArray errorsJsonArray = new JSONArray(jsonObject.getString("errors"));
                    JSONObject errorJsonObject;
                    for (int i = 0; i < errorsJsonArray.length(); i++) {
                        errorJsonObject = errorsJsonArray.getJSONObject(i);
                        errors.append(errorJsonObject.getString("error")).append("\n");
                    }
                    builder.setMessage(errors);
                    builder.show();


                } else if (jsonObject.getString("status").equals("true")) {

                    String order_id = jsonObject.getString("order_id");
                    userSharedPrefs.setSharedPrefs("order_id", order_id);
                    cart.deleteAll();

                    Intent intent = new Intent(CheckoutActivity.this, MainActivity.class);
                    intent.putExtra("open", "ORDER_PLACED");
                    intent.putExtra("order_id", order_id);

                    Dialog Otp_dialog;
                    Otp_dialog = new Dialog(context);
                    Otp_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    Otp_dialog.setContentView(R.layout.my_dialog);
                    Window window = Otp_dialog.getWindow();
                    window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                    window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                    window.setBackgroundDrawableResource(R.color.semi_transparent);
                    Otp_dialog.setCancelable(true);
                    TextView title, message;
                    title = Otp_dialog.findViewById(R.id.title);
                    message = Otp_dialog.findViewById(R.id.message);

                    title.setText("Success");
                    message.setText("Order Placed Successfully");

                    Otp_dialog.show();
                    Handler handler = new Handler();
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            Otp_dialog.dismiss();
                            startActivity(intent);
                            finish();
                        }
                    };

                    handler.postDelayed(runnable, 2000);
                } else {
                    builder.setTitle("Error");
                    builder.setMessage(jsonObject.getString("msg"));
                }
            } catch (JSONException e) {
                Toast.makeText(CheckoutActivity.this, s, Toast.LENGTH_SHORT).show();
            }
            processDialog.dismiss();
        }

        @Override
        protected String doInBackground(String... params) {
            String urls = getString(R.string.base_url).concat("place_order/");
            try {
                URL url = new URL(urls);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_Data = URLEncoder.encode("products", "UTF-8") + "=" + URLEncoder.encode(params[0], "UTF-8") + "&" +
                        URLEncoder.encode("customer", "UTF-8") + "=" + URLEncoder.encode(params[1], "UTF-8") + "&" +
                        URLEncoder.encode("address", "UTF-8") + "=" + URLEncoder.encode(params[2], "UTF-8") + "&" +
                        URLEncoder.encode("amount", "UTF-8") + "=" + URLEncoder.encode(params[3], "UTF-8") + "&" +
                        URLEncoder.encode("payment_id", "UTF-8") + "=" + URLEncoder.encode(params[4], "UTF-8");

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

    private void disableEditText(EditText editText) {
        editText.setFocusable(false);
        editText.setEnabled(false);
        editText.setCursorVisible(false);
        editText.setFocusableInTouchMode(false);
        editText.setBackgroundColor(Color.TRANSPARENT);
    }

    private void enableEditText(EditText editText) {
        editText.setFocusable(true);
        editText.setEnabled(true);
        editText.setCursorVisible(true);
        editText.setFocusableInTouchMode(true);
        editText.setBackgroundColor(getResources().getColor(R.color.light_grey));
    }


    public void startPayment(String amount) {
        /**
         * Instantiate Checkout
         */
        Checkout checkout = new Checkout();

        /**
         * Set your logo here
         */
        checkout.setImage(R.drawable.handi_logo);

        /**
         * Reference to current activity
         */
        AppCompatActivity activity = this;

        /**
         * Pass your payment options to the Razorpay Checkout as a JSONObject
         */
        try {
            JSONObject options = new JSONObject();

            /**
             * Merchant Name
             * eg: ACME Corp || HasGeek etc.
             */
            options.put("name", "Handiwala");

            /**
             * Description can be anything
             * eg: Reference No. #123123 - This order number is passed by you for your internal reference. This is not the `razorpay_order_id`.
             *     Invoice Payment
             *     etc.
             */


            options.put("currency", "INR");

            /**
             * Amount is always passed in currency subunits
             * Eg: "500" = INR 5.00
             */
            options.put("amount", amount);

            checkout.open(activity, options);
        } catch (Exception e) {
            Log.e("Error", "Error in starting Razorpay Checkout", e);
        }
    }

    @Override
    public void onPaymentSuccess(String s) {
        payment = s;
        SubmitData data = new SubmitData();
        data.execute(cart.getCartProducts(), userSharedPrefs.getSharedPrefs("id"), address.getText().toString(), String.valueOf(total_amt), payment);
    }

    @Override
    public void onPaymentError(int i, String s) {

    }


}