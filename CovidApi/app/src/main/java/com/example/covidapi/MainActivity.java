package com.example.covidapi;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private TextView totalTextView;
    private TextView confirmedCasesIndianTextView;
    private CovidApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        totalTextView = findViewById(R.id.totalTextView);
        confirmedCasesIndianTextView = findViewById(R.id.confirmedCasesIndianTextView);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.rootnet.in/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(CovidApiService.class);

        fetchDataFromAPI();
    }

    private void fetchDataFromAPI() {
        Call<CovidStatsResponse> call = apiService.getCovidStats();
        call.enqueue(new Callback<CovidStatsResponse>() {
            @Override
            public void onResponse(Call<CovidStatsResponse> call, Response<CovidStatsResponse> response) {
                if (response.isSuccessful()) {
                    CovidStatsResponse covidStatsResponse = response.body();
                    if (covidStatsResponse != null) {
                        Summary summary = covidStatsResponse.getData().getSummary();
                        int total = summary.getTotal();
                        int confirmedCasesIndian = summary.getConfirmedCasesIndian();
                        totalTextView.setText("Total: " + total);
                        confirmedCasesIndianTextView.setText("Confirmed Cases Indian: " + confirmedCasesIndian);
                    }
                } else {
                    totalTextView.setText("Failed to fetch data");
                    confirmedCasesIndianTextView.setText("");
                }
            }

            @Override
            public void onFailure(Call<CovidStatsResponse> call, Throwable t) {
                totalTextView.setText("Error: " + t.getMessage());
                confirmedCasesIndianTextView.setText("");
            }
        });
    }
}