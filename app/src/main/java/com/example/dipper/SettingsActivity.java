package com.example.dipper;

import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SharedPreferences settings = getApplicationContext().getSharedPreferences(Constants.Preferences, 0);

        ((Switch)findViewById(R.id.pushNotificationsSwitch)).setChecked(settings.getBoolean(Constants.UserPushNotificationsKey, false));
        ((TextView)findViewById(R.id.alertTimeVal)).append(String.valueOf(settings.getInt(Constants.UserAlertTimeKey, 60)));
    }

    public void saveSettings(View view){
        boolean pushNotificaitonsSetting = ((Switch)findViewById(R.id.pushNotificationsSwitch)).isChecked();
        boolean alertTimeParseSuccess = tryParse(((TextView)findViewById(R.id.alertTimeVal)).getText().toString());

        findViewById(R.id.saveResults).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.saveResults)).setText("");

        if (alertTimeParseSuccess) {
            try {
                int alertTimeVal = Integer.parseInt(((TextView) findViewById(R.id.alertTimeVal)).getText().toString());

                SharedPreferences settings = getApplicationContext().getSharedPreferences(Constants.Preferences, 0);
                SharedPreferences.Editor editor = settings.edit();

                editor.putBoolean(Constants.UserPushNotificationsKey, pushNotificaitonsSetting);
                editor.putInt(Constants.UserAlertTimeKey, alertTimeVal);
                editor.apply();

                ((TextView) findViewById(R.id.saveResults)).append("Settings saved successfully!");
            } catch (Exception ex) {
                ((TextView) findViewById(R.id.saveResults)).append("Error saving settings");
            }


        }
        else {
            ((TextView)findViewById(R.id.saveResults)).append("Alert time must be formatted as a number!");
        }

    }

    private boolean tryParse(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
