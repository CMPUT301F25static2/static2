package com.ualberta.eventlottery.ui.notifications;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;
import com.ualberta.eventlottery.notification.NotificationModel;
import com.ualberta.static2.R;

import java.util.ArrayList;
import java.util.List;

public class NotificationsListAdapter extends BaseAdapter {

    public interface OnNotificationClickListener {
        void onNotificationClick(NotificationModel notification);
        void onNotificationClose(NotificationModel notification);
    }

    private final List<NotificationModel> notifications = new ArrayList<>();
    private final LayoutInflater inflater;
    private final OnNotificationClickListener clickListener;



    public NotificationsListAdapter(Context context, OnNotificationClickListener listener) {
        inflater = LayoutInflater.from(context);
        this.clickListener = listener;
    }

    public void setNotifications(List<NotificationModel> newNotifications) {
        notifications.clear();
        if (newNotifications != null) {
            notifications.addAll(newNotifications);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return notifications.size();
    }

    @Override
    public NotificationModel getItem(int position) {
        return notifications.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.notification_item, parent, false);
            holder = new ViewHolder();
            holder.titleText = convertView.findViewById(R.id.notification_title);
            holder.bodyText = convertView.findViewById(R.id.notification_body);
            holder.card = convertView.findViewById(R.id.notification_card);
            holder.closeButton = convertView.findViewById(R.id.close_button);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        NotificationModel notification = getItem(position);

        holder.titleText.setText(notification.getTitle());

        String body = notification.getBody();
        if (body.length() > 100) {
            body = body.substring(0, 100) + "...";
        }
        holder.bodyText.setText(body);

        holder.card.setAlpha(notification.getIsRead() ? 0.7f : 1.0f);

        holder.card.setOnClickListener(v -> clickListener.onNotificationClick(notification));
        holder.closeButton.setOnClickListener(v -> {
            v.animate()
                    .scaleX(0.7f)
                    .scaleY(0.7f)
                    .setDuration(120)
                    .withEndAction(() -> {
                        v.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(120);

                        clickListener.onNotificationClose(notification); // Perform action after animation
                    });
        });

        return convertView;
    }

    static class ViewHolder {
        TextView titleText;
        TextView bodyText;
        MaterialCardView card;
        ImageView closeButton;
    }
}
