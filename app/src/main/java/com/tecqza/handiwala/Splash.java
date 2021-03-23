package com.tecqza.handiwala;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class Splash extends AppCompatActivity {

    Runnable runnable;
    Handler handler1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        handler1 = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(Splash.this);
                alertBuilder.setTitle("Connection");
                alertBuilder.setMessage("Internet connection not available");
                alertBuilder.show();
            }
        };
        handler1.postDelayed(runnable, 4500);


        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkRequest.Builder builder = new NetworkRequest.Builder();

        connectivityManager.registerNetworkCallback(
                builder.build(),
                new ConnectivityManager.NetworkCallback() {
                    @Override
                    public void onAvailable(@NonNull Network network) {
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                handler1.removeCallbacks(runnable);

                                startActivity(new Intent(Splash.this, LoginActivity.class));
                                finish();
                            }
                        }, 3000);
                    }
                }
        );

    }
}