package com.exitoms.app;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class MyFirebaseMessagingService extends FirebaseMessagingService
{

    private static final String TAG = "WAS";
    public String title="";
    Uri sound = Uri.parse("android.resource://com.exitoms.app/" + R.raw.alert);

    @Override
    public void onNewToken(String s)
    {
        super.onNewToken(s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage)
    {

        try
        {

            JSONObject json = new JSONObject(remoteMessage.getData().toString());
            Log.e(TAG, "Json Exception: " + remoteMessage.getData().toString());
            JSONObject data = json.getJSONObject("data");
            title = data.getString("title");
            String message = data.getString("message");
            showNotification(title,message);
            updateMyActivity(getApplicationContext(),message);
        }
        catch (JSONException e)
        {
            Log.e(TAG, "Json Exception: " + e.getMessage());
            showNotification(title,e.getMessage());
        }
        super.onMessageReceived(remoteMessage);
    }

    static void updateMyActivity(Context context, String message) {
        Intent intent = new Intent("was-push");
        intent.putExtra("message", message);
        context.sendBroadcast(intent);
    }

    private void showNotification(String title, String message)
    {
        Log.d("Exito",title);
        String CHANNEL_ID="Exito";
        String CHANNEL_NAME="Alert";

        Intent resultIntent = new Intent(this, SplashActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationManager notificationManager = (NotificationManager) getApplication().getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(R.drawable.icon, message, System.currentTimeMillis());
        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            AudioAttributes attributes = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION).build();
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, getApplication().getString(R.string.app_name), NotificationManager.IMPORTANCE_HIGH);
            mChannel.setDescription(title);
            mChannel.enableLights(true);
            mChannel.enableVibration(true);
            mChannel.setSound(sound, attributes);

            if (notificationManager != null)
                notificationManager.createNotificationChannel(mChannel);
        }
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.icon);
        NotificationCompat.Builder status = new NotificationCompat.Builder(getApplicationContext(),CHANNEL_ID);
        status.setContentIntent(resultPendingIntent);
        status.setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.logo_white)
                .setColor(getResources().getColor(R.color.purple_700))
                .setContentTitle(title)
                .setLargeIcon(largeIcon)
                .setChannelId(CHANNEL_ID)
                .setNumber(1)
                .setContentText(message)
                .setWhen(System.currentTimeMillis())
                .setDefaults(Notification.FLAG_ONLY_ALERT_ONCE)
                .setSound(Uri.parse("android.resource://com.exitoms.app/"+R.raw.alert));

        notificationManager.notify(12232, status.build());
    }

}