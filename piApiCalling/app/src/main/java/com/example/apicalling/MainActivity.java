package com.example.apicalling;


import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SaleAdapter saleAdapter;
    private List<SaleReport> saleReports = new ArrayList<>(); // Create an empty list

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        saleAdapter = new SaleAdapter(saleReports); // Pass the empty list to the adapter
        recyclerView.setAdapter(saleAdapter);

        SaleApiService service = RetrofitClient.getRetrofitInstance().create(SaleApiService.class);
        Call<List<SaleReport>> call = service.getSaleData();
        call.enqueue(new Callback<List<SaleReport>>() {
            @Override
            public void onResponse(Call<List<SaleReport>> call, Response<List<SaleReport>> response) {
                if (response.isSuccessful()) {
                    List<SaleReport> responseList = response.body();
                    if (responseList != null) {
                        saleReports.addAll(responseList); // Add the response data to the list
                        saleAdapter.notifyDataSetChanged(); // Notify adapter that data set changed
                    }
                }
            }

            @Override
            public void onFailure(Call<List<SaleReport>> call, Throwable t) {
                // Handle failure
            }
        });
    }
}