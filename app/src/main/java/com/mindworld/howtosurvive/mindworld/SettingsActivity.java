package com.mindworld.howtosurvive.mindworld;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Switch;

public class SettingsActivity extends AppCompatActivity {
    private static final String LARGER_FONT = "LARGER_FONT";

    private SharedPreferences mPreferences;
    private String sharedPrefFile = "com.mindworld.howtosurvive.mindworld";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        final SharedPreferences.Editor preferencesEditor = mPreferences.edit();

        final Switch largerFontSwitch = (Switch) findViewById(R.id.larger_font_switch);
        largerFontSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String statusSwitch1;
                if (largerFontSwitch.isChecked()) {
                    preferencesEditor.putBoolean(LARGER_FONT, true).commit();
                } else {
                    preferencesEditor.putBoolean(LARGER_FONT, false).commit();
                }
            }
        });
    }
}
