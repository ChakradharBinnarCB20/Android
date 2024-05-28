package com.example.tab_system_ts.java_class;

import static com.example.tab_system_ts.IpAddress.ipAdd;
import static com.example.tab_system_ts.IpAddress.portNum;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tab_system_ts.IpAddress;
import com.example.tab_system_ts.R;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnectionClass extends AppCompatActivity{

//    IpAddress ip_port = new IpAddress();
//
//    public Connection getConnection() {
//
//       // String ip = ip_port.getIpAdd();
//       // String port = ip_port.getPortNum();
//
//       // System.out.println(ip_port.getIpAdd());
//      //  System.out.println(ip_port.getPortNum());
//       //String URL = "jdbc:jtds:sqlserver://"+ip+":"+port+";databaseName=BEERBAR";
//       //String URL = "jdbc:jtds:sqlserver://192.168.1.111:1433;databaseName=BEERBAR";
//        String USER = "sa";
//        String PASSWORD = "pimagic";
//
//        Connection connection = null;
//        try {
//            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//            StrictMode.setThreadPolicy(policy);
//            Class.forName("net.sourceforge.jtds.jdbc.Driver");
//           // connection = DriverManager.getConnection(URL, USER, PASSWORD);
//        } //catch (ClassNotFoundException | SQLException e) {
//            e.printStackTrace();
//        }
//        return connection;
//    }
}
