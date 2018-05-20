package com.stefankussmaul.activitylog.activity;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.stefankussmaul.activitylog.R;
import com.stefankussmaul.activitylog.content.DateUtil;
import com.stefankussmaul.activitylog.content.Goal;

import org.w3c.dom.Text;

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
        holder.target.setText(goal.getTargetString()); // TODO
        holder.progressbar.setProgress((int) goal.getPercentProgress());//, true);

        // calculate how much time is left and set coloring/text based on various cases
        long ms_left = goal.getEndDate().getTime() - System.currentTimeMillis();
        if (ms_left < DateUtil.HOUR_MS) {
            holder.timeLeft.setText((ms_left / DateUtil.MINUTE_MS) + " Minutes");
            holder.timeLeft.setTextColor(Color.RED);
        } else if (ms_left < 4 * DateUtil.HOUR_MS) {
            holder.timeLeft.setText((ms_left / DateUtil.HOUR_MS) + " Hours");
            holder.timeLeft.setTextColor(Color.YELLOW);
        } else if (ms_left < DateUtil.DAY_MS) {
            holder.timeLeft.setText("Today");
            holder.timeLeft.setTextColor(Color.GREEN);
        } else if (ms_left < 2 * DateUtil.DAY_MS) {
            holder.timeLeft.setText("Tomorrow");
            holder.timeLeft.setTextColor(Color.GREEN);
        } else {
            holder.timeLeft.setText((ms_left / DateUtil.DAY_MS) + " Days");
            holder.timeLeft.setTextColor(Color.GREEN);
        }

        // set percentage progress and fraction below progress bar
        holder.percentage.setText(goal.getPercentProgress() + "%");  // TODO: IMPROVE
        String progress_str = goal.getProgress() + " / " +
                (goal.getGoalType() == Goal.GoalType.GOAL_TIME ? (goal.getTarget() / DateUtil.HOUR_MS) :
                goal.getTarget());
        holder.progress.setText(progress_str);
    }

    @Override
    public int getItemCount() {
        return goals.size();
    }

    class GoalViewHolder extends RecyclerView.ViewHolder {
        protected TextView title, target, timeLeft, goalData, percentage, progress;
        protected ProgressBar progressbar;

        public GoalViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.goal_title);
            target = (TextView) view.findViewById(R.id.goal_target);
            timeLeft = (TextView) view.findViewById(R.id.goal_timeleft);
            goalData = (TextView) view.findViewById(R.id.goal_info);
            progressbar = (ProgressBar) view.findViewById(R.id.goal_progressbar);
            percentage = (TextView) view.findViewById(R.id.goal_percentage);
            progress = (TextView) view.findViewById(R.id.goal_progress);
        }
    }
}
