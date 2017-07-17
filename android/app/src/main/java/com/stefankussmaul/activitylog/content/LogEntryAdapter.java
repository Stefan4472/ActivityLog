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
 * Adapter to visually display a list of LogEntry objects in a RecyclerView. Communicates via the
 * LogEntryListener interface, which fires events for relevant actions. Background coloring (i.e.
 * different color for selected and deselected) should be managed by an outside class and use
 * LogEntryViewHolder.setClicked(). Each LogEntry instance has a trash-icon button which fires
 * the onDeleteLogEntry callback. The actual delete action must also be handled by an outside class.
 */

public class LogEntryAdapter extends RecyclerView.Adapter<LogEntryAdapter.LogEntryViewHolder> {

    // list of LogEntries to be displayed in the RecyclerView
    private List<LogEntry> displayedLogs;
    // used to inflate views for LogEntries
    private LayoutInflater inflater;
    private LogEntryListener mListener;

    // callback interface. Each callback passes the index of the selected LogEntry in the Adapter's
    // internal list, as well as the actual LogEntry instance.
    public interface LogEntryListener {
        // fired when user selects an entry from the list
        void onSelectLogEntry(int index, LogEntry selected);
        // fired when user deselects the selected entry
        void onDeselectLogEntry(int index, LogEntry toEdit);
        // fired when delete button is clicked on an entry. Passes the index of the element in the
        // list to be removed
        void onDeleteLogEntry(int index, LogEntry toDelete);
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
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onDeleteLogEntry(position, displayedLogs.get(position));
            }
        });
        // entire item is clicked
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.setClicked(!holder.clicked);
                // handle selecting LogEntry
                if (holder.clicked) {
                    mListener.onSelectLogEntry(position, displayedLogs.get(position));
                } else {
                    mListener.onDeselectLogEntry(position, displayedLogs.get(position));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return displayedLogs.size();
    }

    public LogEntry get(int index) {
        return displayedLogs.get(index);
    }

    public static void setClicked(View v, boolean clicked) {
        v.setBackgroundColor(clicked ? Color.GRAY : Color.TRANSPARENT);
    }

    class LogEntryViewHolder extends RecyclerView.ViewHolder {

        private TextView logName;
        private TextView logDate;
        private TextView logDuration;
        private ImageButton deleteButton;
        private boolean clicked;

        LogEntryViewHolder(View itemView) {
            super(itemView);
            logName = (TextView) itemView.findViewById(R.id.log_name);
            logDate = (TextView) itemView.findViewById(R.id.log_date);
            logDuration = (TextView) itemView.findViewById(R.id.log_duration);
            deleteButton = (ImageButton) itemView.findViewById(R.id.delete_entry);
        }

        // set the clicked state. Sets the background color of the view
        public void setClicked(boolean clicked) {
            this.clicked = clicked;
            itemView.setBackgroundColor(clicked ? Color.GRAY : Color.TRANSPARENT);
        }
    }
}
