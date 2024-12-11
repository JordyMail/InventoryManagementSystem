package com.example.invetorymanagementsystem;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MetricsAnalyticsActivity extends AppCompatActivity {

    private TextView revenueLabel;
    private Button btn1D, btn1W, btn1M, btn3M;

    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.metrics_analytics);

        // Initialize Firebase
        databaseRef = FirebaseDatabase.getInstance().getReference("Analytics");

        // UI Elements
        revenueLabel = findViewById(R.id.revenue_label);
        btn1D = findViewById(R.id.btn_1d);
        btn1W = findViewById(R.id.btn_1w);
        btn1M = findViewById(R.id.btn_1m);
        btn3M = findViewById(R.id.btn_3m);

        // Load default data
        loadAnalyticsData("1W");

        // Button Listeners
        btn1D.setOnClickListener(v -> loadAnalyticsData("1D"));
        btn1W.setOnClickListener(v -> loadAnalyticsData("1W"));
        btn1M.setOnClickListener(v -> loadAnalyticsData("1M"));
        btn3M.setOnClickListener(v -> loadAnalyticsData("3M"));
    }

    private void loadAnalyticsData(String timeRange) {
        // Retrieve analytics data based on the time range
        databaseRef.child(timeRange).addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String revenue = snapshot.child("revenue").getValue(String.class);
                    revenueLabel.setText("Revenue: $" + revenue);
                } else {
                    Toast.makeText(MetricsAnalyticsActivity.this, "No data available for " + timeRange, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(MetricsAnalyticsActivity.this, "Failed to load data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
