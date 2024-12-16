package com.example.inventorymanagement;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ListInventoryActivity extends AppCompatActivity {

    private static final String TAG = "ListInventoryActivity"; // Tag for debugging
    private RecyclerView recyclerView;
    private InventoryAdapter adapter;
    private final List<InventoryItem> inventoryList = new ArrayList<>();
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_inventory);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Configure RecyclerView
        recyclerView = findViewById(R.id.recyclerViewInventory);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize Adapter
        adapter = new InventoryAdapter(inventoryList, this::deleteInventory);
        recyclerView.setAdapter(adapter);

        // Load inventory data
        loadInventory();
    }

    /**
     * Loads the inventory list from Firestore.
     */
    private void loadInventory() {
        CollectionReference inventoryRef = db.collection("inventory");

        inventoryRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        inventoryList.clear(); // Clear the list before reloading
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String id = document.getId();
                            String itemName = document.getString("itemName");

                            if (itemName != null && !itemName.trim().isEmpty()) { // Check if 'itemName' is not null or empty
                                inventoryList.add(new InventoryItem(id, itemName));
                            } else {
                                Log.e(TAG, "Field 'itemName' is missing or empty in document: " + id);
                            }
                        }
                        // Notify the adapter about data changes
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.e(TAG, "Failed to fetch data: ", task.getException());
                        Toast.makeText(this, "Failed to load inventory", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error retrieving inventory: ", e);
                    Toast.makeText(this, "Error loading inventory", Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Deletes an inventory item by its ID.
     *
     * @param id Document ID in Firestore.
     */
    private void deleteInventory(@NonNull String id) {
        db.collection("inventory").document(id).delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Inventory item deleted successfully", Toast.LENGTH_SHORT).show();
                    loadInventory(); // Reload data after deletion
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to delete document: ", e);
                    Toast.makeText(this, "Failed to delete inventory item", Toast.LENGTH_SHORT).show();
                });
    }
}
