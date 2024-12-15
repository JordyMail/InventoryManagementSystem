package com.example.inventorymanagement;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    private Button btnInventory, btnViewInventory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Menghubungkan tombol dengan ID di XML
        btnInventory = findViewById(R.id.btnAddInventory);
        btnViewInventory = findViewById(R.id.btnViewInventory);

        // Listener tombol "Add Inventory"
        btnInventory.setOnClickListener(v -> {
            // Berpindah ke halaman InventoryActivity
            Intent intent = new Intent(HomeActivity.this, InventoryActivity.class);
            startActivity(intent);
        });

        // Listener tombol "View Inventory"
        btnViewInventory.setOnClickListener(v -> {
            // Berpindah ke halaman ListInventoryActivity
            Intent intent = new Intent(HomeActivity.this, ListInventoryActivity.class);
            startActivity(intent);
        });
    }
}
