package com.example.wuhui.gesturelock;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by wuhui on 2016/12/15.
 */

public class SetGestureActivity extends Activity {
    public static final int launcherAct = 0;
    public static final int settingAct = 1;
    public static int activityNum;
    public static StatusChange statusChange = new StatusChange();
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_gesture);

        ActivityCollector.addActivity(this);

        Intent intent = getIntent();
        activityNum = intent.getIntExtra("activityNum", 0);

        preferences= PreferenceManager.getDefaultSharedPreferences(this);

        final TextView gestureText = (TextView) findViewById(R.id.gesture_text);
        final TextView prompt=(TextView) findViewById(R.id.prompt);
        CircleImageView userHeader = (CircleImageView) findViewById(R.id.user_header);

        switch (activityNum) {
            case settingAct:
                statusChange.addPropertyChangeListener(new PropertyChangeListener() {
                    @Override
                    public void propertyChange(PropertyChangeEvent event) {
                        if ((int) event.getNewValue() == 2) {
                            finish();
                        } else if ((int) event.getNewValue() == 4) {
                            gestureText.setText("再次绘制以确认");
                            prompt.setVisibility(View.VISIBLE);
                            prompt.setText("请与上次绘制保持一致");
                        }
                    }
                });
                break;
            case launcherAct:
                if (!TextUtils.isEmpty(preferences.getString("gesture", ""))) {
                    gestureText.setVisibility(View.GONE);
                    userHeader.setVisibility(View.VISIBLE);
                    userHeader.setImageResource(R.drawable.header);
                    statusChange.addPropertyChangeListener(new PropertyChangeListener() {
                        @Override
                        public void propertyChange(PropertyChangeEvent event) {
                            if ((int) event.getNewValue() == 2) {
                                finish();
                            }
                        }
                    });
                }
                break;
            default:
                break;
        }

    }

    @Override
    public void onBackPressed() {
        if (activityNum==launcherAct) {
            ActivityCollector.finishAll();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }
}
