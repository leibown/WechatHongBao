package com.leibown.accessibilityservicedemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.kyleduo.switchbutton.SwitchButton;

public class MainActivity extends Activity {

    private SwitchButton sb_strong;
    private Button btnQiangQian;
    private AccessibilityManager accessibilityManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sb_strong = (SwitchButton) findViewById(R.id.sb_strong);
        btnQiangQian = (Button) findViewById(R.id.btn_qiangqian);

        accessibilityManager = (AccessibilityManager) getSystemService(ACCESSIBILITY_SERVICE);
        accessibilityManager.addAccessibilityStateChangeListener(new AccessibilityManager.AccessibilityStateChangeListener() {
            @Override
            public void onAccessibilityStateChanged(boolean b) {
                if (b) {
                    Toast.makeText(MainActivity.this, "开抢！( $ _ $ )", Toast.LENGTH_LONG).show();
                }
            }
        });

        btnQiangQian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (accessibilityManager.isEnabled()) {
                    Toast.makeText(MainActivity.this, "已经在抢咯！( $ _ $ )", Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                    startActivity(intent);
                }
            }
        });

        boolean isStrong = getSharePre();
        Constans.isStrongMode = isStrong;
        sb_strong.setChecked(isStrong);
        sb_strong.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Constans.isStrongMode = isChecked;
                putSharePre(isChecked);
            }
        });
    }


    private void putSharePre(boolean isStrong) {
        SharedPreferences sp = getSharedPreferences("QIANG_HONG_BAO", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("isStrong", isStrong);
        editor.apply();
    }

    private boolean getSharePre() {
        SharedPreferences sp = getSharedPreferences("QIANG_HONG_BAO", Context.MODE_PRIVATE);
        return sp.getBoolean("isStrong", false);
    }
}
