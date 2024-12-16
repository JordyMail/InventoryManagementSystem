package com.example.inventorymanagement;

import android.net.Uri;
import java.util.HashMap;
import java.util.Map;

public class InventoryItem {
    private String id;
    private String itemName;
    private int itemCount;  // Item count field
    private String photoUri;  // Field for storing the photo URI

    /**
     * Default constructor required for Firestore deserialization or DataSnapshot.
     */
    public InventoryItem() {
        // Default constructor for Firebase deserialization
    }

    /**
     * Constructor with parameters.
     * @param id ID of the inventory item.
     * @param itemName Name of the inventory item.
     * @param itemCount The count of this inventory item.
     * @param photoUri URI of the photo associated with the inventory item.
     */
    public InventoryItem(String id, String itemName, int itemCount, String photoUri) {
        this.id = id;
        this.itemName = itemName;
        this.itemCount = itemCount;
        this.photoUri = photoUri;
    }

    // Getter methods
    public String getId() {
        return id;
    }

    public String getItemName() {
        return itemName;
    }

    public int getItemCount() {
        return itemCount;  // Getter for itemCount
    }

    public String getPhotoUri() {
        return photoUri;  // Getter for photoUri
    }

    /**
     * Convert the photo URI from String to Uri for better handling in the app.
     */
    public Uri getPhotoUriAsUri() {
        return (photoUri != null && !photoUri.isEmpty()) ? Uri.parse(photoUri) : null;
    }

    // Setter methods
    public void setId(String id) {
        this.id = id;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;  // Setter for itemCount
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;  // Setter for photoUri
    }

    /**
     * Validates the inventory item before adding it to Firestore.
     * This ensures that both itemName and itemCount are valid.
     * @return true if the item is valid, false otherwise.
     */
    public boolean isValid() {
        return itemName != null && !itemName.trim().isEmpty() && itemCount > 0;
    }

    /**
     * Converts the object to a map for Firestore.
     * This is important when adding or updating the item in Firestore.
     */
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("itemName", itemName); // Add itemName to the map
        map.put("itemCount", itemCount); // Add itemCount to the map
        map.put("photoUri", photoUri); // Add photoUri to the map
        return map;
    }

    /**
     * String representation of the object for debugging/logging.
     */
    @Override
    public String toString() {
        return "InventoryItem{" +
                "id='" + id + '\'' +
                ", itemName='" + itemName + '\'' +
                ", itemCount=" + itemCount +  // Include itemCount in the string representation
                ", photoUri='" + photoUri + '\'' +  // Include photoUri in the string representation
                '}';
    }
}
