package com.example.home;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import android.widget.TextView;

import com.example.home.Class.ConnectionDetector;


/**
 * Created by Scorpio on 15-02-16.
 */
public class Splashscreen extends Activity {
    private static int SPLASH_TIME_OUT = 3000;
    TextView splashText;
    ConnectionDetector cd;
    Boolean isInternetPresent = false;
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);


        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();
        if (isInternetPresent)
        {
            new Handler().postDelayed(new Runnable()
            {

                @Override
                public void run()
                {
                    Intent i = new Intent(Splashscreen.this, Home.class);
                    startActivity(i);
                    finish();
                }
            }, SPLASH_TIME_OUT);
        }
        else
        {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(Splashscreen.this);
            alertDialog.setTitle("No Internet Connection");
            alertDialog.setMessage("Please check your internet connection and try again.");
            alertDialog.setIcon(R.drawable.fail);
            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                    dialog.cancel();
                }
            });
            alertDialog.show();
        }

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Runtime.getRuntime().gc();
    }
}
