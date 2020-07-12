package com.alobeidi.btcount;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    Button buttonON , buttonOFF, scanBt;
    ListView deviceList;
    ArrayList<String> arrayList = new ArrayList<String>();
    ArrayAdapter<String> arrayAdapter;
    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    int requestCodeForEnable = 1;
    Intent btEnablingIntent;
    Vibrator vibrator;
    MediaPlayer mediaPlayer;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonOFF = findViewById(R.id.bt_off);
        buttonON = findViewById(R.id.bt_on);
        scanBt = findViewById(R.id.bt_scan);
        deviceList = findViewById(R.id.device_list);
        btEnablingIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
         mediaPlayer = MediaPlayer.create(this,R.raw.alarm);

 // /////////////////// Enable bluetooth ///////////////////////
        buttonON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (bluetoothAdapter==null) {
                    Toast.makeText(getApplicationContext(),"this device not support bluetooth",Toast.LENGTH_SHORT).show();
                }else {
                    if (!bluetoothAdapter.isEnabled()){
                        startActivityForResult(btEnablingIntent,requestCodeForEnable);
                    }
                }

            }


        });

        // /////////////////// disable bluetooth ///////////////////////

        buttonOFF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bluetoothAdapter.isEnabled()) {
                    bluetoothAdapter.disable();
                    Toast.makeText(getApplicationContext(),"bluetooth disable",Toast.LENGTH_SHORT).show();

                }
            }
        });

        // /////////////////// start scan ///////////////////////

        scanBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                arrayList.clear();
                if (!bluetoothAdapter.isEnabled()){
                    Toast.makeText(getApplicationContext(), " bluetooth not Enabled", Toast.LENGTH_SHORT).show();

                }else {
                    bluetoothAdapter.startDiscovery();
                    Toast.makeText(getApplicationContext(), "discover started", Toast.LENGTH_SHORT).show();
                }
            }
        });
        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, intentFilter);
        arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, arrayList);
        deviceList.setAdapter(arrayAdapter);
    }



    BroadcastReceiver receiver = new BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                arrayList.add(device.getName() + device.getAddress());
                arrayAdapter.notifyDataSetChanged();
                if (arrayList.size()>1){
                    Toast.makeText(getApplicationContext(), "There are :" + arrayList.size() + "person here ", Toast.LENGTH_LONG).show();
                    vibrator.vibrate(3*1000);
                    mediaPlayer.start();



                }
//
            }
        }
    };
    @Override
    protected void onDestroy() {
        super.onDestroy();


        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(receiver);
    }

}
