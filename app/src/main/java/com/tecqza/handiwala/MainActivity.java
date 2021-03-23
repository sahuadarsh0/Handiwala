package com.tecqza.handiwala;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.razorpay.PaymentResultListener;

public class MainActivity extends AppCompatActivity implements PaymentResultListener {

    BottomNavigationView bottomNavigationView;
    Context context;
    Bundle bundle;
    DatabaseHelper cart;
    SharedPrefs userSharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        context = this;
        bundle = getIntent().getExtras();
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        userSharedPrefs = new SharedPrefs(context, "USER");


        if (userSharedPrefs.getSharedPrefs("id") == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new SettingsFragment()).commit();
        }

        cart = new DatabaseHelper(context);
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new HomeFragment()).commit();


        String fragment = getIntent().getStringExtra("fragment");
        String orderId = getIntent().getStringExtra("extra");


        if (fragment != null && fragment.equals("ORDER_DETAIL")) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new OrderPlacedFragment(context, orderId)).addToBackStack(null).commit();
        }

        String open = bundle.getString("open", null);
        if (bundle != null && open != null) {

            switch (bundle.getString("open", null)) {

                case "ORDER_PLACED":
                    bottomNavigationView.setSelectedItemId(R.id.order);
                    String order_id = bundle.getString("order_id");
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new OrderPlacedFragment(context, order_id)).addToBackStack(null).commit();
                    break;

                default:
                    bottomNavigationView.setSelectedItemId(R.id.home);
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new HomeFragment()).addToBackStack(null).commit();
                    break;
            }
        }


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                switch (item.getItemId()) {
                    case R.id.home:
                        selectedFragment = new HomeFragment();

                        break;
                    case R.id.setting:
                        selectedFragment = new SettingsFragment();
                        break;

                    case R.id.order:
                        selectedFragment = new OrderFragment(context);
                        break;

                    case R.id.cart:
                        if (cart.totalCartItems() != 0)
                            selectedFragment = new CartFragment();
                        else
                            Toast.makeText(context, "No Item in Cart", Toast.LENGTH_SHORT).show();
                        break;

                }
                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, selectedFragment).addToBackStack(null).commit();
                }

                return true;
            }
        });


    }


    @Override
    public void onPaymentSuccess(String s) {
//        RecordPayment recordPayment = new RecordPayment();
//        recordPayment.execute(s,order_id);
    }


    @Override
    public void onPaymentError(int i, String s) {

        Log.d("asa", "onPaymentError: " + s);
    }

}
