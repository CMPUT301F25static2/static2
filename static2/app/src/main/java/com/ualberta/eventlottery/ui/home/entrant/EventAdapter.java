package com.ualberta.eventlottery.ui.home.entrant;

import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.annotation.VisibleForTesting;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.idling.CountingIdlingResource;

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

/*
 * Adapter class for displaying a list of events in a RecyclerView for entrants
 * Handles the binding of event data to view holders, managers user interactions
 * for registration and withdrawal, and displays real-time waitlist count
 */
public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder>{
    @VisibleForTesting
    public static CountingIdlingResource idlingResource = new CountingIdlingResource("EventAdapter-RegistrationAction");
    private static SimpleDateFormat sdfWithoutYear = new SimpleDateFormat("MMM dd", Locale.CANADA);
    private static SimpleDateFormat sdfWithYear = new SimpleDateFormat("MMM dd, yyyy", Locale.CANADA);

    private static String WAIT_SYMBOL = "\u231B";
    private static String NOT_ALLOWED_SYMBOL = "\u26D4";

    private static String BTN_ACTION_TEXT_REGISTER = "Register";
    private static String BTN_ACTION_TEXT_WITHDRAW = "Withdraw";

    private List<Event> eventList;
    private OnEventListener onEventListener;

    /**
     * Constructs an EventAdapter with a specified eventList.
     * @param eventList the list of events to display
     */
    public EventAdapter(List<Event> eventList, OnEventListener onEventListener){
        this.eventList = eventList;
        this.onEventListener = onEventListener;
    }

    /**
     * Updates the adapter with a new event and notifies the observer of any changes.
     * @param newEvents the list of new events to display
     */
    public void updateEvents(List<Event> newEvents){
        this.eventList = newEvents;
        notifyDataSetChanged();
    }

    /**
     * Creates a new ViewHolder based on the event item layout.
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_item, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Binds event data to a specified position in the ViewHolder
     * Sets the event details including title, waitlist count, status, dates, and time.
     * Configures action button based on user registration status and sets up the listeners
     * for withdrawals and registrations.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
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
                idlingResource.decrement();
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("EventLottery", "failed to find registration", e);
                holder.btnActionText.setText(NOT_ALLOWED_SYMBOL);
                idlingResource.decrement();
            }
        };

        holder.btnAction.setOnClickListener(v -> {
            idlingResource.increment();
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
        idlingResource.increment();

        registrationRepository.findRegistrationByEventAndUser(event.getId(), UserManager.getCurrentUserId(), callback);

        holder.itemView.setOnClickListener(v -> {
            if (onEventListener != null) {
                onEventListener.onEventClick(event);
            }
        });
    }

    /**
     * Returns the text for the number of entrants.
     *
     * @param waitListCount   The number of users on the waitlist.
     * @param maxWaitListSize The maximum size of the waitlist.
     * @return The formatted string for the number of entrants.
     */
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

    /**
     * Returns the formatted date range for the event.
     *
     * @param event The event.
     * @return The formatted date range.
     */
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

    /**
     * Returns the formatted session start time for the event.
     *
     * @param event The event.
     * @return The formatted session start time.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private String getSessionStartTimeText(Event event){
        if (event.getDailyStartTime() == null) {
            return "TBD AM/PM";
        }
        LocalTime sessionStartTime = event.getDailyStartTime();
        return sessionStartTime.format(DateTimeFormatter.ofPattern("hh:mm a"));
    }

    /**
     * Returns the total number of events in the adapter
     * @return the size of the event list
     */
    @Override
    public int getItemCount(){
        return eventList.size();
    }

    /**
     * ViewHolder class for an event item in the containing Recycler View
     */
    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView eventTitle, entrantsNumber, eventFromTo, eventStatus, eventSessionStartTime, btnActionText;
        LinearLayout btnAction;
        /**
         * Constructs a new ViewHolder.
         *
         * @param itemView The view that you inflated in
         *                 {@link EventAdapter#onCreateViewHolder(ViewGroup, int)}
         */
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

    /**
     * Interface for listening to event clicks.
     */
    public interface OnEventListener {
        /**
         * Called when an event is clicked.
         *
         * @param event The clicked event.
         */
        void onEventClick(Event event);
    }
}
