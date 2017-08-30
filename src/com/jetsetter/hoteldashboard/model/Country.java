package com.jetsetter.hoteldashboard.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Document(collection = "Country")
public class Country implements Serializable {

    @Id
    @JsonProperty("countryIso")
    private String countryIso;

    @JsonProperty("dialCode")
    private String dialCode;

    @JsonProperty("countryName")
    private String countryName;


    public String getCountryIso() {
        return countryIso;
    }

    public void setCountryIso(String countryIso) {
        this.countryIso = countryIso;
    }

    public String getDialCode() {
        return dialCode;
    }

    public void setDialCode(String dialCode) {
        this.dialCode = dialCode;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }


}
