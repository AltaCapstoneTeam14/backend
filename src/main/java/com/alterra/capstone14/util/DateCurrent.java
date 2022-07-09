package com.alterra.capstone14.util;

import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;

public class DateCurrent {
    public String getMonday(){
        LocalDate now = new LocalDate();
        return now.withDayOfWeek(DateTimeConstants.MONDAY).toString();
    }

    public int getDay(){
        LocalDate now = new LocalDate();
        return now.getDayOfWeek();
    }
}
