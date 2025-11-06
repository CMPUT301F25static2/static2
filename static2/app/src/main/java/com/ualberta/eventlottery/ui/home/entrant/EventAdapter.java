package com.ualberta.eventlottery.ui.home.entrant;

import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.ualberta.eventlottery.model.EntrantRegistrationStatus;
import com.ualberta.eventlottery.model.Event;
import com.ualberta.eventlottery.model.Registration;
import com.ualberta.eventlottery.repository.RegistrationRepository;
import com.ualberta.eventlottery.utils.UserManager;
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

    private static String WAIT_SYMBOL = "\u231B";
    private static String NOT_ALLOWED_SYMBOL = "\u26D4";

    private static String BTN_ACTION_TEXT_REGISTER = "Register";
    private static String BTN_ACTION_TEXT_WITHDRAW = "Withdraw";

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


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RegistrationRepository registrationRepository = RegistrationRepository.getInstance();
        Event event = eventList.get(position);

        holder.eventTitle.setText(event.getTitle());
        holder.entrantsNumber.setText(getEntrantsText(0, event.getMaxWaitListSize()));
        registrationRepository.watchRegistrationCountByStatus(event.getId(), EntrantRegistrationStatus.WAITING, new RegistrationRepository.CountCallback() {
            @Override
            public void onSuccess(int count) {
                holder.entrantsNumber.setText(getEntrantsText(count, event.getMaxWaitListSize()));
            }


            @Override
            public void onFailure(Exception e) {
                Log.e("EventLottery", "failure while watching count of waitlisted entrants", e);
            }
        });

        holder.eventStatus.setText(event.getRegistrationStatus().toString());
        holder.eventFromTo.setText(getFromToText(event));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            holder.eventSessionStartTime.setText(getSessionStartTimeText(event));
        }
        holder.eventSessionStartTime.setText(getSessionStartTimeText(event));

        holder.btnActionText.setText(WAIT_SYMBOL);
        RegistrationRepository.RegistrationCallback callback = new RegistrationRepository.RegistrationCallback() {
            @Override
            public void onSuccess(Registration registration) {
                if (registration != null) {
                    holder.btnActionText.setText(BTN_ACTION_TEXT_WITHDRAW);
                } else {
                    holder.btnActionText.setText(BTN_ACTION_TEXT_REGISTER);
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("EventLottery", "failed to find registration", e);
                holder.btnActionText.setText(NOT_ALLOWED_SYMBOL);
            }
        };

        holder.btnAction.setOnClickListener(v -> {
            String btnActionText = holder.btnActionText.getText().toString();
            String userId = UserManager.getCurrentUserId();
            if (btnActionText.compareTo(BTN_ACTION_TEXT_REGISTER) == 0) {
                holder.btnActionText.setText(WAIT_SYMBOL);
                registrationRepository.registerUser(event.getId(), userId, callback);
            } else if (btnActionText.compareTo(BTN_ACTION_TEXT_WITHDRAW) == 0){
                holder.btnActionText.setText(WAIT_SYMBOL);
                registrationRepository.unregisterUser(event.getId(), userId, callback);
            }
        });
        registrationRepository.findRegistrationByEventAndUser(event.getId(), UserManager.getCurrentUserId(), callback);
    }

    private String getEntrantsText(int waitListCount, int maxWaitListSize) {
        StringBuffer buffer = new StringBuffer();
        if (waitListCount >= 0) {
            buffer.append(waitListCount);
        } else {
            buffer.append("-");
        }
        buffer.append("/");
        if (maxWaitListSize > 0) {
            buffer.append(maxWaitListSize);
        } else {
            buffer.append("unlimited");
        }
        return buffer.toString();
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
        TextView eventTitle, entrantsNumber, eventFromTo, eventStatus, eventSessionStartTime, btnActionText;
        LinearLayout btnAction;
        public ViewHolder(@NonNull View itemView){
            super(itemView);
            eventTitle = itemView.findViewById(R.id.tv_event_title);
            entrantsNumber = itemView.findViewById(R.id.tv_event_entrants_number);
            eventStatus = itemView.findViewById(R.id.tv_event_status);
            eventFromTo = itemView.findViewById(R.id.tv_event_from_to);
            eventSessionStartTime = itemView.findViewById(R.id.tv_event_session_start_time);
            btnAction = itemView.findViewById(R.id.btn_action);
            btnActionText = itemView.findViewById(R.id.btn_action_text);
        }
    }
}
