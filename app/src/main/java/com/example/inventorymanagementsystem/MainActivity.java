package com.example.inventorymanagementsystem;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class MainActivity extends AppCompatActivity {

    // DatabaseHelper instance
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable edge-to-edge and set layout
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize the DatabaseHelper
        dbHelper = new DatabaseHelper(this);

        // Connect UI elements
        Button addButton = findViewById(R.id.btn_add);
        Button viewButton = findViewById(R.id.btn_view);
        Button updateButton = findViewById(R.id.btn_update);
        Button deleteButton = findViewById(R.id.btn_delete);

        EditText itemName = findViewById(R.id.item_name);
        EditText itemQuantity = findViewById(R.id.item_quantity);
        EditText itemPrice = findViewById(R.id.item_price);

        // Add Button Listener
        addButton.setOnClickListener(v -> {
            String name = itemName.getText().toString();
            int quantity = Integer.parseInt(itemQuantity.getText().toString());
            double price = Double.parseDouble(itemPrice.getText().toString());
            addData(name, quantity, price);
        });

        // View Button Listener
        viewButton.setOnClickListener(v -> fetchData());

        // Update Button Listener
        updateButton.setOnClickListener(v -> {
            String name = itemName.getText().toString();
            int quantity = Integer.parseInt(itemQuantity.getText().toString());
            updateData(name, quantity);
        });

        // Delete Button Listener
        deleteButton.setOnClickListener(v -> {
            String name = itemName.getText().toString();
            deleteData(name);
        });
    }

    // Add data to the database
    private void addData(String name, int quantity, double price) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_NAME, name);
        values.put(DatabaseHelper.COLUMN_QUANTITY, quantity);
        values.put(DatabaseHelper.COLUMN_PRICE, price);

        long newRowId = database.insert(DatabaseHelper.TABLE_INVENTORY, null, values);
        Log.d("MainActivity", "Inserted new row with ID: " + newRowId);
        database.close();
    }

    // Fetch data and populate the table
    private void fetchData() {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.query(
                DatabaseHelper.TABLE_INVENTORY,
                null, // Retrieve all columns
                null, // No WHERE clause
                null, // No WHERE arguments
                null, // No GROUP BY
                null, // No HAVING
                null  // Default ORDER BY
        );

        TableLayout tableLayout = findViewById(R.id.table_layout);
        tableLayout.removeAllViews(); // Clear previous data

        // Add table headers
        TableRow headerRow = new TableRow(this);
        headerRow.setPadding(8, 8, 8, 8);

        TextView headerName = new TextView(this);
        headerName.setText("Name");
        headerName.setPadding(8, 8, 8, 8);
        headerRow.addView(headerName);

        TextView headerQuantity = new TextView(this);
        headerQuantity.setText("Quantity");
        headerQuantity.setPadding(8, 8, 8, 8);
        headerRow.addView(headerQuantity);

        TextView headerPrice = new TextView(this);
        headerPrice.setText("Price");
        headerPrice.setPadding(8, 8, 8, 8);
        headerRow.addView(headerPrice);

        tableLayout.addView(headerRow);

        // Populate table rows
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME));
            int quantity = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_QUANTITY));
            double price = cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRICE));

            TableRow row = new TableRow(this);
            row.setPadding(8, 8, 8, 8);

            TextView nameCell = new TextView(this);
            nameCell.setText(name);
            nameCell.setPadding(8, 8, 8, 8);
            row.addView(nameCell);

            TextView quantityCell = new TextView(this);
            quantityCell.setText(String.valueOf(quantity));
            quantityCell.setPadding(8, 8, 8, 8);
            row.addView(quantityCell);

            TextView priceCell = new TextView(this);
            priceCell.setText(String.valueOf(price));
            priceCell.setPadding(8, 8, 8, 8);
            row.addView(priceCell);

            tableLayout.addView(row);
        }

        cursor.close();
        database.close();
    }

    // Update data in the database
    private void updateData(String name, int newQuantity) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_QUANTITY, newQuantity);

        String selection = DatabaseHelper.COLUMN_NAME + " = ?";
        String[] selectionArgs = {name};

        int rowsAffected = database.update(DatabaseHelper.TABLE_INVENTORY, values, selection, selectionArgs);
        Log.d("MainActivity", "Updated rows: " + rowsAffected);
        database.close();
    }

    // Delete data from the database
    private void deleteData(String name) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        String selection = DatabaseHelper.COLUMN_NAME + " = ?";
        String[] selectionArgs = {name};

        int rowsDeleted = database.delete(DatabaseHelper.TABLE_INVENTORY, selection, selectionArgs);
        Log.d("MainActivity", "Deleted rows: " + rowsDeleted);
        database.close();
    }
}
