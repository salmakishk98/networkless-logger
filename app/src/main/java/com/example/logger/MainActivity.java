package com.example.logger;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void openRssiLogger(View view)
    {
        Intent intent = new Intent(this, com.example.logger.Rssi.class);
        startActivity(intent);
    }
    public void openCell_infoLogger(View view)
    {
        Intent intent1 = new Intent(this, com.example.logger.Cell_info.class);
        startActivity(intent1);
    }
    public void openBluetooth_infoLogger(View view)
    {
        Intent intent2 = new Intent(this, com.example.logger.Blutooth_info.class);
        startActivity(intent2);
    }

}