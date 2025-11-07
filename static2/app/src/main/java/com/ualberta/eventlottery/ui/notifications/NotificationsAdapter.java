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

/**
 * RecyclerView adapter for displaying a list of notifications.
 * Uses {@link DiffUtil} for efficient updates and supports click interactions.
 */
public class NotificationsAdapter extends ListAdapter<NotificationModel, NotificationsAdapter.NotificationViewHolder> {

    /**
     * Listener interface for handling notification click events.
     */
    public interface OnNotificationClickListener {
        void onNotificationClick(NotificationModel notification);
    }

    private final OnNotificationClickListener clickListener;

    /**
     * Creates a new NotificationsAdapter with a click listener.
     *
     * @param clickListener listener to handle item click events
     */
    public NotificationsAdapter(OnNotificationClickListener clickListener) {
        super(DIFF_CALLBACK);
        this.clickListener = clickListener;
    }

    /**
     * Compares notification items to determine if updates are needed.
     */
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

    /**
     * Inflates a new item view for the RecyclerView.
     */
    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_item, parent, false);
        return new NotificationViewHolder(view);
    }

    /**
     * Binds a notification to the corresponding view holder.
     */
    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        NotificationModel notification = getItem(position);
        holder.bind(notification, clickListener);
    }

    /**
     * ViewHolder that represents a single notification item.
     * Handles basic UI binding and click actions.
     */
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

        /**
         * Binds a single {@link NotificationModel} to the view.
         * Truncates long text and adjusts appearance based on read status.
         *
         * @param notification the notification data to display
         * @param listener     click listener for the notification item
         */
        public void bind(NotificationModel notification, OnNotificationClickListener listener) {
            titleText.setText(notification.getTitle());

            // Truncate body if too long
            String body = notification.getBody();
            if (body.length() > 100) {
                body = body.substring(0, 100) + "...";
            }
            bodyText.setText(body);

            // Dim the card if the notification is read
            if (notification.getIsRead()) {
                card.setAlpha(0.7f);
            } else {
                card.setAlpha(1.0f);
            }

            card.setOnClickListener(v -> listener.onNotificationClick(notification));
        }
    }
}
