package com.stefankussmaul.activitylog.activity;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.stefankussmaul.activitylog.R;
import com.stefankussmaul.activitylog.content.DBManager;

/**
 * Displays MainScreen of the app.
 */

public class MainActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homescreen_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.home_toolbar);

        setSupportActionBar(toolbar);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        DBManager manager = new DBManager(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override // handle user clicking something in the action bar
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_shownav:
                Log.d("MainActivity", "ShowNav clicked");
                return true;
            default: // send to be handled by super
                return super.onOptionsItemSelected(item);
        }
    }

    public void launchLogDialog(View view) {
        Log.d("MainActivity", "Launching Log Activity");
        DialogFragment log_dialog = LogActivityDialogFragment.newInstance();
        log_dialog.show(getFragmentManager(), "Log");
    }

    public void launchStartActivity(View view) {
        Log.d("MainActivity", "Launching Start Activity");

    }

    public void launchAnalytics(View view) {
        Log.d("MainActivity", "Launching Analytics");

    }

    public void launchViewEditLog(View view) {
        Log.d("MainActivity", "Launching View/Edit Log");


    }
}
