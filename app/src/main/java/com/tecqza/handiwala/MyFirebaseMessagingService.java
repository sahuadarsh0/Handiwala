package com.tecqza.handiwala;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    SharedPrefs token;
    String order_id;
    String fragment;

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        token = new SharedPrefs(getApplicationContext(), "TOKEN");
        token.setSharedPrefs("token", s);
        Log.d("TOKEN_CUSTOMER", s);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {

            if (remoteMessage.getData().size() > 0) {
                Log.d("asa", "Message data payload: " + remoteMessage.getData());
                try {
                    JSONObject data = new JSONObject(remoteMessage.getData());
                    order_id = data.getString("extra");
                    fragment = data.getString("fragment");
                    Log.d("asa", "onMessageReceived: \n" +
                            "Extra Information: " + order_id);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            String title = remoteMessage.getNotification().getTitle(); //get title
            String message = remoteMessage.getNotification().getBody(); //get message
            String click_action = remoteMessage.getNotification().getClickAction(); //get click_action

            Log.d("asa", "Message Notification Title: " + title);
            Log.d("asa", "Message Notification Body: " + message);
            Log.d("asa", "Message Notification click_action: " + click_action);

            if (order_id != null)
                sendNotification(title, message, click_action, "ORDER");


        }


    }


    public static final int PRIMARY_FOREGROUND_NOTIF_SERVICE_ID = 1001;


    private void sendNotification(String title, String messageBody, String click_action, String extra) {
        Intent intent;
        if (click_action.equals("MainActivity") && extra.equals("ORDER")) {
            intent = new Intent(this, MainActivity.class);
            intent.putExtra("fragment", fragment);
            intent.putExtra("extra", order_id);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        } else {
            intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            String id = "_channel_01";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(id, "notification", importance);
            mChannel.enableLights(true);

            Notification notification = new Notification.Builder(getApplicationContext(), id)
                    .setSmallIcon(R.drawable.handi_logo)
                    .setContentTitle(title)
                    .setContentText(messageBody)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .build();

            notification.flags = Notification.FLAG_AUTO_CANCEL;

            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (mNotificationManager != null) {
                mNotificationManager.createNotificationChannel(mChannel);
                mNotificationManager.notify(PRIMARY_FOREGROUND_NOTIF_SERVICE_ID, notification);
            }

            startForeground(PRIMARY_FOREGROUND_NOTIF_SERVICE_ID, notification);
        }
    }
}