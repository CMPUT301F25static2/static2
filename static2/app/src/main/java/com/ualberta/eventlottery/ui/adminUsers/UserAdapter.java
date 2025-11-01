/*package com.ualberta.eventlottery.ui.adminUsers.

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ualberta.eventlottery.admin.User;
import com.ualberta.eventlottery.event.Event;
import com.ualberta.eventlottery.ui.home.entrant.EventAdapter;
import com.ualberta.static2.R;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder>{
    private List<User> userList;
/*
    public UserAdapter(List<Event> userList){
        this.userList = userList;
    }

    public void updateEvents(List<Event> newEvents){
        this.userList = newEvents;
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
        Event event = userList.get(position);

        holder.eventTitle.setText(event.getTitle());
        holder.eventDetails.setText(event.getEventDetails());
        holder.timeFrame.setText(event.getTimeFrame());
        holder.eventStaus.setText(event.getEventStatus());
    }

    @Override
    public int getItemCount(){
        return userList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView eventTitle, eventDetails, timeFrame, eventStaus;
        public ViewHolder(@NonNull View itemView){
            super(itemView);
            eventTitle = itemView.findViewById(R.id.eventTitle);
            eventDetails=  itemView.findViewById(R.id.eventDetails);
            timeFrame = itemView.findViewById(R.id.eventTimeFrame);
            eventStaus = itemView.findViewById(R.id.eventStatus);
        }
    }


}

 */
