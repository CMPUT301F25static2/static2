package com.ualberta.eventlottery.ui.adminLogs;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ualberta.eventlottery.model.NotificationLog;
import com.ualberta.static2.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Adapter for displaying notification logs in the admin logs screen.
 */
public class NotificationLogsAdapter extends RecyclerView.Adapter<NotificationLogsAdapter.LogViewHolder> {

    private List<NotificationLog> logList;
    private final OnLogClickListener listener;
    private final SimpleDateFormat dateFormat;

    public interface OnLogClickListener {
        void onLogClick(NotificationLog log);
    }

    public NotificationLogsAdapter(OnLogClickListener listener) {
        this.logList = new ArrayList<>();
        this.listener = listener;
        this.dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
    }

    public void setLogList(List<NotificationLog> logList) {
        this.logList = logList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification_log, parent, false);
        return new LogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LogViewHolder holder, int position) {
        NotificationLog log = logList.get(position);
        holder.bind(log);
    }

    @Override
    public int getItemCount() {
        return logList.size();
    }

    class LogViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvNotificationTitle;
        private final TextView tvEventTitle;
        private final TextView tvOrganizerName;
        private final TextView tvRecipientCount;
        private final TextView tvTimestamp;
        private final TextView tvBody;

        public LogViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNotificationTitle = itemView.findViewById(R.id.tvNotificationTitle);
            tvEventTitle = itemView.findViewById(R.id.tvEventTitle);
            tvOrganizerName = itemView.findViewById(R.id.tvOrganizerName);
            tvRecipientCount = itemView.findViewById(R.id.tvRecipientCount);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            tvBody = itemView.findViewById(R.id.tvNotificationBody);
        }

        public void bind(NotificationLog log) {
            tvNotificationTitle.setText(log.getTitle() != null ? log.getTitle() : "No Title");
            tvBody.setText(log.getBody() != null ? log.getBody() : "No message");
            tvEventTitle.setText(log.getEventTitle() != null ? log.getEventTitle() : "Unknown Event");
            tvOrganizerName.setText("From: " + (log.getOrganizerName() != null ? log.getOrganizerName() : "Unknown"));
            tvRecipientCount.setText("Recipients: " + log.getRecipientCount());

            if (log.getCreatedAt() != null) {
                tvTimestamp.setText(dateFormat.format(log.getCreatedAt()));
            } else {
                tvTimestamp.setText("Unknown time");
            }

            // Click to view details
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onLogClick(log);
                }
            });
        }
    }
}

