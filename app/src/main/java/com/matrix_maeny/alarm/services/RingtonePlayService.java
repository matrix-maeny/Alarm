package com.matrix_maeny.alarm.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.Vibrator;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.matrix_maeny.alarm.receivers.AlarmReceiver;

public class RingtonePlayService extends Service {

    Ringtone ringtone;
    Uri alarmUri;
    AudioManager manager;
    int ringMode;
    MediaPlayer player;


    @Override
    public void onCreate() {
        super.onCreate();

        alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        manager = (AudioManager) getSystemService(AUDIO_SERVICE);


        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);


        }

        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        }


        ringtone = RingtoneManager.getRingtone(getApplicationContext(), alarmUri);
        player = MediaPlayer.create(getApplicationContext(), alarmUri);

        ringMode = manager.getRingerMode();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String status = intent.getStringExtra("status");

        if (ringtone != null) {
            if (status.equals("play")) {
                if (!ringtone.isPlaying()) {
                    if (ringMode == AudioManager.RINGER_MODE_SILENT || ringMode == AudioManager.RINGER_MODE_VIBRATE) {
                        player.start();
                    } else {
                        ringtone.play();
                    }

                }
            } else if (status.equals("stop")) {
                if (ringtone.isPlaying() || player.isPlaying()) {
                    if (ringMode == AudioManager.RINGER_MODE_SILENT || ringMode == AudioManager.RINGER_MODE_VIBRATE) {
                        player.stop();
                    } else {
                        ringtone.stop();

                    }
                    stopSelf();
                }


            }
        }

        return START_STICKY;

    }
}
