package com.example.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Calendar;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CalendarUtil {

    private Calendar calendar;

    public long getTimeInMillis() {
        return this.calendar.getTimeInMillis();
    }

    /** 取整 */
    public CalendarUtil round() {
        this.calendar.set(Calendar.HOUR_OF_DAY, 0);
        this.calendar.set(Calendar.MINUTE, 0);
        this.calendar.set(Calendar.SECOND, 0);
        this.calendar.set(Calendar.MILLISECOND, 0);
        return this;
    }

    public CalendarUtil addYear(int value) {
        this.calendar.add(Calendar.YEAR, value);
        return this;
    }

    public CalendarUtil addMonth(int value) {
        this.calendar.add(Calendar.MONTH, value);
        return this;
    }

    public CalendarUtil addWeek(int value) {
        this.calendar.add(Calendar.WEEK_OF_YEAR, value);
        return this;
    }

    public CalendarUtil addDay(int value) {
        this.calendar.add(Calendar.DATE, value);
        return this;
    }

    public CalendarUtil addHour(int value) {
        this.calendar.add(Calendar.HOUR_OF_DAY, value);
        return this;
    }

    public CalendarUtil addMinute(int value) {
        this.calendar.add(Calendar.MINUTE, value);
        return this;
    }

    public CalendarUtil addSecond(int value) {
        this.calendar.add(Calendar.SECOND, value);
        return this;
    }

    public CalendarUtil addMillisecond(int value) {
        this.calendar.add(Calendar.MILLISECOND, value);
        return this;
    }

}
