package com.ualberta.eventlottery.ui.organizer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ualberta.eventlottery.model.Event;
import com.ualberta.static2.R;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class OrganizerEventAdapter extends BaseAdapter {
    private Context context;
    private List<Event> eventList;
    private LayoutInflater inflater;
    private SimpleDateFormat dateFormat;

    public OrganizerEventAdapter(Context context, List<Event> eventList) {
        this.context = context;
        this.eventList = eventList;
        this.inflater = LayoutInflater.from(context);
        this.dateFormat = new SimpleDateFormat("h:mma, MMM dd, yyyy", Locale.getDefault());
    }

    @Override
    public int getCount() {
        return eventList != null ? eventList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return eventList != null ? eventList.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.organizer_item_event, parent, false);
            holder = new ViewHolder();
            holder.tv_event_title = convertView.findViewById(R.id.tv_event_title);
            holder.tv_event_entrants_number = convertView.findViewById(R.id.tv_event_entrants_number);
            holder.tv_event_end_time = convertView.findViewById(R.id.tv_event_end_time);
            holder.tv_event_status = convertView.findViewById(R.id.tv_event_status);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Event event = eventList.get(position);

        holder.tv_event_title.setText(event.getTitle());

        String entrantsNumber = String.format("Entrants: %d/%d", event.getConfirmedCount(), event.getMaxAttendees());
        holder.tv_event_entrants_number.setText(entrantsNumber);

        if (event.getEndTime() != null) {
            String formattedTime = "End: " + dateFormat.format(event.getEndTime());
            holder.tv_event_end_time.setText(formattedTime);
        } else {
            holder.tv_event_end_time.setText("End: TBD");
        }

        if (event.getStatus() != null) {
            holder.tv_event_status.setText(event.getStatus().toString());
        }

        return convertView;
    }

    static class ViewHolder {
        TextView tv_event_title;
        TextView tv_event_entrants_number;
        TextView tv_event_end_time;
        TextView tv_event_status;
    }
}
