
package com.tecqza.handiwala;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

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
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pub.devrel.easypermissions.EasyPermissions;

public class LoginActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    EditText mobile, otp;
    TextView label, click_here;
    Button submit, verify, resend;
    SharedPrefs userSharedPrefs;
    Context context;
    ProcessDialog processDialog;
    String customer_data;
    private static final int REQUEST_CODE = 121;
    private static final int REQ_USER_CONSENT = 200;

    SmsBroadcastReceiver smsBroadcastReceiver;
    SharedPrefs token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        context = this;
        userSharedPrefs = new SharedPrefs(context, "USER");
        processDialog = new ProcessDialog(context, "Processing");
        token = new SharedPrefs(getApplicationContext(), "TOKEN");

        startSmsUserConsent();
        String[] perms = {
                Manifest.permission.READ_SMS,
                Manifest.permission.RECEIVE_SMS
        };
        if (!EasyPermissions.hasPermissions(context, perms)) {
            EasyPermissions.requestPermissions(this, "All permissions are required in oder to run this application", REQUEST_CODE, perms);
        }


        if (userSharedPrefs.getSharedPrefs("id") != null) {
            Intent i = new Intent(context, MainActivity.class);
            i.putExtra("open", "ORDER_TYPE");
            startActivity(i);
            finish();
        }


        mobile = findViewById(R.id.mobile);
        otp = findViewById(R.id.otp);
        label = findViewById(R.id.label);
        submit = findViewById(R.id.submit);
        resend = findViewById(R.id.resend);
        verify = findViewById(R.id.verify);
        click_here = findViewById(R.id.click_here);

        click_here.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, WebPage.class);
                startActivity(i);
            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GenerateOtp generateOtp = new GenerateOtp();
                generateOtp.execute(mobile.getText().toString());

                Runnable runnable;
                Handler handler1;
                handler1 = new Handler();
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        resend.setVisibility(View.VISIBLE);
                    }
                };
                handler1.postDelayed(runnable, 60000);
            }
        });


        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GenerateOtp generateOtp = new GenerateOtp();
                generateOtp.execute(mobile.getText().toString());
                resend.setVisibility(View.GONE);

                Runnable runnable;
                Handler handler1;

                handler1 = new Handler();
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        resend.setVisibility(View.VISIBLE);
                    }
                };
                handler1.postDelayed(runnable, 60000);
            }
        });


        verify.setOnClickListener(v -> {
            if (userSharedPrefs.getSharedPrefs("otp").equals(otp.getText().toString())) {
                userSharedPrefs.setSharedPrefs("mobile", mobile.getText().toString());
                if (customer_data.equals("NA")) {
                    Intent i = new Intent(context, MainActivity.class);
                    i.putExtra("open", "PROFILE");
                    startActivity(i);
                    finish();


                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(customer_data);
                        userSharedPrefs.setSharedPrefs("id", jsonObject.getString("id"));
                        userSharedPrefs.setSharedPrefs("name", jsonObject.getString("name"));
                        userSharedPrefs.setSharedPrefs("image", jsonObject.getString("image"));
                        userSharedPrefs.setSharedPrefs("email", jsonObject.getString("email"));
                        userSharedPrefs.setSharedPrefs("mobile", jsonObject.getString("mobile"));
                        userSharedPrefs.setSharedPrefs("address", jsonObject.getString("address"));
                        userSharedPrefs.setSharedPrefs("landmark", jsonObject.getString("landmark"));
                        userSharedPrefs.setSharedPrefs("state_id", jsonObject.getString("state_id"));
                        userSharedPrefs.setSharedPrefs("city_id", jsonObject.getString("city_id"));
                        processDialog.dismiss();


                        RegisterToken register = new RegisterToken();
                        register.execute(userSharedPrefs.getSharedPrefs("id"),token.getSharedPrefs("token"));

                        Intent i = new Intent(context, MainActivity.class);
                        i.putExtra("open", "ORDER_TYPE");
                        startActivity(i);
                        finish();

                    } catch (JSONException e) {

                    }
                }
            } else {
                Toast.makeText(context, "Invalid otp", Toast.LENGTH_SHORT).show();
            }
        });


    }


    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_USER_CONSENT) {
            if ((resultCode == RESULT_OK) && (data != null)) {

                String message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE);
                getOtpFromMessage(message);
            }
        }
    }

    private void getOtpFromMessage(String message) {
        // This will match any 6 digit number in the message
        Pattern pattern = Pattern.compile("(|^)\\d{4}");
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            otp.setText(matcher.group(0));
        }
    }

    class GenerateOtp extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            processDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
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
                message.setText("OTP sent Successfully");


                JSONObject jsonObject = new JSONObject(s);
                if (jsonObject.getString("status").equals("missing")) {
                    title.setText("Missing Values");
                    StringBuffer errors = new StringBuffer();
                    JSONArray errorsJsonArray = new JSONArray(jsonObject.getString("errors"));
                    JSONObject errorJsonObject;
                    for (int i = 0; i < errorsJsonArray.length(); i++) {
                        errorJsonObject = errorsJsonArray.getJSONObject(i);
                        errors.append(errorJsonObject.getString("error")).append("\n");
                    }
                    message.setText(errors);
                    Otp_dialog.show();
                } else if (jsonObject.getString("status").equals("otpsent")) {
                    Log.d("asa", "OTP: "+jsonObject.getString("otp"));
                    userSharedPrefs.setSharedPrefs("otp", jsonObject.getString("otp"));
                    customer_data = jsonObject.getString("data");
                    title.setText("Success");
                    message.setText(jsonObject.getString("msg"));
                    optMode();
                    Otp_dialog.show();
                    Handler handler = new Handler();

                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            Otp_dialog.dismiss();
                        }
                    };


                    handler.postDelayed(runnable, 2000);


                } else {
                }
            } catch (JSONException e) {

            }
            processDialog.dismiss();
        }

        @Override
        protected String doInBackground(String... params) {
            String urls = context.getString(R.string.base_url).concat("generate_and_send_otp_to_customer/");
            try {
                URL url = new URL(urls);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_Data = URLEncoder.encode("mobile", "UTF-8") + "=" + URLEncoder.encode(params[0], "UTF-8");

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


    public void optMode() {
        mobile.setVisibility(View.GONE);
        submit.setVisibility(View.GONE);
        label.setText("Enter OTP");

        otp.setVisibility(View.VISIBLE);
        verify.setVisibility(View.VISIBLE);
    }


    private void startSmsUserConsent() {
        SmsRetrieverClient client = SmsRetriever.getClient(this);

        client.startSmsUserConsent(null).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void registerBroadcastReceiver() {
        smsBroadcastReceiver = new SmsBroadcastReceiver();
        smsBroadcastReceiver.smsBroadcastReceiverListener =
                new SmsBroadcastReceiver.SmsBroadcastReceiverListener() {
                    @Override
                    public void onSuccess(Intent intent) {
                        startActivityForResult(intent, REQ_USER_CONSENT);
                    }

                    @Override
                    public void onFailure() {
                    }
                };
        IntentFilter intentFilter = new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION);
        registerReceiver(smsBroadcastReceiver, intentFilter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerBroadcastReceiver();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(smsBroadcastReceiver);
    }


    class RegisterToken extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            processDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                final JSONObject jsonObject = new JSONObject(s);
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
                }
            } catch (JSONException e) {
                Log.d("asa", "onPostExecute: " + e.toString());
            }
            processDialog.dismiss();
        }

        @Override
        protected String doInBackground(String... params) {
            String urls = context.getString(R.string.base_url).concat("register_token/");
            try {
                URL url = new URL(urls);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_Data = URLEncoder.encode("customer_id", "UTF-8") + "=" + URLEncoder.encode(params[0], "UTF-8") + "&" +
                        URLEncoder.encode("token", "UTF-8") + "=" + URLEncoder.encode(params[1], "UTF-8") ;

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
