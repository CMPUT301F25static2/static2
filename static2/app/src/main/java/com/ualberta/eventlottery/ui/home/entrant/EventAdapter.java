package com.ualberta.eventlottery.ui.home.entrant;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ualberta.eventlottery.model.Event;
import com.ualberta.static2.R;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder>{
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
            holder.entrantsNumber.setText("Entrants: " + event.getWaitListCount() + "/" + event.getMaxWaitListSize());
        } else {
            holder.entrantsNumber.setText("Entrants: " + event.getWaitListCount() + "/unlimited");
        }
        holder.eventStaus.setText(event.getStatus().toString());
    }

    @Override
    public int getItemCount(){
        return eventList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView eventTitle, entrantsNumber, timeFrame, eventStaus;
        public ViewHolder(@NonNull View itemView){
            super(itemView);
            eventTitle = itemView.findViewById(R.id.tv_event_title);
            entrantsNumber = itemView.findViewById(R.id.tv_event_entrants_number);
            eventStaus = itemView.findViewById(R.id.tv_event_status);
        }
    }
}
