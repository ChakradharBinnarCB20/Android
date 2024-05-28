package com.example.covidapi;

import com.google.gson.annotations.SerializedName;

public class Summary {
    @SerializedName("total")
    private int total;

    @SerializedName("confirmedCasesIndian")
    private int confirmedCasesIndian;

    public int getTotal() {
        return total;
    }

    public int getConfirmedCasesIndian() {
        return confirmedCasesIndian;
    }
}
