package com.jetsetter.hoteldashboard.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.List;

/**
 * Created by gim on 8/7/17.
 */
@Document(collection = "Hotel")
public class Hotel implements Serializable {

    @Id
    @JsonProperty("hotelId")
    private String hotelId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("countryCode")
    private String countryCode;

    @JsonProperty("zipcode")
    private int zipcode;

    @JsonProperty("address")
    private String address;

    @JsonProperty("rooms")
    private List<Room> rooms;

    public List<Room> getRooms() {
        return rooms;
    }

    public void setRooms(List<Room> rooms) {
        this.rooms = rooms;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getHotelId() {
        return hotelId;
    }

    public void setHotelId(String hotelId) {
        this.hotelId = hotelId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public int getZipcode() {
        return zipcode;
    }

    public void setZipcode(int zipcode) {
        this.zipcode = zipcode;
    }
}
