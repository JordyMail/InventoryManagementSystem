package com.example.inventorymanagement;

import android.net.Uri;
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

        // Initialize Firestore instance
        db = FirebaseFirestore.getInstance();

        // Configure RecyclerView
        recyclerView = findViewById(R.id.recyclerViewInventory);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize and set Adapter for RecyclerView
        adapter = new InventoryAdapter(inventoryList, this::deleteInventory);
        recyclerView.setAdapter(adapter);

        // Load the inventory data from Firestore
        loadInventory();
    }

    /**
     * Fetches inventory data from Firestore and updates the RecyclerView.
     */
    private void loadInventory() {
        CollectionReference inventoryRef = db.collection("inventory");

        inventoryRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        inventoryList.clear(); // Clear the list before reloading new data
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String id = document.getId();
                            String itemName = document.getString("itemName");
                            Long itemCountLong = document.getLong("itemCount");
                            String photoUri = document.getString("photoUri");  // Fetching the photo URI

                            // Null-safe check for missing fields
                            if (itemName != null && !itemName.trim().isEmpty()) {
                                int itemCount = (itemCountLong != null) ? itemCountLong.intValue() : 0;
                                // Create new InventoryItem with photoUri
                                inventoryList.add(new InventoryItem(id, itemName, itemCount, photoUri));
                            } else {
                                Log.e(TAG, "Field 'itemName' is missing or empty in document: " + id);
                            }
                        }
                        // Notify the adapter that the data has changed
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
     * Deletes an inventory item by its Firestore document ID.
     *
     * @param id The document ID of the item to be deleted.
     */
    private void deleteInventory(@NonNull String id) {
        db.collection("inventory").document(id).delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Inventory item deleted successfully", Toast.LENGTH_SHORT).show();
                    loadInventory(); // Reload inventory after successful deletion
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to delete document: ", e);
                    Toast.makeText(this, "Failed to delete inventory item", Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Updates the photo of a specific inventory item in Firestore and in the adapter.
     *
     * @param itemId   The ID of the item to update.
     * @param photoUri The new photo URI.
     */
    public void updateInventoryItemPhoto(String itemId, Uri photoUri) {
        if (photoUri == null) {
            Log.e(TAG, "Photo URI is invalid.");
            Toast.makeText(this, "Invalid photo URI", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update Firestore with the new photo URI
        db.collection("inventory").document(itemId)
                .update("photoUri", photoUri.toString())
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Photo updated successfully", Toast.LENGTH_SHORT).show();
                    // Update the adapter with the new photo URI
                    adapter.updateInventoryItemPhoto(itemId, photoUri);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to update photo URI: ", e);
                    Toast.makeText(this, "Failed to update photo", Toast.LENGTH_SHORT).show();
                });
    }
}
