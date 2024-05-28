package com.example.demoapicall;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SaleRegisterAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SaleRegisterAdapter();
        recyclerView.setAdapter(adapter);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://online.pranaliinfotech.com/pigateway/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        Call<List<SaleRegister>> call = apiService.getSaleRegisters();

        call.enqueue(new Callback<List<SaleRegister>>() {
            @Override
            public void onResponse(Call<List<SaleRegister>> call, Response<List<SaleRegister>> response) {
                if (response.isSuccessful()) {
                    List<SaleRegister> saleRegisters = response.body();
                    if (saleRegisters != null) {
                        adapter.setData(saleRegisters);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<SaleRegister>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}