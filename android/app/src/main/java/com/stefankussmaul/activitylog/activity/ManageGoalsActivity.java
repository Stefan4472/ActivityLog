package com.stefankussmaul.activitylog.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.stefankussmaul.activitylog.R;
import com.stefankussmaul.activitylog.content.DBManager;
import com.stefankussmaul.activitylog.content.Goal;

import java.util.Date;
import java.util.List;

/**
 * Created by Stefan on 5/12/2018.
 */

public class ManageGoalsActivity extends AppCompatActivity {

    // scrolling list of goals
    private RecyclerView goalsRecyclerView;
    // used to populate goalsRecyclerView with Goals
    private GoalAdapter goalAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.managegoals_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // enable up button to go back to MainActivity
        ActionBar action_bar = getSupportActionBar();
        action_bar.setDisplayHomeAsUpEnabled(true);

        // fill goals view with list of current goals (sorted by relevance)
        goalsRecyclerView = (RecyclerView) findViewById(R.id.goals_recyclerview);
        goalsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // retrieve goals relevant to current time and update progress
        List<Goal> current_goals = DBManager.getGoals(new Date(System.currentTimeMillis()));
        for (Goal goal : current_goals) {
            goal.updateProgress();
        }

        goalAdapter = new GoalAdapter(current_goals);
        goalsRecyclerView.setAdapter(goalAdapter);
    }

}
