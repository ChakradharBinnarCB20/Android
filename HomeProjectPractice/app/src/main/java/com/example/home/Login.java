package com.example.home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.home.Class.SessionManager;
import com.example.home.Class.TransparentProgressDialog;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class Login extends AppCompatActivity {

    String con_ipaddress,portnumber;

    EditText edit_login_email,edit_login_pass;
    TextView btn_login;
    TransparentProgressDialog pd;
    IMEI_Activity connectionClass;
    SessionManager sessionManager;
    SharedPreferences sp_pi_login,sp_edit;
    SharedPreferences.Editor editor_sp_pi_login;
    String tab_user_code,tab_user_name,imei;
    String session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences  sp1 = getSharedPreferences("IMEI", MODE_PRIVATE);
        imei = sp1.getString("imei", "");
        session = sp1.getString("session", "");
        if(session.equals("0"))
        {
            Intent i=new Intent(getApplicationContext(),IPAdderss_Activity.class);
            startActivity(i);
            finish();
        }

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
        connectionClass = new IMEI_Activity();
        edit_login_email = (EditText) findViewById(R.id.edit_login_email);
        edit_login_pass = (EditText) findViewById(R.id.edit_login_pass);
        pd = new TransparentProgressDialog(Login.this, R.drawable.busy);
        btn_login = (TextView) findViewById(R.id.btn_signin_login);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edit_login_email.getText().toString().length()==0) {
                    edit_login_email.setError("Input User name missing");
                }
                else if (edit_login_pass.getText().toString().length()==0){
                    edit_login_pass.setError("Input Password missing");
                }
                else {
                    new login().execute();
                }

            }
        });
    }

    public class login extends AsyncTask<String,String,String>
    {
        String z = "";
        Boolean isSuccess = false;
        String lemail = edit_login_email.getText().toString();
        String lpass = edit_login_pass.getText().toString();
        @Override
        protected void onPreExecute() {
            pd.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            try
            {
                Connection con = connectionClass.CONN(con_ipaddress,portnumber);
                if (con == null) {
                    z = "Error in connection with SQL server";
                } else {
                    String query = "select TABUSER_CODE,TABUSER_DESC,TABUSER_PASS_WORD from tabusermast WHERE TABUSER_DESC='"+lemail+"' AND TABUSER_PASS_WORD='"+lpass+"' AND IMEI_NO LIKE '%"+imei+"%'";
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    if(rs.next())
                    {
                        tab_user_code=rs.getString("TABUSER_CODE");
                        tab_user_name=rs.getString("TABUSER_DESC");

                        z = "Login successful";
                        isSuccess=true;
                        con.close();
                    }
                    else
                    {
                        z = "Invalid Credentials!";
                        isSuccess = false;
                    }
                }


            }catch(Exception e)
            {

            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }
            if(isSuccess==true)
            {
                SharedPreferences pref = getApplicationContext().getSharedPreferences("TAB_DATA", MODE_PRIVATE); // 0 - for private mode
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("tab_user_name",tab_user_name);
                editor.putString("tab_user_code",tab_user_code);

                editor.commit();
                sessionManager.createLoginSession(edit_login_email.getText().toString(), edit_login_pass.getText().toString());
                Toast.makeText(getApplicationContext(), "Login Successful.", Toast.LENGTH_SHORT).show();
                editor_sp_pi_login.putString("email", lemail);
                editor_sp_pi_login.commit();


                Intent i = new Intent(Login.this, Home.class);
                i.putExtra("email", lemail);
                startActivity(i);
                finish();

            }
            else
            {
                Toast.makeText(getApplicationContext(), "Invalid Credentials.", Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(s);
        }
    }

    //========================Connection String===========================


}

