package com.ualberta.eventlottery.model;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@RequiresApi(api = Build.VERSION_CODES.O)
public enum TimeRange {
    MORNING("Morning", LocalTime.of(6, 0, 0), LocalTime.of(12, 0, 0)),
    AFTERNOON("Afternoon", LocalTime.of(12 , 0, 0), LocalTime.of(18, 0, 0)),
    EVENING("Evening", LocalTime.of(18, 0, 0), LocalTime.of(22, 0, 0));

    private LocalTime start, end;
    private String fullDisplayName;
    private String shortDisplayName;
    TimeRange(String displayName, LocalTime from, LocalTime to) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
        StringBuffer sb = new StringBuffer();
        sb.append(displayName)
                .append("(")
                .append(from.format(formatter))
                .append(" - ")
                .append(to.format(formatter));
        this.fullDisplayName = sb.toString();
        this.shortDisplayName = displayName;
        this.start = from;
        this.end = to;
    }

    public boolean isInRange(LocalTime time) {
        if (time.equals(start)) {
            return true;
        }
        return time.isAfter(start) && time.isBefore(end);
    }

    public String getFullDisplayName() {
        return fullDisplayName;
    }

    public String getShortDisplayName() {
        return shortDisplayName;
    }

}
