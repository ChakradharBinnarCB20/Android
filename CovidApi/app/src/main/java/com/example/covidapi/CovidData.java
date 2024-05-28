package com.example.covidapi;

import com.google.gson.annotations.SerializedName;

public class CovidData {
    @SerializedName("summary")
    private Summary summary;

    public Summary getSummary() {
        return summary;
    }
}