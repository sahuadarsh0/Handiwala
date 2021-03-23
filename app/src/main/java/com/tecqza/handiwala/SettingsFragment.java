package com.tecqza.handiwala;

import android.Manifest;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import net.gotev.uploadservice.MultipartUploadRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Random;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

public class SettingsFragment extends Fragment {

    private Context context;
    private ProcessDialog processDialog;
    private SharedPrefs userSharedPrefs;
    private TextView edit1, mobile, name1;
    private BottomNavigationView bottomNavigationView;

    private EditText name, email, address;

    private TextView t_n_c,  privacy_policy, refund_cancellation,   about_us, support, rate_us;
    private TextView log_out;

    private Dialog logout_dialog;

    private ImageView profile;
    private Uri imageUri;


    public SettingsFragment() {
    }

    public static int REQUEST_CODE = 200;

    private static final int PERMISSION_REQUEST = 101;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings_fragment, container, false);


        context = getActivity();
        this.processDialog = new ProcessDialog(context, "LOADING");
        this.userSharedPrefs = new SharedPrefs(context, "USER");

        bottomNavigationView = getActivity().findViewById(R.id.bottomNavigationView);

//        bottomNavigationView.setSelectedItemId(R.id.setting);
        ImageView back = view.findViewById(R.id.back);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                remove();
                getActivity().onBackPressed();
            }
        });

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, REQUEST_CODE);
        }

        name = view.findViewById(R.id.name);
        name1 = view.findViewById(R.id.name1);
        email = view.findViewById(R.id.email);
        address = view.findViewById(R.id.address);
        profile = view.findViewById(R.id.profile);


        t_n_c = view.findViewById(R.id.t_n_c);
        privacy_policy = view.findViewById(R.id.privacy_policy);
        refund_cancellation = view.findViewById(R.id.refund_cancellation);
        about_us = view.findViewById(R.id.about_us);
        rate_us = view.findViewById(R.id.rate_us);
        log_out = view.findViewById(R.id.log_out);
        mobile = view.findViewById(R.id.mobile);


        logout_dialog = new Dialog(context);
        logout_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        logout_dialog.setContentView(R.layout.logout_dialog);
        Window window = logout_dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.setBackgroundDrawableResource(R.color.semi_transparent);
        logout_dialog.setCancelable(true);


        userSharedPrefs.getSharedPrefs("mobile");

        t_n_c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenFragment("terms_and_conditions.json");
            }
        });


        privacy_policy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenFragment("privacy_policy.json");
            }
        });

        refund_cancellation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenFragment("refund_and_cancellations.json");
            }
        });


        about_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenFragment("about_us.json");
            }
        });




        rate_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                // To count with Play market backstack, After pressing back button,
                // to taken back to our application, we need to add following flags to intent.
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName())));
                }

            }
        });

        log_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                logout_dialog.show();

                TextView yes, no;
                yes = logout_dialog.findViewById(R.id.yes);
                no = logout_dialog.findViewById(R.id.no);
                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        userSharedPrefs.clearAll();
                        getActivity().finish();
                        Intent i = new Intent(context, LoginActivity.class);
                        getActivity().startActivity(i);
                        logout_dialog.dismiss();
                    }
                });

                no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        logout_dialog.dismiss();
                    }
                });
            }
        });


        TextView upload_text = view.findViewById(R.id.textView111);
        name.setText(userSharedPrefs.getSharedPrefs("name"));
        name1.setText(userSharedPrefs.getSharedPrefs("name"));
        email.setText(userSharedPrefs.getSharedPrefs("email"));
        address.setText(userSharedPrefs.getSharedPrefs("address"));
        mobile.setText(userSharedPrefs.getSharedPrefs("mobile"));

        if (userSharedPrefs.getSharedPrefs("image") != null) {
            String image_path = context.getString(R.string.file_base_url) + "customers/" + userSharedPrefs.getSharedPrefs("image");
            Picasso.get().load(image_path).placeholder(R.drawable.handi_logo).into(profile);
            upload_text.setVisibility(View.GONE);
        } else upload_text.setVisibility(View.VISIBLE);



        edit1 = view.findViewById(R.id.edit1);

        if (userSharedPrefs.getSharedPrefs("name") == null) {

            edit1.setText("Save");
            enableEditText(name);
            name.setSelection(name.getText().length());
            name.requestFocus();
            enableEditText(email);
            enableEditText(address);

        } else {

            disableEditText(name);
            disableEditText(email);
            disableEditText(address);

        }

        edit1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edit1.getText().toString().equals("Save")) {
                    SubmitData submitData = new SubmitData();
                    submitData.execute(
                            userSharedPrefs.getSharedPrefs("mobile"),
                            name.getText().toString(),
                            email.getText().toString(),
                            address.getText().toString()
                    );
                    edit1.setText("Edit");
                    disableEditText(name);
                    disableEditText(email);

                    disableEditText(address);

                } else if (edit1.getText().toString().equals("Edit")) {
                    edit1.setText("Save");

                    enableEditText(name);
                    name.setSelection(name.getText().length());
                    name.requestFocus();
                    enableEditText(email);
                    enableEditText(address);

                }
            }
        });


        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .start(getContext(), SettingsFragment.this);
            }
        });


        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, PERMISSION_REQUEST);
            } else {
                Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


    class SubmitData extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            processDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            processDialog.dismiss();
            try {

                Dialog dialog;
                dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.my_dialog);
                Window window = dialog.getWindow();
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                window.setBackgroundDrawableResource(R.color.semi_transparent);
                dialog.setCancelable(true);
                TextView title, message;
                title = dialog.findViewById(R.id.title);
                message = dialog.findViewById(R.id.message);


                final JSONObject jsonObject = new JSONObject(s);
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
                    dialog.show();
                } else if (jsonObject.getString("status").equals("true")) {


                    title.setText("Success");
                    message.setText(jsonObject.getString("msg"));

                    processDialog.dismiss();
                    try {
                        JSONObject customer_JO = new JSONObject(jsonObject.getString("data"));

                        userSharedPrefs.setSharedPrefs("id", customer_JO.getString("id"));
                        userSharedPrefs.setSharedPrefs("name", customer_JO.getString("name"));
                        userSharedPrefs.setSharedPrefs("email", customer_JO.getString("email"));
                        userSharedPrefs.setSharedPrefs("mobile", customer_JO.getString("mobile"));
                        userSharedPrefs.setSharedPrefs("address", customer_JO.getString("address"));

                        bottomNavigationView.setSelectedItemId(R.id.home);
//                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new OrderTypeFragment(context, bottomNavigationView)).commit();

                    } catch (JSONException e) {
                        e.printStackTrace();


                    }
                    dialog.show();

                } else if (jsonObject.getString("status").equals("error")) {
                    title.setText("Undefined");
                    message.setText(jsonObject.getString("msg"));
                    dialog.show();
                } else {
                    title.setText("Error");
                    message.setText(jsonObject.getString("msg"));
                    dialog.show();
                }
                Handler handler = new Handler();

                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                    }
                };
                handler.postDelayed(runnable, 2000);
            } catch (JSONException e) {

            }
            processDialog.dismiss();
        }

        @Override
        protected String doInBackground(String... params) {
            String urls = context.getString(R.string.base_url).concat("register_customer/");
            try {
                URL url = new URL(urls);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_Data = URLEncoder.encode("mobile", "UTF-8") + "=" + URLEncoder.encode(params[0], "UTF-8") + "&" +
                        URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(params[1], "UTF-8") + "&" +
                        URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(params[2], "UTF-8") + "&" +
                        URLEncoder.encode("address", "UTF-8") + "=" + URLEncoder.encode(params[3], "UTF-8");

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

    private void OpenFragment(String url) {

        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new UrlFragment(context, url)).addToBackStack(null).commit();

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                    profile.setImageBitmap(bitmap);

                    UploadProfileImage uploadProfileImage = new UploadProfileImage();
                    uploadProfileImage.execute();

                } catch (IOException e) {
                    Log.d("sdr:", e.toString());
                }
            }
        }
    }


    class UploadProfileImage extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            processDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            processDialog.dismiss();
        }

        @Override
        protected String doInBackground(String... params) {
            uploadMultipart();
            return null;
        }
    }

    public void uploadMultipart() {

        String urls = context.getString(R.string.base_url).concat("/upload_customer_image/");

        //Uploading code
        Random rand = new Random();
        int random_no = rand.nextInt(9999999);
        String image_name = userSharedPrefs.getSharedPrefs("mobile") + random_no + ".jpg";
        userSharedPrefs.setSharedPrefs("image", image_name);
        try {
            String uploadId = UUID.randomUUID().toString();

            //Creating a multi part request

            new MultipartUploadRequest(context, uploadId, urls)
                    .addFileToUpload(imageUri.getPath(), "image") //Adding file
                    .addParameter("name", image_name) //Adding text parameter to the request
                    .addParameter("customer_id", userSharedPrefs.getSharedPrefs("id")) //Adding text parameter to the request

                    .setMaxRetries(2)
                    .startUpload(); //Starting the upload

        } catch (Exception exc) {
            Toast.makeText(context, exc.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

}
