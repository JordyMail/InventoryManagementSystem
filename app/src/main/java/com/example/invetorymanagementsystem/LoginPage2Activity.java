package com.example.invetorymanagementsystem;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class LoginPage2Activity extends AppCompatActivity {

    // Firebase Authentication instance
    private FirebaseAuth auth;
    private String verificationId;

    // Log tag for debugging
    private static final String TAG = "LoginPage2Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page_2);

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Retrieve the verificationId from Intent
        verificationId = getIntent().getStringExtra("verificationId");

        // UI Elements
        EditText passcodeInput = findViewById(R.id.passcode_input);
        Button loginButton = findViewById(R.id.login_btn);
        TextView resendPasscode = findViewById(R.id.resend_passcode);

        // Login Button Click Listener
        loginButton.setOnClickListener(v -> {
            String passcode = passcodeInput.getText().toString().trim();

            if (!passcode.isEmpty()) {
                // Verify the OTP entered by the user
                verifyCode(passcode);
            } else {
                // Show error if passcode is empty
                Toast.makeText(this, "Please enter the passcode", Toast.LENGTH_SHORT).show();
            }
        });

        // Resend Passcode Click Listener (Optional, implementation not included)
        resendPasscode.setOnClickListener(v -> {
            Log.d(TAG, "Resend passcode clicked.");
            Toast.makeText(this, "Resend functionality not implemented yet.", Toast.LENGTH_SHORT).show();
        });
    }

    // Verify the passcode
    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    // Sign in the user with PhoneAuthCredential
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Successfully signed in
                        Log.d(TAG, "Sign-in successful. Navigating to Home Page.");
                        Intent intent = new Intent(LoginPage2Activity.this, HomePageActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // Sign-in failed
                        Log.e(TAG, "Sign-in failed.");
                        Toast.makeText(LoginPage2Activity.this, "Invalid passcode. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
