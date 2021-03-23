package com.tecqza.handiwala;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

public class UrlFragment extends androidx.fragment.app.Fragment {

    Context context;
    ProcessDialog processDialog;
    SharedPrefs userSharedPrefs;
    View view;
   TextView title,data;
    String url;

    public UrlFragment(Context context, String url) {
        this.context = context;
        this.url = url;
        processDialog = new ProcessDialog(context, "Loading..");
        this.userSharedPrefs = new SharedPrefs(context, "USER");
    }

    @androidx.annotation.Nullable
    @Override
    public android.view.View onCreateView(@androidx.annotation.NonNull LayoutInflater inflater, @androidx.annotation.Nullable ViewGroup container, @androidx.annotation.Nullable android.os.Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.url_fragment, container, false);


        ImageView back = view.findViewById(R.id.back);

        back.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
//                remove();
                getActivity().onBackPressed();
            }
        });



        title = view.findViewById(R.id.title);
        data = view.findViewById(R.id.data);



        LoadUrl loadUrl = new LoadUrl();
        loadUrl.execute();

        return view;

    }


    class LoadUrl extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            processDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            super.onPostExecute(s);
            processDialog.dismiss();
            try {
                JSONObject jsonObject = new JSONObject(s);
                title.setText(jsonObject.getString("title"));
                data.setText(jsonObject.getString("data"));

            } catch (JSONException e) {
                Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String urls = context.getString(R.string.file_base_url).concat("json/").concat(url);
            try {
                java.net.URL url = new java.net.URL(urls);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                java.io.InputStream inputStream = httpURLConnection.getInputStream();
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
