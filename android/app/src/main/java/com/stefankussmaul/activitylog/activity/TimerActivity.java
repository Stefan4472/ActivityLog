package com.stefankussmaul.activitylog.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.stefankussmaul.activitylog.R;
import com.stefankussmaul.activitylog.content.DBManager;
import com.stefankussmaul.activitylog.content.DateUtil;
import com.stefankussmaul.activitylog.content.LogEntry;
import com.stefankussmaul.activitylog.content.MsTimer;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Provides Stopwatch/Timer functionality for the user, with ability to then log it.
 */

public class TimerActivity extends AppCompatActivity implements EditLogEntryDialog.LogDialogListener {

    private MsTimer msTimer;
    private Timer timerThread;
    private TimerTask timerTask;

    private TextView timerDisplay;

    private Button playPauseButton;
    private Button logTimeButton;
    private Button resetTimeButton;

    private boolean paused = true;

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

        timerDisplay = (TextView) findViewById(R.id.timer_display);
        playPauseButton = (Button) findViewById(R.id.play_pause_button);
        logTimeButton = (Button) findViewById(R.id.log_time_button);
        resetTimeButton = (Button) findViewById(R.id.reset_time_button);

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
        timerDisplay.setText(
                MsTimer.formatField(msTimer.getHoursField()) + ":" +
                MsTimer.formatField(msTimer.getMinutesField()) + ":" +
                MsTimer.formatField(msTimer.getSecondsField()));
    }

    // handles user clicking play/pause button, toggling the state of the timer
    public void toggleTimer(View view) {
        if (paused) { // play
            msTimer.start();
            timerThread = new Timer();
            timerThread.schedule(createTimerTask(), 1_000, 1_000);
            playPauseButton.setText(getString(R.string.pause_timer));
            resetTimeButton.setEnabled(true);
            resetTimeButton.setAlpha(1);
        } else { // pause
            msTimer.pause();
            timerThread.cancel();
            playPauseButton.setText(getString(R.string.start_timer));
        }
        paused = !paused;
    }

    // handles user choosing a mode for the timer (count up or count down)
    public void onModeSelected(View view) {
        switch (view.getId()) {
            case R.id.count_down:
                // show DurationPickerDialog with current hour/min/sec field
                DurationPickerDialog dialog = DurationPickerDialog.newInstance(
                        getString(R.string.set_count_down), true, true, true, (int) msTimer.getTimedMs());
                // set listener to update msTimer
                dialog.setOnDurationChangedListener(new DurationPickerDialog.OnDurationChangedListener() {
                    @Override
                    public void onDurationSet(DurationPickerDialog dialog, int hours, int minutes, int seconds) {
                        long ms = hours * DateUtil.HOUR_MS + minutes * DateUtil.MINUTE_MS +
                                seconds * DateUtil.SECOND_MS;
                        msTimer.setCountDown(ms);
                        updateTimerDisplay();
                        dialog.dismiss();
                    }
                });
                dialog.show(getFragmentManager(), "Pick Duration");
                break;
            case R.id.count_up:
                msTimer.setCountUp();
                break;
            default:
                throw new IllegalArgumentException("Only Radiobutton should be passed");
        }
    }

    // handles user clicking button to log the Timer's current time. Pauses the timer and displays
    // LogEntry dialog pre-filled with duration from timer and current date.
    public void logTime(View view) {
        if (!paused) {
            toggleTimer(null);
        }
        LogEntry new_entry = new LogEntry("", System.currentTimeMillis(), (int) msTimer.getTimedMs());
        EditLogEntryDialog log_dialog = EditLogEntryDialog.newInstance(new_entry);
        log_dialog.show(getFragmentManager(), "Log");
    }

    @Override // handles user saving a LogEntry they edited through EditLogEntryFragment
    public void onLogSaved(EditLogEntryDialog dialogFragment, LogEntry createdEntry) {
        DBManager.insertEntry(createdEntry);
        dialogFragment.dismiss();
    }

    @Override
    public void onLogCancelled(EditLogEntryDialog dialogFragment) {
        dialogFragment.dismiss();
    }

    // handles user clicking button to reset the timer. Resets the Timer object, stops the timerTask,
    // clears all the display fields.
    public void resetTimer(View view) {
        timerThread.cancel();
        msTimer.reset();
        timerDisplay.setText("00:00:00");
    }

    @Override
    public void onPause() {
        super.onPause();
        if (timerThread != null) {
            timerThread.cancel();
        }
    }
}
