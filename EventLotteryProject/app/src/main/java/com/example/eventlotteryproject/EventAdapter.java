package com.example.eventlotteryproject;

import java.util.List;

public class EventAdapter {
    private List<Event> eventList;

    public EventAdapter(List<Event> eventList){
        this.eventList = eventList;
    }

    public void updateEvents(List<Event> newEvents){
        this.eventList = newEvents;
        //notifyDataSetChanged();
    }
}
