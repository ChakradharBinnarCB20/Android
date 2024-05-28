package com.example.home.Class;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.StrictMode;
import android.util.Log;

import com.example.home.IPAdderss_Activity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
/**

 * Created by IT on 2/17/2017.

 */

public class ConnectionClass {

    IPAdderss_Activity a=new IPAdderss_Activity();
    String ip=a.getFileName();
    private Context context;
    public ConnectionClass(Context context){
        this.context = context;
    }

   // String ip = "192.168.29.252:2222";

    String classs = "net.sourceforge.jtds.jdbc.Driver";

    String db = "BEERBAR";

    String un = "SA";

    String password = "PIMAGIC";



    @SuppressLint("NewApi")

    public Connection CONN() {
        System.out.println("Ip Address Connection......"+ip);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()

                .permitAll().build();

        StrictMode.setThreadPolicy(policy);

        Connection conn = null;

        String ConnURL = null;

        try {
            Class.forName(classs);
            ConnURL = "jdbc:jtds:sqlserver://" + ip + ";"

                    + "databaseName=" + db + ";user=" + un + ";password="

                    + password + ";";

            conn = DriverManager.getConnection(ConnURL);

        } catch (SQLException se) {

            Log.e("ERRO", se.getMessage());

        } catch (ClassNotFoundException e) {

            Log.e("ERRO", e.getMessage());

        } catch (Exception e) {

            Log.e("ERRO", e.getMessage());

        }

        return conn;

    }

}