package com.example.generateimeinumber;

import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

    Button getImeiButton;

    String imei;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getImeiButton  = findViewById(R.id.getImeiButton);

        getImeiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                imei = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                Log.d("dddd",imei);

            }
        });


    }
}
