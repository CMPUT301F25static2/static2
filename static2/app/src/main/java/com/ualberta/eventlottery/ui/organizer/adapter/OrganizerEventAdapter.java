package com.ualberta.eventlottery.ui.organizer.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.ualberta.eventlottery.model.EntrantRegistrationStatus;
import com.ualberta.eventlottery.model.Event;
import com.ualberta.eventlottery.model.EventStatus;
import com.ualberta.eventlottery.model.EventRegistrationStatus;
import com.ualberta.eventlottery.repository.RegistrationRepository;
import com.ualberta.static2.R;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class OrganizerEventAdapter extends BaseAdapter {
    private Context context;
    private List<Event> eventList;
    private LayoutInflater inflater;
    private SimpleDateFormat dateFormat;
    private OnDrawButtonClickListener onDrawButtonClickListener;
    private OnExportButtonClickListener onExportButtonClickListener;
    private RegistrationRepository registrationRepository;
    private int confirmedCount;
    public interface OnDrawButtonClickListener {
        void onDrawButtonClick(Event event);
    }

    public interface OnExportButtonClickListener {
        void onExportButtonClick(Event event);
    }

    public void setOnDrawButtonClickListener(OnDrawButtonClickListener listener) {
        this.onDrawButtonClickListener = listener;
    }

    public void setOnExportButtonClickListener(OnExportButtonClickListener listener) {
        this.onExportButtonClickListener = listener;
    }


    public OrganizerEventAdapter(Context context, List<Event> eventList) {
        this.context = context;
        this.eventList = eventList;
        this.inflater = LayoutInflater.from(context);
        this.dateFormat = new SimpleDateFormat("h:mma, MMM dd, yyyy", Locale.getDefault());
        this.registrationRepository = RegistrationRepository.getInstance();
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
            holder.tv_event_start_time = convertView.findViewById(R.id.tv_event_start_time);
            holder.tv_event_end_time = convertView.findViewById(R.id.tv_event_end_time);
            holder.tv_event_status = convertView.findViewById(R.id.tv_event_status);
            holder.tv_event_registry_status = convertView.findViewById(R.id.tv_event_registry_status);
            holder.btn_draw = convertView.findViewById(R.id.btn_draw);
            holder.btn_export = convertView.findViewById(R.id.btn_export);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Event event = eventList.get(position);

        // Update registration status based on deadline before displaying
        event.updateRegistrationStatusBasedOnDeadline();

        // Update event status based on current time if needed
        if (event.getEventStatus() == EventStatus.UPCOMING && event.getStartTime() != null) {
            java.util.Date now = new java.util.Date();
            if (now.after(event.getStartTime()) && now.before(event.getEndTime())) {
                event.setEventStatus(EventStatus.ONGOING);
            } else if (now.after(event.getEndTime())) {
                event.setEventStatus(EventStatus.CLOSED);
            }
        }

        // title
        holder.tv_event_title.setText(event.getTitle());

        // event status
        if (event.getEventStatus() != null) {
            if (event.getEventStatus() == EventStatus.UPCOMING) {
                holder.tv_event_status.setText("Upcoming");
                holder.tv_event_status.setTextColor(
                        ContextCompat.getColor(context, R.color.text_secondary)
                );
                holder.tv_event_status.setBackgroundTintList(
                        ColorStateList.valueOf(ContextCompat.getColor(context, R.color.text_tertiary))
                );
            } else if (event.getEventStatus() == EventStatus.ONGOING) {
                holder.tv_event_status.setText("Ongoing");
                holder.tv_event_status.setTextColor(
                        ContextCompat.getColor(context, R.color.green_deep)
                );
                holder.tv_event_status.setBackgroundTintList(
                        ColorStateList.valueOf(ContextCompat.getColor(context, R.color.green_light))
                );
            } else if (event.getEventStatus() == EventStatus.CLOSED) {
                holder.tv_event_status.setText("Closed");
                holder.tv_event_status.setTextColor(
                        ContextCompat.getColor(context, R.color.white)
                );
                holder.tv_event_status.setBackgroundTintList(
                        ColorStateList.valueOf(ContextCompat.getColor(context, R.color.red_deep))
                );
            }

        } else {
            holder.tv_event_status.setText("Unknown");
            holder.tv_event_status.setTextColor(
                    ContextCompat.getColor(context, R.color.white)
            );
            holder.tv_event_status.setBackgroundColor(
                    ContextCompat.getColor(context, R.color.text_primary)
            );
        }

        // registration status
        if (event.getRegistrationStatus() != null) {
            if (event.getRegistrationStatus() == EventRegistrationStatus.REGISTRATION_OPEN) {
                holder.tv_event_registry_status.setText("REGISTRATION OPEN");
                holder.tv_event_registry_status.setTextColor(
                        ContextCompat.getColor(context, R.color.green_deep)
                );
            } else if (event.getRegistrationStatus() == EventRegistrationStatus.REGISTRATION_CLOSED) {
                holder.tv_event_registry_status.setText("REGISTRATION CLOSED");
                holder.tv_event_registry_status.setTextColor(
                        ContextCompat.getColor(context, R.color.red_deep)
                );
            }
        } else {
            holder.tv_event_registry_status.setText("Unknown");
            holder.tv_event_registry_status.setTextColor(ContextCompat.getColor(context, R.color.text_secondary));
        }

        confirmedCount = event.getConfirmedAttendees();
        registrationRepository.getRegistrationCountByStatus(event.getId(), EntrantRegistrationStatus.CONFIRMED, new RegistrationRepository.CountCallback() {
            @Override
            public void onSuccess(int count) {
                confirmedCount = count;
            }

            @Override
            public void onFailure(Exception e) {

            }
        });
        // entrant number
        String entrantsNumber = String.format("Entrants: %d/%d", confirmedCount, event.getMaxAttendees());
        holder.tv_event_entrants_number.setText(entrantsNumber);

        // start time
        if (event.getStartTime() != null) {
            String formattedTime = "Start: " + dateFormat.format(event.getStartTime());
            holder.tv_event_start_time.setText(formattedTime);
        } else {
            holder.tv_event_start_time.setText("Start: TBD");
        }


        // end time
        if (event.getEndTime() != null) {
            String formattedTime = "End:  " + dateFormat.format(event.getEndTime());
            holder.tv_event_end_time.setText(formattedTime);
        } else {
            holder.tv_event_end_time.setText("End:  TBD");
        }

        // set up draw button event
        if (holder.btn_draw != null) {
            holder.btn_draw.setOnClickListener(v -> {
                if (onDrawButtonClickListener != null) {
                    onDrawButtonClickListener.onDrawButtonClick(event);
                }
            });
        }

        // set up export button event
        if (holder.btn_export != null) {
            holder.btn_export.setOnClickListener(v -> {
                if (onExportButtonClickListener != null) {
                    onExportButtonClickListener.onExportButtonClick(event);
                }
            });
        }


        return convertView;
    }

    static class ViewHolder {
        TextView tv_event_title;
        TextView tv_event_entrants_number;
        TextView tv_event_start_time;
        TextView tv_event_end_time;
        TextView tv_event_status;
        TextView tv_event_registry_status;
        LinearLayout btn_draw;
        LinearLayout btn_export;


    }
}
