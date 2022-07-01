package com.matrix_maeny.alarm.notifications;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.matrix_maeny.alarm.R;
import com.matrix_maeny.alarm.receivers.AlarmReceiver;

public class NotificationSender extends Application {

    public static final String CHANNEL_ID = "MATRIX_ALARM";
    public static final String CHANNEL_NAME = "ALARM";

    @Override
    public void onCreate() {
        super.onCreate();

        createChannel();
    }

    private void createChannel() {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            NotificationChannel notificationChannel = new NotificationChannel(
                    CHANNEL_ID,CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH
            );

            notificationChannel.setDescription("Sends notification when Alarm starts ringing");

            NotificationManager manager = (NotificationManager) getSystemService(NotificationManager.class);
            manager.createNotificationChannel(notificationChannel);
        }
    }


//    public void sendNotification(String time){
//
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
//            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//
//            Intent actionIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
//            actionIntent.putExtra("status","stop");
//
//            PendingIntent actionIntentPending = PendingIntent.getBroadcast(getApplicationContext(),0,actionIntent,PendingIntent.FLAG_UPDATE_CURRENT);
//
//            Notification notification = new NotificationCompat.Builder(this,CHANNEL_ID)
//                    .setContentTitle("Alarm")
//                    .setContentText("Time: "+time)
//                    .setSmallIcon(R.drawable.alarm)
//                    .setContentIntent(actionIntentPending)
//                    .addAction(R.drawable.alarm,"stop",actionIntentPending)
//                    .setAutoCancel(true)
//                    .setColor(Color.rgb(100,3,218))
//                    .setCategory("Alarm")
//                    .build();
//
//            manager.notify(0,notification);
//
//        }
//    }


}
