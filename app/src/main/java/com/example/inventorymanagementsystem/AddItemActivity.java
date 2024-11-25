package com.example.inventorymanagementsystem;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AddItemActivity extends AppCompatActivity {

    private EditText etItemName, etQuantity, etPrice;
    private Button btnAddItem;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        // Initialize views
        etItemName = findViewById(R.id.et_item_name);
        etQuantity = findViewById(R.id.et_quantity);
        etPrice = findViewById(R.id.et_price);
        btnAddItem = findViewById(R.id.btn_add_item);

        // Initialize database helper
        dbHelper = new DatabaseHelper(this);

        // Set button click listener
        btnAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItemToDatabase();
            }
        });
    }

    private void addItemToDatabase() {
        String itemName = etItemName.getText().toString().trim();
        String quantityStr = etQuantity.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();

        if (TextUtils.isEmpty(itemName) || TextUtils.isEmpty(quantityStr) || TextUtils.isEmpty(priceStr)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int quantity = Integer.parseInt(quantityStr);
        double price = Double.parseDouble(priceStr);

        // Insert into database
        InventoryDAO inventoryDAO = new InventoryDAO(this);
        inventoryDAO.addItem(itemName, quantity, price);

        Toast.makeText(this, "Item added successfully!", Toast.LENGTH_SHORT).show();

        // Clear fields
        etItemName.setText("");
        etQuantity.setText("");
        etPrice.setText("");
    }
}
