package com.khi.scvotingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.khi.scvotingapp.db.DBConnection;
import com.khi.scvotingapp.util.AppUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConnectionActivity extends AppCompatActivity {

    Button btnConnect;
    TextInputLayout edttxtlytIPAddress;
    TextInputEditText edttxtIPAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);
        initialize(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.exit(0);
    }

    private void initialize(Bundle savedInstanceState) {

        btnConnect = findViewById(R.id.btnConnect);
        edttxtlytIPAddress = findViewById(R.id.edttxtlytIPAddress);
        edttxtIPAddress = findViewById(R.id.edttxtIPAddress);

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String ip = edttxtIPAddress.getText().toString();
                if (!validateIP(ip)) {
                    edttxtlytIPAddress.setError("Please enter a valid IP Address.");
                    return;
                }

                edttxtlytIPAddress.setEnabled(false);
                btnConnect.setEnabled(false);
                connect(ip);
            }
        });

        edttxtIPAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                edttxtlytIPAddress.setError(null);
            }
        });

    }

    private boolean validateIP(String ip) {
        String ipRegex = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";

        return ip != null && ip.matches(ipRegex);
    }


    private void connect(String ip) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    DBConnection.setIP(ip);
                    Connection conn = DBConnection.connect();
                    if (conn != null) {
                        String query = "SELECT GETDATE() AS now_date";
                        Statement stmt = conn.createStatement();
                        ResultSet rs = stmt.executeQuery(query);


                        while (rs.next()) {
                            final String result = rs.getString("now_date");
                            new AppUtil(result);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ConnectionActivity.this, "Connected. " + AppUtil.getDate(), Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(ConnectionActivity.this, SignInActivity.class));
                                    finish();
                                }
                            });
                        }
                        conn.close();
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                edttxtlytIPAddress.setError("Connection Failed. If you're not sure what the IP address is, please refer to our faculty.");
                                edttxtlytIPAddress.setEnabled(true);
                                btnConnect.setEnabled(true);
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ConnectionActivity.this, "An error occured. Please report to our faculty.\n\nError: " + e.toString(), Toast.LENGTH_LONG).show();
                            edttxtlytIPAddress.setEnabled(true);
                            btnConnect.setEnabled(true);
                        }
                    });
                }
            }
        });
    }
}