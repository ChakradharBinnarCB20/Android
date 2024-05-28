package com.example.home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.home.Class.ConnectionDetector;

import java.net.NetworkInterface;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.List;

public class IMEI_Activity extends AppCompatActivity {

    //---------------------
    //String ip = "202.189.238.94";

    String classs = "net.sourceforge.jtds.jdbc.Driver";
    String db = "BEERBAR";
    String un = "SA";
    String password = "PIMAGIC";
    Connection conn = null;

    String out_put = "";
    int login_yn;
    //--------------------
     TextView txt_imei_num,txt_msg;
     TelephonyManager tm;
    SharedPreferences sp;
    private static final int PERMISSION_REQUEST_CODE = 1;
    String IMEINumber,reg_imei_num;
    public String con_ipaddress,portnumber;
    public String ipaddress;
    Toolbar toolbar;
    IMEI_Activity m_com;
    IMEI_Activity connectionClass;
    ConnectionDetector cd;
    Boolean isInternetPresent = false;
   // String m_androidId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imei_);

        //------------------------Toolbar-------------------------------------------
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView toolbar_title = (TextView) toolbar.findViewById(R.id.toolbar_title);//title
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        toolbar_title.setText("Device Registration");
        toolbar_title.setTextColor(0xFFFFFFFF);
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        sp = getSharedPreferences("IPADDR", MODE_PRIVATE);
        con_ipaddress = sp.getString("ipaddress", "");
        portnumber = sp.getString("portnumber", "");
        Log.d("IPADDRESS", con_ipaddress);

        //setFileName(con_ipaddress);
        //-----9/30/2022----new update-------
        IMEINumber = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.d("m_androidId",IMEINumber);

        m_com = new IMEI_Activity();
        connectionClass = new IMEI_Activity();
        //---------MAC ADDRESS--------------------------
       /* WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wInfo = wifiManager.getConnectionInfo();
        IMEINumber = wInfo.getMacAddress();
        Log.d("macAddress",IMEINumber);*/
          //-----9/30/2022-- comment---------
      /*  try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            IMEINumber ="";
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(Integer.toHexString(b & 0xFF) + ":");
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                IMEINumber=res1.toString().toUpperCase();
            }

        } catch (Exception ex) {
            //handle exception
        }*/
        //----------------------------------------------
        txt_imei_num=(TextView)findViewById(R.id.txt_imei_num);
        txt_msg=(TextView)findViewById(R.id.txt_msg);
        txt_msg.setPaintFlags(txt_msg.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        //tm=(TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
       // check_permission();
        txt_imei_num.setText(IMEINumber);
       // Toast.makeText(this, "IMEINumber"+IMEINumber, Toast.LENGTH_SHORT).show();
        connectionClass.CONN(con_ipaddress,portnumber);
        //-----------validate imei  number--------

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();
        if (isInternetPresent)
        {
            try
            {

                reg_imei_num = M_get_string("select IMEI_NO from tabusermast where IMEI_NO like '%"+IMEINumber+"%'",con_ipaddress,portnumber);
                if(reg_imei_num.length()>0)
                {
                    SharedPreferences pref = getApplicationContext().getSharedPreferences("IMEI", MODE_PRIVATE); // 0 - for private mode
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("imei",IMEINumber);
                    editor.putString("session","1");
                    editor.commit();
                    SharedPreferences p = getApplicationContext().getSharedPreferences("IPADDR", MODE_PRIVATE); // 0 - for private mode
                    SharedPreferences.Editor ed = p.edit();
                    ed.putString("ipaddress",con_ipaddress);
                    ed.putString("portnumber",portnumber);
                    ed.commit();

                    Intent i=new Intent(getApplicationContext(),Login.class);
                    startActivity(i);
                    finish();
                }
                else
                {

                    Toast.makeText(this, "Not Registered Yet...", Toast.LENGTH_SHORT).show();
                }
            }catch(Exception e)
            {

            }
        }
        else
        {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(IMEI_Activity.this);
            alertDialog.setTitle("WiFi Connection Error");
            alertDialog.setMessage("Would You Like To Try Again.");
            alertDialog.setIcon(R.drawable.fail);

            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(getApplicationContext(),IMEI_Activity.class);
                    startActivity(intent);
                    finish();

                }
            });
            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            //alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
            //alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.setCancelable(false);

        }

        //--------------------------------------------------
       // Toast.makeText(this, "Registered IMEINumber"+reg_imei_num, Toast.LENGTH_SHORT).show();

    }

    public String M_get_string(String qry,String ip,String port) throws ClassNotFoundException, SQLException {
        try {

            System.out.println("Ip Address......"+con_ipaddress);
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            String ConnURL = "jdbc:jtds:sqlserver://" + ip + ":" + port + ";" + "databaseName=" + db + ";user=" + un + ";password=" + password + ";";;
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
            Intent i=new Intent(getApplicationContext(),IPAdderss_Activity.class);
            startActivity(i);
            finish();
        }
        return out_put;
    }

    public Connection CONN(String ip,String port) {


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()

                .permitAll().build();

        StrictMode.setThreadPolicy(policy);

        Connection conn = null;

        String ConnURL = null;

        try {

            Class.forName(classs);
            ConnURL = "jdbc:jtds:sqlserver://" + ip + ":" + port + ";" + "databaseName=" + db + ";user=" + un + ";password=" + password + ";";;
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
