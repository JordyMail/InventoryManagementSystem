package com.example.invetorymanagementsystem;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import java.util.concurrent.TimeUnit;

public class LoginPage1Activity extends AppCompatActivity {

    // Firebase Authentication instance
    private FirebaseAuth auth;
    private String verificationId;

    // Log tag for debugging
    private static final String TAG = "LoginPage1Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page_1);

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        // UI Elements
        EditText phoneInput = findViewById(R.id.phone_input);
        Button sendPasscodeButton = findViewById(R.id.send_passcode_btn);

        // Send Passcode Button Click Listener
        sendPasscodeButton.setOnClickListener(v -> {
            String phoneNumber = phoneInput.getText().toString().trim();

            if (!phoneNumber.isEmpty()) {
                // Send OTP to the entered phone number
                sendVerificationCode(phoneNumber);
            } else {
                // Show error if phone number is empty
                Toast.makeText(this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Send OTP to the phone number
    private void sendVerificationCode(String phoneNumber) {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phoneNumber)            // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS)      // Timeout duration
                .setActivity(this)                      // Activity (for callback binding)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                        // Auto-retrieval or instant verification
                        Log.d(TAG, "Verification completed successfully.");
                        signInWithPhoneAuthCredential(credential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        // Error in verification
                        Log.e(TAG, "Verification failed: " + e.getMessage(), e);
                        Toast.makeText(LoginPage1Activity.this, "Verification Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String verificationId,
                                           @NonNull PhoneAuthProvider.ForceResendingToken token) {
                        // Code has been sent, proceed to the next activity
                        LoginPage1Activity.this.verificationId = verificationId;
                        Log.d(TAG, "OTP sent to the phone number.");

                        // Navigate to Login Page 2
                        Intent intent = new Intent(LoginPage1Activity.this, LoginPage2Activity.class);
                        intent.putExtra("verificationId", verificationId);
                        startActivity(intent);
                    }
                })
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    // Sign in the user with PhoneAuthCredential
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Successfully signed in
                        Log.d(TAG, "Sign-in successful. Navigating to Home Page.");
                        startActivity(new Intent(LoginPage1Activity.this, HomePageActivity.class));
                        finish();
                    } else {
                        // Sign-in failed
                        Log.e(TAG, "Sign-in failed.");
                        Toast.makeText(LoginPage1Activity.this, "Sign-in failed. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
