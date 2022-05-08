package com.example.logger;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Rssi extends AppCompatActivity implements View.OnClickListener {
    // declare variables
    private int numofsamples=0;
    private ArrayList<Integer> RSSI_LIST= new ArrayList<Integer>();

    // handler
    Handler handler= new Handler();
    Runnable myRunable ;
    int delay= 500; // ms
    // wifi var
    private WifiManager wifiManager;// display access point
    private WifiInfo wifiInfo;// connection access point-->get RSSI

    private TextView samples, values;
    private Button buttonstart, buttonclear, buttonsave;
    public static final int Write_permission=101;
    private  boolean measureIsOn=false;
    private String saveDescription="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rssi_layout);
        Intent intent = getIntent();
        samples = (TextView) findViewById(R.id.count);
        values = (TextView) findViewById(R.id.measure);
        buttonstart = (Button) findViewById(R.id.start);
        buttonclear = (Button) findViewById(R.id.clear);
        buttonsave = (Button) findViewById(R.id.save);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        //click listener
        buttonstart.setOnClickListener(this);
        buttonclear.setOnClickListener(this);
        buttonsave.setOnClickListener(this);
        //create Runnable
        myRunable = new Runnable() {
            @Override
            public void run() {
                wifiInfo = wifiManager.getConnectionInfo();
                values.setText(String.valueOf(wifiInfo.getRssi()));
                RSSI_LIST.add(wifiInfo.getRssi());
                numofsamples++;
                samples.setText("Amount of samples: " + numofsamples);
                handler.postDelayed(myRunable, delay);
            }
        };
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Write_permission);
        }
    }
    // If the user does not give permission, the app can not function and will exit
    public void onRequestPermissionResult ( int requestCode, String permission[], int[] grantResults)
    {
        switch (requestCode) {
            case Write_permission: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        //do nothing
                    }
                }
                else {
                    finish();
                    System.exit(0);
                }
                return;
            }
        }
    }

    private void writeToFile (String data, String filename)
    {
        try {
            File file = new File(this.getExternalFilesDir (null),filename + ".txt");
            FileOutputStream fileoutput = new FileOutputStream(file);
            OutputStreamWriter os = new OutputStreamWriter(fileoutput);
            os.write(data);
            os.flush();
            os.close();
            Toast.makeText(this, "Data is saved as "+ filename + ".txt", Toast.LENGTH_LONG).show();
        }
        catch (IOException e) {
            Log.e("Exception ", "File writed failed" + e.toString());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.start:{
                if(measureIsOn==false){
                    buttonstart.setText("stop Measuring");
                    buttonsave.setEnabled(false);
                    buttonclear.setEnabled(false);
                    handler.postDelayed(myRunable,delay);
                    measureIsOn=true;
                }
                else{
                    buttonstart.setText("start Measuring");
                    handler.removeCallbacks(myRunable);
                    buttonsave.setEnabled(true);
                    buttonclear.setEnabled(true);
                    measureIsOn=false;
                }
                break;
            }
            case R.id.clear:{
                RSSI_LIST.clear();
                numofsamples=0;
                samples.setText("The amount of samples:"+ numofsamples);
                values.setText(String.valueOf(0));
                break;
            }
            case R.id.save:{
                AlertDialog.Builder builder=new AlertDialog.Builder(this);
                builder.setTitle("Input file name description");
                //set up the input
                final EditText input= new EditText(this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);
                //dialog has an ok choice or cancel choice
                //if ok
                builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        saveDescription=input.getText().toString();
                        SimpleDateFormat df= new SimpleDateFormat("yyMMdd-HHmm");
                        writeToFile(RSSI_LIST.toString(),df.format(new Date())+"_"+saveDescription);
                    }
                });
                builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                builder.show();
                break;
            }
            default:
                break;
        }
    }

}