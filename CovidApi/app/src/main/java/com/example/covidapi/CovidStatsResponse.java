package com.example.covidapi;

import com.google.gson.annotations.SerializedName;

public class CovidStatsResponse {
    @SerializedName("data")
    private CovidData data;

    public CovidData getData() {
        return data;
    }
}
