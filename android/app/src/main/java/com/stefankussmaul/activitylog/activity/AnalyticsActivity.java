package com.stefankussmaul.activitylog.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.stefankussmaul.activitylog.R;
import com.stefankussmaul.activitylog.content.DBManager;

/**
 * Created by Stefan on 5/29/2017.
 */

public class AnalyticsActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.analytics_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.home_toolbar);
        setSupportActionBar(toolbar);
    }
}
