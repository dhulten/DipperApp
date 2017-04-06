package com.example.dipper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
}
