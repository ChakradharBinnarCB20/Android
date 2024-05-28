package com.example.demoapicall1;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SaleRegisterAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SaleRegisterAdapter();
        recyclerView.setAdapter(adapter);

        fetchDataFromAPI();
    }

    private void fetchDataFromAPI() {
        ProgressDialog progressDialog = ProgressDialog.show(this, "Loading", "Fetching data from API...", true);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(600, TimeUnit.SECONDS)
                .readTimeout(600, TimeUnit.SECONDS)
                .writeTimeout(600, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true) // Enable retry mechanism
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://online.pranaliinfotech.com/pigateway/api/")
                .client(okHttpClient) // Set the custom OkHttpClient
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        Call<List<SaleRegisterItem>> call = apiService.getSaleRegisterData();

        call.enqueue(new retrofit2.Callback<List<SaleRegisterItem>>() {
            @Override
            public void onResponse(Call<List<SaleRegisterItem>> call, Response<List<SaleRegisterItem>> response) {
                progressDialog.dismiss();

                System.out.println(response);

                Toast.makeText(MainActivity.this, ""+response, Toast.LENGTH_SHORT).show();

//                if (response.isSuccessful()) {
//                    List<SaleRegisterItem> itemList = response.body();
//                    if (itemList != null && !itemList.isEmpty()) {
//                        adapter.setItems(itemList);
//
//                    } else {
//                        Toast.makeText(MainActivity.this, "Empty response", Toast.LENGTH_SHORT).show();
//                    }
//                } else {
//                    Toast.makeText(MainActivity.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
//                }
            }

            @Override
            public void onFailure(Call<List<SaleRegisterItem>> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
