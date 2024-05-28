package com.example.test;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
              Boolean status = sp.getBoolean("flag", false);

                Intent in;

              if (status){
                  in = new Intent(SplashScreen.this, MainActivity.class);
              }else{
                  in = new Intent(SplashScreen.this, LoginScreen.class);
              }
              startActivity(in);
            }
        },1000);
    }
}