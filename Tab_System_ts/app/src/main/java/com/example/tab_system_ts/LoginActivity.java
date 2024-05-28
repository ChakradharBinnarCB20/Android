package com.example.tab_system_ts;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tab_system_ts.java_class.DbConnectionClass;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginActivity extends AppCompatActivity {

    EditText etLoginId,etPass;
    Button btn_signin_login;

    ImeiActivity connClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);



        etLoginId = findViewById(R.id.edit_login_email);
        etPass = findViewById(R.id.edit_login_pass);
        btn_signin_login = findViewById(R.id.btn_signin_login);

        btn_signin_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id=etLoginId.getText().toString();
                String pass = etPass.getText().toString();
                System.out.println(id);
                System.out.println(pass);

                String sql = "select TABUSER_DESC,TABUSER_PASS_WORD from tabusermast WHERE TABUSER_DESC='"+id+"' AND TABUSER_PASS_WORD='"+pass+"'";

                SharedPreferences sp1 = getSharedPreferences("ipAdd_portNum", MODE_PRIVATE);
                String ip = sp1.getString("ipAdd", "");
                String port = sp1.getString("portNum", "");

                try {
                    Connection con = connClass.getConnection(ip, port);
                    PreparedStatement ps = con.prepareStatement(sql);
                    ResultSet set = ps.executeQuery();
                    if(set.next()) {
                        Intent in = new Intent(LoginActivity.this, Home.class);
                        in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(in);
                        SharedPreferences sp = getSharedPreferences("ip_port_flag", MODE_PRIVATE);
                        SharedPreferences.Editor edt = sp.edit();
                        edt.putBoolean("flag", true);
                        edt.apply();
                    }else{
                        Toast.makeText(LoginActivity.this, "Invalid id, Pass", Toast.LENGTH_SHORT).show();
                    }
                    ps.close();
                    con.close();

                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

            }
        });






    }
}