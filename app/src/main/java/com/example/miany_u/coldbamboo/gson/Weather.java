package com.example.miany_u.coldbamboo.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by miany_u on 2019/11/12.
 */

public class Weather
{
    public String status;
    public Basic basic;
    public AQI aqi;
    public Now now;
    public Suggestion suggestion;

    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;
}
