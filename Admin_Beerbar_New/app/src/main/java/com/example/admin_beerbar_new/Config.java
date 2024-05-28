package com.example.admin_beerbar_new;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.example.admin_beerbar_new.Class.SessionManager;
import com.example.admin_beerbar_new.Class.TransparentProgressDialog;

public class Config extends AppCompatActivity {

    String classs = "net.sourceforge.jtds.jdbc.Driver";
   // String db = "WINESHOP";
   // String db = "BEERBAR";
    String un = "SA";
    String password = "PIMAGIC";
    Connection conn = null;
    String con_ipaddress,portnumber;
    String out_put = "";
    int login_yn;
    EditText edit_login_email,edit_login_pass;
    TextView btn_login;
    TransparentProgressDialog pd;
    //IMEI_Activity connectionClass;
    SessionManager sessionManager;
    SharedPreferences sp_pi_login,sp_edit;
    SharedPreferences.Editor editor_sp_pi_login;
    String tab_user_code,tab_user_name,imei;
    Toolbar toolbar;
    String session;
    Config connectionClass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences  sp1 = getSharedPreferences("IP", MODE_PRIVATE);
        session = sp1.getString("session", "");
        if(session.equals("0"))
        {
            Intent i=new Intent(getApplicationContext(),IPAdderss_Activity.class);
            startActivity(i);
            finish();
        }
        //------------------------Toolbar-------------------------------------------
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView toolbar_title = (TextView) toolbar.findViewById(R.id.toolbar_title);//title
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        toolbar_title.setText("Login");
        toolbar_title.setTextColor(0xFFFFFFFF);


        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        SharedPreferences sp = getSharedPreferences("IPADDR", MODE_PRIVATE);
        con_ipaddress = sp.getString("ipaddress", "");
        portnumber = sp.getString("portnumber", "");

        sessionManager = new SessionManager(this);
        sp_pi_login = getSharedPreferences("PI", MODE_PRIVATE);
        editor_sp_pi_login = sp_pi_login.edit();

        if (sessionManager.isLoggedIn()) {

            String user=sp_pi_login.getString("email",null);
            Intent i=new Intent(getApplicationContext(),Home.class);
            i.putExtra("email",user );
            startActivity(i);
            finish();

        }
        connectionClass=new Config();
       /* edit_login_email = (EditText) findViewById(R.id.edit_login_email);
        edit_login_pass = (EditText) findViewById(R.id.edit_login_pass);
        pd = new TransparentProgressDialog(Config.this, R.drawable.busy);
        btn_login = (TextView) findViewById(R.id.btn_signin_login);*/

    }


    public Connection CONN(String ip,String port,String db) {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()

                .permitAll().build();

        StrictMode.setThreadPolicy(policy);

       conn = null;
        String ConnURL = null;
        try {

            Class.forName(classs);
            ConnURL = "jdbc:jtds:sqlserver://" + ip + ":" + port + ";" + "databaseName=" + db + ";user=" + un + ";password=" + password + ";";;
           // ConnURL = "jdbc:jtds:sqlserver://localhost:2222;" + "databaseName=" + db + ";user=" + un + ";password=" + password + ";";;
           /* ConnURL = "jdbc:jtds:sqlserver://" + con_ipaddress + ";"

                    + "databaseName=" + db + ";user=" + un + ";password="

                    + password + ";";*/

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

