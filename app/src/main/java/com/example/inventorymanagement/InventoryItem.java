package com.example.inventorymanagement;

import java.util.HashMap;
import java.util.Map;

public class InventoryItem {
    private String id;
    private String itemName;

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
     */
    public InventoryItem(String id, String itemName) {
        this.id = id;
        this.itemName = itemName;
    }

    // Getter methods
    public String getId() {
        return id;
    }

    public String getItemName() {
        return itemName;
    }

    // Setter methods
    public void setId(String id) {
        this.id = id;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    /**
     * Converts the object to a map for Firestore.
     */
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("itemName", itemName); // Add itemName to the map
        return map;
    }

    /**
     * A method to validate the inventory item data before adding it to Firestore.
     * @return true if the item is valid, false otherwise.
     */
    public boolean isValid() {
        return itemName != null && !itemName.trim().isEmpty();
    }

    /**
     * String representation of the object for debugging/logging.
     */
    @Override
    public String toString() {
        return "InventoryItem{" +
                "id='" + id + '\'' +
                ", itemName='" + itemName + '\'' +
                '}';
    }
}
