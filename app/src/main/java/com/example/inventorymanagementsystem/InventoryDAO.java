package com.example.inventorymanagementsystem;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class InventoryDAO {
    private SQLiteDatabase database;

    public InventoryDAO(Context context) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
    }

    public void addItem(String name, int quantity, double price) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_NAME, name);
        values.put(DatabaseHelper.COLUMN_QUANTITY, quantity);
        values.put(DatabaseHelper.COLUMN_PRICE, price);

        database.insert(DatabaseHelper.TABLE_INVENTORY, null, values);
    }
}
