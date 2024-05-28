package com.example.tab_system_ts;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tab_system_ts.java_class.ConnectionDetector;

import java.io.IOException;
import java.net.Socket;

public class IpAddress extends AppCompatActivity {

    private EditText editTextIP, editTextPort;
    private Button btnConnect;
    Boolean isInternetPresent = false;

    public static String ipAdd;
    public static String portNum;


    public void internetDialog(){

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(IpAddress.this);
        alertDialog.setTitle("Network Error");
        alertDialog.setMessage("Would You Like To Try Again.");
        alertDialog.setIcon(R.drawable.fail);

        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getApplicationContext(),IpAddress.class);
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
        alertDialog.show();
        alertDialog.setCancelable(false);
    }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            EdgeToEdge.enable(this);
            setContentView(R.layout.activity_ip_address);

            editTextIP = findViewById(R.id.edt_ip_addr);
            editTextPort = findViewById(R.id.edt_port_number);
            btnConnect = findViewById(R.id.btn_ip_addr);

            ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
            isInternetPresent = cd.isConnectingToInternet();

            if(isInternetPresent) {
                btnConnect.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        validation();
                    }
                });

            }else{
                internetDialog();

            }

        }
    public void validation(){

         String ipAddress = editTextIP.getText().toString();
         String portt = editTextPort.getText().toString();


        if(ipAddress.isEmpty()){
            editTextIP.setError("Empty");
            Toast.makeText(IpAddress.this, "Enter Ip Address", Toast.LENGTH_SHORT).show();
        } else if (portt.isEmpty()) {
            editTextPort.setError("Empty");
            Toast.makeText(IpAddress.this, "Enter Port Number", Toast.LENGTH_SHORT).show();
        } else if (ipAddress.isEmpty() && portt.isEmpty()) {
            Toast.makeText(IpAddress.this, "Enter Ip & Port", Toast.LENGTH_SHORT).show();
            editTextIP.setError("Empty");
            editTextPort.setError("Empty");
        }else{
            int port = Integer.parseInt(editTextPort.getText().toString());
            ConnectTask connectTask = new ConnectTask();
            connectTask.execute(ipAddress, String.valueOf(port));
        }
    }

    private class ConnectTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            String ipAddress = params[0];
            int port = Integer.parseInt(params[1]);

            try {
                // Attempt to establish a connection with the server
                Socket socket = new Socket(ipAddress, port);
                socket.close();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean isConnected) {
            if (isConnected) {

                SharedPreferences sp1 = getSharedPreferences("ipAdd_portNum", MODE_PRIVATE);
                SharedPreferences.Editor edt = sp1.edit();
                edt.putString("ipAdd",  editTextIP.getText().toString());
                edt.putString("portNum", editTextPort.getText().toString());
                edt.apply();


                Intent in = new Intent(IpAddress.this, ImeiActivity.class);
                in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(in);
            } else {
                Toast.makeText(IpAddress.this, "Connection failed", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

