package com.example.wuhui.gesturelock;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Switch aSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("wuhuitest","wuhuitest");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();

        if (!TextUtils.isEmpty(preferences.getString("gesture", ""))) {
            Intent intent=new Intent(MainActivity.this,SetGestureActivity.class);
            intent.putExtra("activityNum",0);
            startActivity(intent);
        }

        aSwitch=(Switch) findViewById(R.id.lock_switch);
        if (TextUtils.isEmpty(preferences.getString("gesture",""))) {
            aSwitch.setChecked(false);
        } else {
            aSwitch.setChecked(true);
        }
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Intent intent = new Intent(MainActivity.this, SetGestureActivity.class);
                    intent.putExtra("activityNum",1);
                    startActivity(intent);
                } else {
                    editor.remove("gesture");
                    editor.commit();
                }
            }
        });
    }
}
