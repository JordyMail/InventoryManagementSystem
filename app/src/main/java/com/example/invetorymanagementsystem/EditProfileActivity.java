package com.example.invetorymanagementsystem;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditProfileActivity extends AppCompatActivity {

    private static final String TAG = "EditProfileActivity";

    private FirebaseAuth auth;
    private DatabaseReference databaseRef;

    private EditText editName, editPhone, editDob, editCountry;
    private ImageView userAvatar, changePhotoIcon;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference("Users");

        // UI Elements
        editName = findViewById(R.id.edit_name);
        editPhone = findViewById(R.id.edit_phone);
        editDob = findViewById(R.id.edit_dob);
        editCountry = findViewById(R.id.edit_country);
        userAvatar = findViewById(R.id.user_avatar);
        changePhotoIcon = findViewById(R.id.change_photo_icon);
        saveButton = findViewById(R.id.save_button);

        // Load current user data
        loadUserData();

        // Save Button Listener
        saveButton.setOnClickListener(v -> saveChanges());
    }

    // Load user data from Firebase
    private void loadUserData() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            databaseRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String name = snapshot.child("name").getValue(String.class);
                        String phone = snapshot.child("phone").getValue(String.class);
                        String dob = snapshot.child("dob").getValue(String.class);
                        String country = snapshot.child("country").getValue(String.class);

                        // Populate fields
                        editName.setText(name);
                        editPhone.setText(phone);
                        editDob.setText(dob);
                        editCountry.setText(country);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, "Error loading user data: " + error.getMessage());
                }
            });
        }
    }

    // Save changes to Firebase
    private void saveChanges() {
        String name = editName.getText().toString().trim();
        String phone = editPhone.getText().toString().trim();
        String dob = editDob.getText().toString().trim();
        String country = editCountry.getText().toString().trim();

        if (name.isEmpty() || phone.isEmpty() || dob.isEmpty() || country.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            databaseRef.child(userId).child("name").setValue(name);
            databaseRef.child(userId).child("phone").setValue(phone);
            databaseRef.child(userId).child("dob").setValue(dob);
            databaseRef.child(userId).child("country").setValue(country);

            Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
        }
    }
}
