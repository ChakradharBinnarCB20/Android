package com.example.tab_system_ts;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tab_system_ts.java_class.DbConnectionClass;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ImeiActivity extends AppCompatActivity {

    TextView androidIdTextView;
    String androidID;


    public static Connection getConnection(String ip, String port) {

        String URL = "jdbc:jtds:sqlserver://"+ip+":"+port+";databaseName=BEERBAR";
        //String URL = "jdbc:jtds:sqlserver://192.168.1.111:1433;databaseName=BEERBAR";
        String USER = "sa";
        String PASSWORD = "pimagic";

        Connection connection = null;
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }






    @SuppressLint({"HardwareIds", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_imei);

        androidIdTextView = findViewById(R.id.txt_imei_num);
        androidID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        androidIdTextView.setText(androidID.toUpperCase());

        Log.e("eee", "Hello...!");

        String sql = "select IMEI_NO from tabusermast where IMEI_NO='"+androidID+"'";

        SharedPreferences sp1 = getSharedPreferences("ipAdd_portNum", MODE_PRIVATE);
        String ip = sp1.getString("ipAdd", "");
        String port = sp1.getString("portNum", "");

        try {
            Connection con = getConnection(ip,port);
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet set = ps.executeQuery();
            if(set.next()) {
                Intent in = new Intent(ImeiActivity.this, LoginActivity.class);
                in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(in);

            }else{
                Toast.makeText(ImeiActivity.this, "Not Register Yet...!", Toast.LENGTH_SHORT).show();
            }
            ps.close();
            con.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}