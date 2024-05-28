package com.example.ipandport;

import android.os.AsyncTask;
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

import java.io.IOException;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    private EditText editTextIP, editTextPort;
    private Button btnConnect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        editTextIP = findViewById(R.id.editTextIP);
        editTextPort = findViewById(R.id.editTextPort);
        btnConnect = findViewById(R.id.btnConnect);

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ipAddress = editTextIP.getText().toString();
                int port = Integer.parseInt(editTextPort.getText().toString());

                // Start a background task to connect to the server
                ConnectTask connectTask = new ConnectTask();
                connectTask.execute(ipAddress, String.valueOf(port));
            }
        });
    }

    private class ConnectTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            String ipAddress = params[0];
            int port = Integer.parseInt(params[1]);

            try {
                // Attempt to establish a connection with the server
                Socket socket = new Socket(ipAddress, port);
                socket.close(); // Close the socket immediately after opening to check if the connection is successful
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean isConnected) {
            if (isConnected) {
                // Connection successful
                Toast.makeText(MainActivity.this, "Connected successfully", Toast.LENGTH_SHORT).show();
            } else {
                // Connection failed
                Toast.makeText(MainActivity.this, "Connection failed", Toast.LENGTH_SHORT).show();
            }
        }

    }
}