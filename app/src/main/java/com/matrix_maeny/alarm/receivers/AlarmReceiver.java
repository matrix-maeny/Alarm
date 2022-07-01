package com.matrix_maeny.alarm.receivers;

import static android.content.Context.NOTIFICATION_SERVICE;
import static android.content.Context.POWER_SERVICE;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.os.Build;
import android.os.PowerManager;


import com.matrix_maeny.alarm.R;
import com.matrix_maeny.alarm.addTime.TimeDB;
import com.matrix_maeny.alarm.notifications.NotificationSender;
import com.matrix_maeny.alarm.services.RingtonePlayService;

public class AlarmReceiver extends BroadcastReceiver {

    Context context;
    PowerManager powerManager;


    @Override
    public void onReceive(Context context, Intent intent) {

        String status = intent.getStringExtra("status");
        String time = intent.getStringExtra("time");
        String type = intent.getStringExtra("type");
        this.context = context;


        Intent serviceIntent = new Intent(context.getApplicationContext(), RingtonePlayService.class);
        serviceIntent.putExtra("status", status);

        context.startService(serviceIntent);

        if (type == null && status != null && !status.equals("stop")) {
            sendNotification(time);

            updateData(time);


        }


    }

    private void updateData(String time) {
        TimeDB db = new TimeDB(context.getApplicationContext());

        db.updateEnabledUsingTime(time, 0);
        db.close();
    }


    public void sendNotification(String time) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

            Intent actionIntent = new Intent(context.getApplicationContext(), AlarmReceiver.class);
            actionIntent.putExtra("status", "stop");
            actionIntent.putExtra("type", "notification");
            actionIntent.putExtra("vibrate", "run");

            PendingIntent actionIntentPending = PendingIntent.getBroadcast(context.getApplicationContext(), 0, actionIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            Notification notification = new Notification.Builder(context.getApplicationContext(), NotificationSender.CHANNEL_ID)
                    .setContentTitle("Alarm: Click to stop")
                    .setContentText("Time: " + time)
                    .setSmallIcon(R.drawable.alarm)
                    .setContentIntent(actionIntentPending)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setAutoCancel(true)
//                    .setColor(Color.rgb(100,3,218))
                    .setCategory(Notification.CATEGORY_MESSAGE)
                    .build();

            manager.notify(0, notification);

        }

//        if (vibrator != null && vibrator.hasVibrator()) {
//            vibrator.vibrate(4000);
//        }

    }


    public class PowerClass {
        PowerManager powerManager;
        PowerManager.WakeLock wakeLock;
        Context context;

        public PowerClass(Context context) {
            this.context = context;
            powerManager = (PowerManager) context.getSystemService(POWER_SERVICE);

            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "Alarm::wakelock");

        }

        public void acquire() {
            wakeLock.acquire(10 * 60 * 1000L /*10 minutes*/);
        }

        public void release() {
            wakeLock.release();
        }

    }


}
