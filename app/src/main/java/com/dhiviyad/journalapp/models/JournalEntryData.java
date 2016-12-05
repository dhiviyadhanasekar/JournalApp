package com.dhiviyad.journalapp.models;


import com.dhiviyad.journalapp.utils.DateUtils;

import java.io.Serializable;

/**
 * Created by dhiviyad on 12/3/16.
 */

public class JournalEntryData implements Serializable{
    private Long id;
    private Double latitude;
    private Double longitude;
    private String countryName;
    private String stateName;
    private String cityName;
    private String weather;
    private String picture;
    private String time;
    private String date;
    private String description;

    public JournalEntryData() {
        time = DateUtils.getCurrentTime();
        date = DateUtils.getCurrentDate();
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public String getCountryName() {
        return countryName;
    }

    public String getStateName() {
        return stateName;
    }

    public String getCityName() {
        return cityName;
    }

    public String getWeather() {
        return weather;
    }

    public String getPicture() {
        return picture;
    }

    public String getDescription() {
        return description;
    }
}
