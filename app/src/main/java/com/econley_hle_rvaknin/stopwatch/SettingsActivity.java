package com.econley_hle_rvaknin.stopwatch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Switch guidanceSwitch = (Switch) findViewById(R.id.guidance_switch);
        guidanceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Log.v("Switch State=", ""+b);
                SharedPreferences storage = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = storage.edit();
                editor.putBoolean("guidanceStatus", b);
                editor.apply();
                if(b == true) {
                    Toast toast = Toast.makeText(SettingsActivity.this,
                            "You have selected mode ON!",
                            Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    Toast toast = Toast.makeText(SettingsActivity.this,
                            "You have selected mode OFF!",
                            Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

    }
}
