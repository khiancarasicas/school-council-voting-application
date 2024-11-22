package com.khi.scvotingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.khi.scvotingapp.manager.SignInManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SignInActivity extends AppCompatActivity {

    Button btnSignIn;
    TextInputLayout edttxtlytID;
    TextInputEditText edttxtID;
    TextInputLayout edttxtlytPassword;
    TextInputEditText edttxtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        initialize(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.exit(0);
    }

    private void initialize(Bundle savedInstanceState) {
        btnSignIn = findViewById(R.id.btnSignIn);
        edttxtlytID = findViewById(R.id.edttxtlytID);
        edttxtID = findViewById(R.id.edttxtID);
        edttxtlytPassword = findViewById(R.id.edttxtlytPassword);
        edttxtPassword = findViewById(R.id.edttxtPassword);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edttxtID.getText().toString().isEmpty()) {
                    edttxtlytID.setError("ID is required.");
                    return;
                }

                if (edttxtPassword.getText().toString().isEmpty()) {
                    edttxtlytPassword.setError("Pasword is required.");
                    return;
                }
                
                btnSignIn.setEnabled(false);
                edttxtlytID.setEnabled(false);
                edttxtlytPassword.setEnabled(false);

                try {
                    int loginID = Integer.parseInt(edttxtID.getText().toString().trim());
                    String password = edttxtPassword.getText().toString().trim();

                    signIn(loginID, password);
                } catch (NumberFormatException e) {
                    edttxtlytID.setError("Invalid ID. Please enter a numeric value.");

                    btnSignIn.setEnabled(true);
                    edttxtlytID.setEnabled(true);
                    edttxtlytPassword.setEnabled(true);
                }

            }
        });
    }

    private void signIn(int loginID, String password) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    boolean loginSuccess = SignInManager.getInstance().validateLogin(loginID, password);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (loginSuccess) {
                                Toast.makeText(SignInActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();

                                startActivity(new Intent(SignInActivity.this, MainActivity.class));
                                finish();
                            } else {
                                Toast.makeText(SignInActivity.this, "Invalid Username or Password", Toast.LENGTH_SHORT).show();

                                btnSignIn.setEnabled(true);
                                edttxtlytID.setEnabled(true);
                                edttxtlytPassword.setEnabled(true);
                            }
                        }
                    });
                } catch (Exception e) {
                    Log.e("ERROR", "signIn: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(SignInActivity.this, "Error during login", Toast.LENGTH_SHORT).show();

                            btnSignIn.setEnabled(true);
                            edttxtlytID.setEnabled(true);
                            edttxtlytPassword.setEnabled(true);
                        }
                    });
                }
            }
        });
    }
}