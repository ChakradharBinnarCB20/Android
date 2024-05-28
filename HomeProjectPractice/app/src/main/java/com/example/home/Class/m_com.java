package com.example.home.Class;

import android.content.Context;
import android.os.StrictMode;
import android.widget.Toast;

import com.example.home.IMEI_Activity;
import com.example.home.IPAdderss_Activity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class m_com  extends  IMEI_Activity{

    IPAdderss_Activity a=new IPAdderss_Activity();
    String ip=a.getFileName();

    private Context context;
    public m_com(Context context){
        this.context = context;
    }

    //String ip = "202.189.238.94";
    String port = "2222";
    String classs = "net.sourceforge.jtds.jdbc.Driver";
    String db = "BEERBAR";
    String un = "SA";
    String password = "PIMAGIC";
    Connection conn = null;

    String out_put = "";

    public String M_get_string(String qry) throws ClassNotFoundException, SQLException {
        try {
            Toast.makeText(context, ""+ip, Toast.LENGTH_SHORT).show();
             System.out.println("Ip Address......"+ip);
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            String ConnURL = "jdbc:jtds:sqlserver://" + ip + ":" + port + ";" + "databaseName=" + db + ";user=" + un + ";password=" + password + ";";

            conn = DriverManager.getConnection(ConnURL);

            Statement statement = conn.createStatement();
            ResultSet resultat = statement.executeQuery(qry);
            while (resultat.next()) {
                out_put = resultat.getString(1);
            }
            resultat.close();
            statement.close();
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        return out_put;
    }
}


