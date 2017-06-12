package com.stefankussmaul.activitylog.content;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.stefankussmaul.activitylog.R;

import java.util.List;

/**
 * Created by Stefan on 6/1/2017.
 */

public class LogEntryAdapter extends RecyclerView.Adapter<LogEntryAdapter.LogEntryViewHolder> {

    // list of LogEntries to be displayed in the RecyclerView
    private List<LogEntry> displayedLogs;
    // used to inflate views for LogEntries
    private LayoutInflater inflater;
    private LogEntryListener mListener;

    public interface LogEntryListener {
        // fired when edit button is clicked on an entry. Passes LogEntry to be edited
        void onEditLogEntry(LogEntry toEdit);
        // fired when delete button is clicked on an entry. Passes LogEntry to be deleted
        void onDeleteLogEntry(LogEntry toDelete);
    }

    public void setLogEntryListener(LogEntryListener listener) {
        mListener = listener;
    }

    // todo: could this be dangerous? make copy of logs?
    public LogEntryAdapter(Context context, List<LogEntry> logsToDisplay, LogEntryListener listener) {
        inflater = LayoutInflater.from(context);
        this.displayedLogs = logsToDisplay;
        mListener = listener;
    }

    @Override // inflates a ViewHolder so it can be populated in onBind
    public LogEntryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View log_entry_view = inflater.inflate(R.layout.logentry_display_layout, parent, false);
        return new LogEntryViewHolder(log_entry_view);
    }

    @Override // binds the data from the LogEntry at given position to the holder that contains the view itself
    public void onBindViewHolder(final LogEntryViewHolder holder, final int position) {
        LogEntry binded_log = displayedLogs.get(position);
        holder.logName.setText(binded_log.getActivityName());
        holder.logDuration.setText(DateUtil.format(binded_log.getDuration()));
        holder.logDate.setText(DateUtil.format(binded_log.getDate()));
        // notify listener when button is clicked
        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            mListener.onEditLogEntry(displayedLogs.get(position));
            }
        });
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onDeleteLogEntry(displayedLogs.get(position));
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.clicked = !holder.clicked;
                if (holder.clicked) {
                    holder.itemView.setBackgroundColor(Color.GRAY);
                } else {
                    holder.itemView.setBackgroundColor(Color.TRANSPARENT);
                }
            }
        });
     // todo: color background of holder when touched
//        holder.itemView.setBackgroundColor(Color.GRAY);
    }

    @Override
    public int getItemCount() {
        return displayedLogs.size();
    }

    public class LogEntryViewHolder extends RecyclerView.ViewHolder {

        private TextView logName;
        private TextView logDate;
        private TextView logDuration;
        private ImageButton editButton;
        private ImageButton deleteButton;
        private boolean clicked;

        public LogEntryViewHolder(View itemView) {
            super(itemView);
            logName = (TextView) itemView.findViewById(R.id.log_name);
            logDate = (TextView) itemView.findViewById(R.id.log_date);
            logDuration = (TextView) itemView.findViewById(R.id.log_duration);
            editButton = (ImageButton) itemView.findViewById(R.id.edit_entry);
            deleteButton = (ImageButton) itemView.findViewById(R.id.delete_entry);
        }
    }
}
