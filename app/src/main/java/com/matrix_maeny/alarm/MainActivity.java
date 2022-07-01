package com.matrix_maeny.alarm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.matrix_maeny.alarm.addTime.TimeAdapter;
import com.matrix_maeny.alarm.addTime.TimeDB;
import com.matrix_maeny.alarm.addTime.TimeModel;
import com.matrix_maeny.alarm.dialogs.AlarmTimePickerDialog;
import com.matrix_maeny.alarm.receivers.AlarmReceiver;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener, TimeAdapter.TimeAdapterListener {

    private RecyclerView recyclerView;

    private TimeAdapter adapter;
    private ArrayList<TimeModel> list;

    final Handler handler = new Handler();

    private TextView emptyText;


    private AlarmManager alarmManager = null;
    Intent intent;// = new Intent(MainActivity.this, AlarmReceiver.class);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize();
    }

    private void initialize() {
        recyclerView = findViewById(R.id.recyclerView);
        emptyText = findViewById(R.id.emptyText);
        emptyText.setVisibility(View.GONE);

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        intent = new Intent(MainActivity.this, AlarmReceiver.class);

        list = new ArrayList<>();
        adapter = new TimeAdapter(MainActivity.this, list);

        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        recyclerView.setAdapter(adapter);


        loadInfo();

    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadInfo() {
        TimeDB db = new TimeDB(MainActivity.this);
        Cursor cursor = db.getData();
        list.clear();

        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                String timeTxt = cursor.getString(2);
                int enabled = cursor.getInt(3);
                String[] arr = timeTxt.split(":");
                int hour = Integer.parseInt(arr[0].trim());
                int minute = Integer.parseInt(arr[1].trim());

                list.add(new TimeModel(hour, minute, enabled));

            }

        }


        handler.post(() -> {
            if (list.isEmpty()) {
                emptyText.setVisibility(View.VISIBLE);
            } else {
                emptyText.setVisibility(View.GONE);
            }
            adapter.notifyDataSetChanged();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.about_app:
                // go to about activity
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                break;
            case R.id.add_new:
                // add new schedule
                showTimePickerDialog();
                break;

            case R.id.delete_all:
                deleteAll();
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteAll() {
        TimeDB db = new TimeDB(MainActivity.this);
        Cursor cursor = db.getData();

        while (cursor.moveToNext()){
            String timeText = cursor.getString(2);
            String[] arr = timeText.split(":");
            int hour = Integer.parseInt(arr[0].trim());
            int minute = Integer.parseInt(arr[1].trim());

            setAlarm(hour,minute,false);
        }

        db.deleteAll();
        db.close();
        loadInfo();
    }


    private void showTimePickerDialog() {
        AlarmTimePickerDialog dialog = new AlarmTimePickerDialog();
        dialog.show(getSupportFragmentManager(), "Time Picker");
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//        Toast.makeText(this, "hour" + hourOfDay + " minute" + minute, Toast.LENGTH_SHORT).show();


        addTimeToDB(hourOfDay, minute);
        setAlarm(hourOfDay, minute, true);

    }

    @SuppressLint({"UnspecifiedImmutableFlag", "ShortAlarm"})
    private void setAlarm(int hourOfDay, int minute, boolean seated) {

        int timeCode = Integer.parseInt(hourOfDay + "" + minute);

//        pendingIntent = PendingIntent.getBroadcast(MainActivity.this, timeCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);


        PendingIntent pendingIntent = null;
        if (!seated) {
            intent.putExtra("status", "stop");
            pendingIntent = PendingIntent.getBroadcast(MainActivity.this, timeCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.cancel(pendingIntent);
            sendBroadcast(intent);
            return;
        } else {
            intent.putExtra("status", "play");
            intent.putExtra("time", hourOfDay + " : " + minute);
            pendingIntent = PendingIntent.getBroadcast(MainActivity.this, timeCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        }

        long time;
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);


        time = (calendar.getTimeInMillis() - (calendar.getTimeInMillis() % 60000));

        if (System.currentTimeMillis() > time) {

            if (Calendar.AM_PM == 0) {
                time = time + (1000 * 60 * 60 * 12);
            } else {
                time = time + (1000 * 60 * 60 * 24);

            }

        }


        alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, pendingIntent);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void addTimeToDB(int hourOfDay, int minute) {

        TimeDB db = new TimeDB(MainActivity.this);

        TimeModel model = new TimeModel(hourOfDay, minute, 1);
        int timeCode = Integer.parseInt(hourOfDay + "" + minute);


        if (db.insertTime(list.size(), timeCode, model.getTimeString(), 1)) {

            Toast.makeText(this, "Created: Alarm on", Toast.LENGTH_SHORT).show();
            list.add(new TimeModel(hourOfDay, minute, 1));
            adapter.notifyDataSetChanged();
            emptyText.setVisibility(View.GONE);

        } else {

            Toast.makeText(this, "Some error occurred: Main: 224", Toast.LENGTH_SHORT).show();

        }


//        else {
//            Cursor cursor = db.getData();
//
//            for (int i = 0; i <= adapterPosition; i++) cursor.moveToNext();
////            int enabled = cursor.getInt(3);
//
//            if (db.updateTime(adapterPosition, timeCode, model.getTimeString(), 1)) {
//
//                Toast.makeText(this, "Alarm on", Toast.LENGTH_SHORT).show();
//                loadInfo();
//
//            } else {
//
//                Toast.makeText(this, "Some error occurred update: Main: 162", Toast.LENGTH_SHORT).show();
//
//            }
//        }

        db.close();

    }


    @Override
    public void getCheckedResponse(boolean seated, int adapterPosition) { // from TimeAdapter


        if (seated) {
            // set alarm
            Toast.makeText(this, "Alarm on", Toast.LENGTH_SHORT).show();
        } else {
            //cancel alarm
            Toast.makeText(this, "Alarm off", Toast.LENGTH_SHORT).show();

        }

        updateEnabled(seated, adapterPosition);
        updateAlarm(seated, adapterPosition);


    }

    @Override
    public void cancelAlarms(String timeText) {

        String[] arr = timeText.split(":");
        int hour = Integer.parseInt(arr[0].trim());
        int minute = Integer.parseInt(arr[1].trim());

        setAlarm(hour, minute, false);


    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private void updateAlarm(boolean seated, int adapterPosition) {

        TimeDB db = new TimeDB(MainActivity.this);
        Cursor cursor = db.getData();

        if (cursor.getCount() != 0) {
            for (int i = 0; i <= adapterPosition; i++) cursor.moveToNext();

            String timeText = cursor.getString(2);
            String[] arr = timeText.split(":");
            int hour = Integer.parseInt(arr[0].trim());
            int minute = Integer.parseInt(arr[1].trim());

            setAlarm(hour, minute, seated);

        }


    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateEnabled(boolean seated, int adapterPosition) {
        int enable = (seated) ? 1 : 0;

        TimeDB db = new TimeDB(MainActivity.this);


        if (!db.updateEnabled(adapterPosition, enable)) {
            Toast.makeText(this, "Some error occurred: Main:195", Toast.LENGTH_SHORT).show();
        }

        list.get(adapterPosition).setEnabled(enable);

        handler.post(() -> adapter.notifyDataSetChanged());


        db.close();


    }


    @Override
    public void refresh() {
        loadInfo();
    }

}