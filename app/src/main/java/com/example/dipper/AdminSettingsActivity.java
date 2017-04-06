package com.example.dipper;

import android.content.SharedPreferences;
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
}
