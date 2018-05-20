package com.stefankussmaul.activitylog.activity;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.stefankussmaul.activitylog.R;
import com.stefankussmaul.activitylog.content.Goal;

import java.util.List;

/**
 * Created by Stefan on 5/19/2018.
 */

public class GoalAdapter extends RecyclerView.Adapter<GoalAdapter.GoalViewHolder> {

    List<Goal> goals;

    public GoalAdapter(List<Goal> goals) {
        Log.d("GoalAdapter", "Creating adapter with " + goals.size() + " goals");
        this.goals = goals;
    }

    @Override
    public GoalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.goal_adapter, null);
        return new GoalViewHolder(view);
    }

    @Override // fills UI with data from Goal at given position
    public void onBindViewHolder(GoalViewHolder holder, int position) {
        // retrieve queried goal
        Goal goal = goals.get(position);

        Log.d("GoalAdapter", "Goal activity is " + goal.getActivity());
        holder.title.setText(goal.getActivity());
        holder.goalData.setText(goal.toString());
        holder.progressbar.setProgress((int) goal.getPercentProgress());//, true);
    }

    @Override
    public int getItemCount() {
        return goals.size();
    }

    class GoalViewHolder extends RecyclerView.ViewHolder {
        protected TextView title;
        protected TextView goalData;
        protected ProgressBar progressbar;

        public GoalViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.goal_title);
            goalData = (TextView) view.findViewById(R.id.goal_info);
            progressbar = (ProgressBar) view.findViewById(R.id.goal_progressbar);
        }
    }
}
