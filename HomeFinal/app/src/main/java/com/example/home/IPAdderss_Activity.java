package com.example.home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class IPAdderss_Activity extends AppCompatActivity {
     EditText edt_ip_addr,edt_port_number;
     TextView txt_msg;
    String session="0";
    Toolbar toolbar;
    Button btn_ip_addr;
    private String fileName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ipaddress_);

        SharedPreferences  sp = getSharedPreferences("IMEI", MODE_PRIVATE);
        session = sp.getString("session", "");
         if(session.equals("1"))
         {
             Intent i=new Intent(getApplicationContext(),IMEI_Activity.class);
             startActivity(i);
             finish();
         }
        //------------------------Toolbar-------------------------------------------
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView toolbar_title = (TextView) toolbar.findViewById(R.id.toolbar_title);//title
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        toolbar_title.setText("Ip Address");
        toolbar_title.setTextColor(0xFFFFFFFF);
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        //----------------------------------------------
        btn_ip_addr=(Button) findViewById(R.id.btn_ip_addr);
        edt_ip_addr=(EditText) findViewById(R.id.edt_ip_addr);
        edt_port_number=(EditText) findViewById(R.id.edt_port_number);
        txt_msg=(TextView)findViewById(R.id.txt_msg);
        txt_msg.setPaintFlags(txt_msg.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        btn_ip_addr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setFileName(edt_ip_addr.getText().toString());
                SharedPreferences pref = getApplicationContext().getSharedPreferences("IPADDR", MODE_PRIVATE); // 0 - for private mode
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("ipaddress",edt_ip_addr.getText().toString());
                editor.putString("portnumber",edt_port_number.getText().toString());
                editor.commit();

                Intent i=new Intent(getApplicationContext(),IMEI_Activity.class);
                startActivity(i);
                finish();
            }
        });


        //--------------------------------------------------
       // Toast.makeText(this, "Registered IMEINumber"+reg_imei_num, Toast.LENGTH_SHORT).show();

    }




    public String getFileName() {
        return fileName;
    }


    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

}
