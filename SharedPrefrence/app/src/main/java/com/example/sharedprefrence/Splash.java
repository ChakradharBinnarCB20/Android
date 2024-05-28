package com.example.sharedprefrence;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
                Boolean check = sp.getBoolean("flag", false);

                Intent in;
                if (check){
                    in = new Intent(Splash.this, Home.class);
                }else {
                    in = new Intent(Splash.this, Login.class);
                }
                startActivity(in);

            }
        },3000);

    }
}