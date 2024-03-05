package com.example.homeautomation;


import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private String macAddress = "";
    private final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice bluetoothDevice;
    private BluetoothSocket bluetoothSocket;
    private OutputStream outputStream;
    private static final String TAG = "MainActivity";

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button b1 = findViewById(R.id.button5);
        Button b2 = findViewById(R.id.button2);
        Button b3 = findViewById(R.id.button3);
        Button b4 = findViewById(R.id.button4);
        Button b5 = findViewById(R.id.button);
        Button b6 = findViewById(R.id.Button);

        // Bluetooth permission check
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.BLUETOOTH_CONNECT}, 100);
        }

        // Bluetooth connection setup
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            Toast.makeText(this, "Bluetooth is not supported on this device", Toast.LENGTH_SHORT).show();
            return;
        }

        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        for (BluetoothDevice device : pairedDevices) {
            if (device.getName().equals("HC-05")) {
                macAddress = device.getAddress();
                Log.d(TAG, "Address: " + macAddress);
                break;
            }
        }
        if (macAddress.isEmpty()) {
            Log.d(TAG, "NO OR WRONG BLUETOOTH MODULE DETECTED");
        }

        if (!macAddress.isEmpty()) {
            bluetoothDevice = bluetoothAdapter.getRemoteDevice(macAddress);

            // Connecting to bluetooth device in a separate thread
            new Thread(() -> {
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED ||
                        ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED ||
                        ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.BLUETOOTH_CONNECT}, 100);
                }

                try {
                    bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuid);
                    bluetoothAdapter.cancelDiscovery();
                    bluetoothSocket.connect();
                    outputStream = bluetoothSocket.getOutputStream();
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Bluetooth successfully connected", Toast.LENGTH_LONG).show());
                } catch (IOException e) {
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Turn on Bluetooth and restart the app", Toast.LENGTH_SHORT).show());
                    Log.e(TAG, "Error connecting to Bluetooth", e);
                }
            }).start();
        }

        b1.setOnClickListener(view -> sendCommand(1));
        b2.setOnClickListener(view -> sendCommand(2));
        b3.setOnClickListener(view -> sendCommand(3));
        b4.setOnClickListener(view -> sendCommand(4));
        b5.setOnClickListener(view -> sendCommand(0));
        b6.setOnClickListener(view -> sendCommand(5));
    }

    public void openActivity(View view) {
        Intent intent;
        intent = new Intent(this, MainActivity3.class);
        startActivity(intent);
    }

    // Method to send commands over Bluetooth
    private void sendCommand(int n) {
        if (outputStream == null) {
            Log.d(TAG, "Output stream error");
            return;
        }
        try {
            char characterToSend = (char) ('0' + n); // Convert integer to ASCII character
            outputStream.write(characterToSend); // Sending the character over Bluetooth as a byte
        } catch (IOException e) {
            Log.e(TAG, "Error sending command over Bluetooth", e);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bluetoothSocket != null) {
            try {
                bluetoothSocket.close();
                Log.d(TAG, "Connection closed");
            } catch (IOException e) {
                Log.e(TAG, "Error while closing the connection", e);
            }
        }
    }
}
