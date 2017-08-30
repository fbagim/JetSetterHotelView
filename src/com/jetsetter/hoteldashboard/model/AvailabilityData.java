package com.jetsetter.hoteldashboard.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class AvailabilityData implements Serializable{
    private String hotelCode;
    private List<RoomAvailability> roomAvailabilities;
    private Date fromDate;
    private Date toDate;

    public String getHotelCode() {
        return hotelCode;
    }

    public void setHotelCode(String hotelCode) {
        this.hotelCode = hotelCode;
    }

    public List<RoomAvailability> getRoomAvailabilities() {
        return roomAvailabilities;
    }

    public void setRoomAvailabilities(List<RoomAvailability> roomAvailabilities) {
        this.roomAvailabilities = roomAvailabilities;
    }

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
}
