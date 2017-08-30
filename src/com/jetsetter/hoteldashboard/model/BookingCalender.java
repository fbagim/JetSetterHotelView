package com.jetsetter.hoteldashboard.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by gim on 8/10/17.
 */

public class BookingCalender implements Serializable {

    @JsonProperty("calenderId")
    private String calenderId;
    @JsonProperty("fromDate")
    private Date fromDate;
    @JsonProperty("toDate")
    private Date toDate;
    @JsonProperty("noOFDays")
    private int noOFDays;
    @JsonProperty("isBooked")
    private boolean isBooked;

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public String getCalenderId() {
        return calenderId;
    }

    public void setCalenderId(String calenderId) {
        this.calenderId = calenderId;
    }

    public int getNoOFDays() {
        return noOFDays;
    }

    public void setNoOFDays(int noOFDays) {
        this.noOFDays = noOFDays;
    }

    public boolean isBooked() {
        return isBooked;
    }

    public void setBooked(boolean booked) {
        isBooked = booked;
    }
}
