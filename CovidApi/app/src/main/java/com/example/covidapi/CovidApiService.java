package com.example.covidapi;

import retrofit2.Call;
import retrofit2.http.GET;

public interface CovidApiService {
    @GET("covid19-in/stats/latest")
    Call<CovidStatsResponse> getCovidStats();
}