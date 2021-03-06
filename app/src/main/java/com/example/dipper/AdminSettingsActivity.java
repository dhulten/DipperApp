package com.example.dipper;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

public class AdminSettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_settings);

        SharedPreferences settings = getApplicationContext().getSharedPreferences(Constants.Preferences, 0);

        ((Switch)findViewById(R.id.pushNotificationsSwitch)).setChecked(settings.getBoolean(Constants.AdminPushNotificationsKey, false));
        ((Switch)findViewById(R.id.adminModeSwitch)).setChecked(settings.getBoolean(Constants.AdminModeKey, false));
    }

    public void saveSettings(View view){
        boolean pushNotificaitonsSetting = ((Switch)findViewById(R.id.pushNotificationsSwitch)).isChecked();
        boolean adminModeSetting = ((Switch)findViewById(R.id.adminModeSwitch)).isChecked();

        findViewById(R.id.saveResults).setVisibility(View.VISIBLE);

        ((TextView) findViewById(R.id.saveResults)).setText("");

        try {
            SharedPreferences settings = getApplicationContext().getSharedPreferences(Constants.Preferences, 0);
            SharedPreferences.Editor editor = settings.edit();

            editor.putBoolean(Constants.AdminPushNotificationsKey, pushNotificaitonsSetting);
            editor.putBoolean(Constants.AdminModeKey, adminModeSetting);
            editor.apply();

            ((TextView) findViewById(R.id.saveResults)).append("Settings saved successfully!");
        } catch (Exception ex) {
            ((TextView) findViewById(R.id.saveResults)).append("Error saving settings");
        }
    }

    public void testPush(View view){
        Intent notificationIntent = new Intent(this, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, 1);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        long futureInMillis = SystemClock.elapsedRealtime();
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME, futureInMillis, pendingIntent);
    }

}
