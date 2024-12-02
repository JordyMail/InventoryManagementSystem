package com.example.inventorymanagementsystem;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ViewInventoryActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_inventory);

        dbHelper = new DatabaseHelper(this);

        TableLayout tableLayout = findViewById(R.id.table_layout);
        populateTable(tableLayout);
    }

    private void populateTable(TableLayout tableLayout) {
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
}
