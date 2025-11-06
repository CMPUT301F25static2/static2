package com.ualberta.eventlottery.ui.notifications;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.ualberta.eventlottery.notification.NotificationModel;
import com.ualberta.static2.R;

public class NotificationsAdapter extends ListAdapter<NotificationModel, NotificationsAdapter.NotificationViewHolder> {

    public interface OnNotificationClickListener {
        void onNotificationClick(NotificationModel notification);
    }

    private final OnNotificationClickListener clickListener;

    public NotificationsAdapter(OnNotificationClickListener clickListener) {
        super(DIFF_CALLBACK);
        this.clickListener = clickListener;
    }

    private static final DiffUtil.ItemCallback<NotificationModel> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<NotificationModel>() {
                @Override
                public boolean areItemsTheSame(@NonNull NotificationModel oldItem, @NonNull NotificationModel newItem) {
                    return oldItem.getNotificationId() != null &&
                            oldItem.getNotificationId().equals(newItem.getNotificationId());
                }

                @Override
                public boolean areContentsTheSame(@NonNull NotificationModel oldItem, @NonNull NotificationModel newItem) {
                    return oldItem.getTitle().equals(newItem.getTitle()) &&
                            oldItem.getBody().equals(newItem.getBody()) &&
                            oldItem.getIsRead() == newItem.getIsRead();
                }
            };

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_item, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        NotificationModel notification = getItem(position);
        holder.bind(notification, clickListener);
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleText;
        private final TextView bodyText;
        private final MaterialCardView card;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.notification_title);
            bodyText = itemView.findViewById(R.id.notification_body);
            card = itemView.findViewById(R.id.notification_card);
        }

        public void bind(NotificationModel notification, OnNotificationClickListener listener) {
            titleText.setText(notification.getTitle());

            // Truncate body if too long
            String body = notification.getBody();
            if (body.length() > 100) {
                body = body.substring(0, 100) + "...";
            }
            bodyText.setText(body);

            // Optional: change card style if unread
            if (notification.getIsRead()) {
                card.setAlpha(0.7f);
            } else {
                card.setAlpha(1.0f);
            }

            card.setOnClickListener(v -> listener.onNotificationClick(notification));
        }
    }
}
