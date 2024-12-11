package com.example.invetorymanagementsystem;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class HomePageActivity extends AppCompatActivity {

    private static final String TAG = "HomePageActivity";

    private FirebaseAuth auth;
    private DatabaseReference databaseRef;

    private TextView welcomeText, userName;
    private ImageView notificationIcon, menuIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference("Users");

        // UI Elements
        welcomeText = findViewById(R.id.welcome_text);
        userName = findViewById(R.id.user_name);
        notificationIcon = findViewById(R.id.notification_icon);
        menuIcon = findViewById(R.id.menu_icon);

        // Load user details
        loadUserDetails();

        // Set navigation listeners for cards
        findViewById(R.id.inventory_card).setOnClickListener(v -> {
            // Navigate to Inventory Page
            Toast.makeText(this, "Navigate to Inventory Page", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.analytics_card).setOnClickListener(v -> {
            // Navigate to Analytics Page
            Toast.makeText(this, "Navigate to Analytics Page", Toast.LENGTH_SHORT).show();
        });

        // Menu Icon Listener
        menuIcon.setOnClickListener(v -> {
            // Open Menu
            Toast.makeText(this, "Menu Icon Clicked", Toast.LENGTH_SHORT).show();
        });

        // Notification Icon Listener
        notificationIcon.setOnClickListener(v -> {
            // Open Notifications
            Toast.makeText(this, "Notification Icon Clicked", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadUserDetails() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            databaseRef.child(userId).addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String name = snapshot.child("name").getValue(String.class);
                        String id = snapshot.child("id").getValue(String.class);

                        if (name != null && id != null) {
                            userName.setText(String.format("%s (%s)", name, id));
                        } else {
                            Log.w(TAG, "Missing name or ID in user data");
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, "Failed to load user details: " + error.getMessage());
                }
            });
        } else {
            Log.w(TAG, "No authenticated user found");
        }
    }
}
