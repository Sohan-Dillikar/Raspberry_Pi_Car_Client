package com.example.picontrol;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {

    private EditText ipEditText;
    private EditText portEditText;
    private Button connectButton;
    private TextView connectedTextView;
    private Button forwardButton;
    private  Button backwardButton;
    private Button leftButton;
    private Button rightButton;
    private Button stopButton;

    private String address;
    private int port;

    private Thread connectThread;
    private PrintWriter data_output;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Raspberry Pi Car Client");

        connectButton = (Button) findViewById(R.id.connectButton);
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ipEditText = (EditText) findViewById(R.id.ipEditText);
                portEditText = (EditText) findViewById(R.id.portEditText);
                if (ipEditText.getText().toString().isEmpty() || portEditText.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter an IP address and port number", Toast.LENGTH_LONG).show();
                } else {
                    connectedTextView = (TextView) findViewById(R.id.connectedTextView);
                    address = ipEditText.getText().toString().trim();
                    port = Integer.parseInt(portEditText.getText().toString().trim());
                    connectThread = new Thread(new ConnectThread());
                    connectThread.start();
                }
            }
        });

        forwardButton = (Button) findViewById(R.id.forwardButton);
        forwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new SendThread("forward")).start();
            }
        });

        backwardButton = (Button) findViewById(R.id.backwardButton);
        backwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new SendThread("backward")).start();
            }
        });

        leftButton = (Button) findViewById(R.id.leftButton);
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new SendThread("left")).start();
            }
        });

        rightButton = (Button) findViewById(R.id.rightButton);
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new SendThread("right")).start();
            }
        });

        stopButton = (Button) findViewById(R.id.stopButton);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new SendThread("stop")).start();
            }
        });
    }

    class ConnectThread implements Runnable {
        @Override
        public void run() {
            Socket socket;
            try {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        connectedTextView.setText("Connecting...");
                    }
                });
                socket = new Socket(address, port);
                data_output = new PrintWriter(socket.getOutputStream());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        connectedTextView.setText("Connected!");
                    }
                });
            } catch (IOException i) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        connectedTextView.setText("Couldn't connect to server");
                    }
                });
            }
        }
    }

    class SendThread implements Runnable {
        private String command;
        SendThread(String command) {
            this.command = command;
        }
        @Override
        public void run() {
            try {
                data_output.write(command);
                data_output.flush();
            } catch (NullPointerException n) {}
        }
    }
}