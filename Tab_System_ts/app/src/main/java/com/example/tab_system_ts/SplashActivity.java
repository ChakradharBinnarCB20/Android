package com.example.tab_system_ts;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tab_system_ts.java_class.ConnectionDetector;

public class SplashActivity extends AppCompatActivity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                SharedPreferences sp = getSharedPreferences("ip_port_flag", MODE_PRIVATE);
                Boolean check = sp.getBoolean("flag", false);

                SharedPreferences sp1 = getSharedPreferences("ipAdd_portNum", MODE_PRIVATE);
                String ip = sp1.getString("ipAdd", "");
                String port = sp1.getString("port", "");

                Intent in;
                if (check){
                                in = new Intent(SplashActivity.this, Home.class);
                                in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                }else {
                    in = new Intent(SplashActivity.this, IpAddress.class);
                    in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                }
                startActivity(in);
            }
        },3000);

    }
}