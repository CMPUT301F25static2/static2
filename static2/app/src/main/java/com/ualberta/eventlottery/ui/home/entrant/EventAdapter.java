package com.ualberta.eventlottery.ui.home.entrant;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.ualberta.eventlottery.model.Event;
import com.ualberta.static2.R;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder>{
    private static SimpleDateFormat sdfWithoutYear = new SimpleDateFormat("MMM dd", Locale.CANADA);
    private static SimpleDateFormat sdfWithYear = new SimpleDateFormat("MMM dd, yyyy", Locale.CANADA);

    private List<Event> eventList;

    public EventAdapter(List<Event> eventList){
        this.eventList = eventList;
    }

    public void updateEvents(List<Event> newEvents){
        this.eventList = newEvents;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_item, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Event event = eventList.get(position);

        holder.eventTitle.setText(event.getTitle());
        if (event.getMaxWaitListSize() > 0) {
            holder.entrantsNumber.setText(event.getWaitListCount() + "/" + event.getMaxWaitListSize());
        } else {
            holder.entrantsNumber.setText(event.getWaitListCount() + "/unlimited");
        }
        holder.eventStatus.setText(event.getRegistrationStatus().toString());
        holder.eventFromTo.setText(getFromToText(event));
        holder.eventSessionStartTime.setText(getSessionStartTimeText(event));
    }

    private String getFromToText(Event event) {
        if (event.getStartTime() == null || event.getEndTime() == null) {
            return "Dates TBD";
        }
        Calendar startTime = Calendar.getInstance();
        startTime.setTime(event.getStartTime());
        Calendar endTime = Calendar.getInstance();
        endTime.setTime(event.getEndTime());
        Calendar now = Calendar.getInstance();

        SimpleDateFormat sdf = sdfWithYear;
        if (startTime.get(Calendar.YEAR) == now.get(Calendar.YEAR) && endTime.get(Calendar.YEAR) == now.get(Calendar.YEAR)) {
            sdf = sdfWithoutYear;
        }
        return sdf.format(startTime.getTime()) + " - " + sdf.format(endTime.getTime());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String getSessionStartTimeText(Event event){
        if (event.getDailyStartTime() == null) {
            return "TBD AM/PM";
        }
        LocalTime sessionStartTime = event.getDailyStartTime();
        return sessionStartTime.format(DateTimeFormatter.ofPattern("hh:mm a"));
    }

    @Override
    public int getItemCount(){
        return eventList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView eventTitle, entrantsNumber, eventFromTo, eventStatus, eventSessionStartTime;
        public ViewHolder(@NonNull View itemView){
            super(itemView);
            eventTitle = itemView.findViewById(R.id.tv_event_title);
            entrantsNumber = itemView.findViewById(R.id.tv_event_entrants_number);
            eventStatus = itemView.findViewById(R.id.tv_event_status);
            eventFromTo = itemView.findViewById(R.id.tv_event_from_to);
            eventSessionStartTime = itemView.findViewById(R.id.tv_event_session_start_time);
        }
    }
}
