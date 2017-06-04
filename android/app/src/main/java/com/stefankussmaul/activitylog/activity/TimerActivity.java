package com.stefankussmaul.activitylog.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.stefankussmaul.activitylog.R;
import com.stefankussmaul.activitylog.content.DBManager;
import com.stefankussmaul.activitylog.content.LogEntry;
import com.stefankussmaul.activitylog.content.MsTimer;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Provides Stopwatch/Timer functionality for the user, with ability to then log it.
 */

public class TimerActivity extends AppCompatActivity implements EditLogEntryDialog.LogDialogListener {

    private MsTimer msTimer;
    private Timer timerThread;
    private TimerTask timerTask;

    private EditText hoursField;
    private EditText minutesField;
    private EditText secondsField;

    private Button startButton;
    private Button pauseButton;
    private Button logTimeButton;
    private Button resetTimeButton;

    // alpha value for disabled buttons
    private static final float DISABLED_ALPHA = 0.7f;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timer_activity_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // enable up button to go back to MainActivity
        ActionBar action_bar = getSupportActionBar();
        action_bar.setDisplayHomeAsUpEnabled(true);

        msTimer = new MsTimer();

        hoursField = (EditText) findViewById(R.id.hours_field);
        minutesField = (EditText) findViewById(R.id.minutes_field);
        secondsField = (EditText) findViewById(R.id.seconds_field);

        startButton = (Button) findViewById(R.id.start_button);
        pauseButton = (Button) findViewById(R.id.pause_button);
        logTimeButton = (Button) findViewById(R.id.log_time_button);
        resetTimeButton = (Button) findViewById(R.id.reset_time_button);

        // disable pause button
        pauseButton.setEnabled(false);
        pauseButton.setAlpha(DISABLED_ALPHA);

        // disable reset button
        resetTimeButton.setEnabled(false);
        resetTimeButton.setAlpha(DISABLED_ALPHA);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private TimerTask createTimerTask() {
        return new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateTimerDisplay();
                    }
                });
            }
        };
    }

    // updates timer hour, minute, second fields with data from Timer
    private void updateTimerDisplay() {
        hoursField.setText(msTimer.getHoursField() + "");
        minutesField.setText(msTimer.getMinutesField() + "");
        secondsField.setText(msTimer.getSecondsField() + "");
    }

    // handles user clicking Start button. Starts Timer object and TimerThread, disables
    // the start button and enables the pause button
    public void startTimer(View view) {
        if (!msTimer.isTiming()) {
            msTimer.start();
            timerThread = new Timer();
            timerThread.schedule(createTimerTask(), 1_000, 1_000);
            startButton.setEnabled(false);
            startButton.setAlpha(DISABLED_ALPHA);
            pauseButton.setEnabled(true);
            pauseButton.setAlpha(1);
            resetTimeButton.setEnabled(true);
            resetTimeButton.setAlpha(1);
        }
    }

    // handles user clicking Pause button. Stops Timer object and TimerThread, disables pause button
    // and enables start button
    public void pauseTimer(View view) {
        if (msTimer.isTiming()) {
            msTimer.pause();
            timerThread.cancel();
            pauseButton.setEnabled(false);
            pauseButton.setAlpha(DISABLED_ALPHA);
            startButton.setEnabled(true);
            startButton.setAlpha(1);
        }
    }

    // handles user clicking button to log the Timer's current time. Pauses the timer and displays
    // LogEntry dialog pre-filled with duration from timer and current date. Sets listener to save
    // the entry to the DB when user is done
    public void logTime(View view) {
        pauseTimer(null);
        LogEntry new_entry = new LogEntry("", System.currentTimeMillis(), (int) msTimer.getTimedMs());
        EditLogEntryDialog log_dialog = EditLogEntryDialog.newInstance(new_entry);
        log_dialog.show(getFragmentManager(), "Log");
    }

    @Override // handles user saving a LogEntry they edited through EditLogEntryFragment
    public void onLogSaved(EditLogEntryDialog dialogFragment, LogEntry createdEntry) {
        DBManager.insertEntry(createdEntry);
        dialogFragment.dismiss();
    }

    // handles user clicking button to reset the timer. Resets the Timer object, stops the timerTask,
    // clears all the display fields.
    public void resetTimer(View view) {
        timerThread.cancel();
        msTimer.reset();
        hoursField.setText("00");
        minutesField.setText("00");
        secondsField.setText("00");
        startButton.setEnabled(true);
        startButton.setAlpha(1);
        pauseButton.setEnabled(false);
        pauseButton.setAlpha(DISABLED_ALPHA);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (timerThread != null) {
            timerThread.cancel();
        }
    }
}
