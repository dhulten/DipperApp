package com.example.dipper;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private ScheduledExecutorService scheduleTaskExecutor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scheduleNotification(5000);
    }


    public void checkIn(View view){
        Intent i = new Intent(this, DisplayImageActivity.class);
        startActivity(i);
    }

    public void viewSettings(View view){
        Intent i = new Intent(this, SettingsActivity.class);
        startActivity(i);
    }

    public void viewHistory(View view){
        Intent i = new Intent(this, ViewHistoryActivity.class);
        startActivity(i);
    }

    private void scheduleNotification(int delay) {

        Intent notificationIntent = new Intent(this, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, 1);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME, futureInMillis, pendingIntent);

        //alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
        //      SystemClock.elapsedRealtime(),
        //    AlarmManager.INTERVAL_FIFTEEN_MINUTES, alarmIntent);
    }
}
