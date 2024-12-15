package com.example.inventorymanagement;

public class InventoryItem {
    private String id;
    private String itemName;

    /**
     * Konstruktor default diperlukan untuk deserialisasi Firestore atau DataSnapshot.
     */
    public InventoryItem() {
        // Default constructor
    }

    /**
     * Konstruktor dengan parameter.
     * @param id ID dari item inventaris.
     * @param itemName Nama dari item inventaris.
     */
    public InventoryItem(String id, String itemName) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("ID tidak boleh kosong");
        }
        if (itemName == null || itemName.isEmpty()) {
            throw new IllegalArgumentException("Nama item tidak boleh kosong");
        }
        this.id = id;
        this.itemName = itemName;
    }

    // Getter
    public String getId() {
        return id;
    }

    public String getItemName() {
        return itemName;
    }

    // Setter
    public void setId(String id) {
        this.id = id;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    /**
     * Representasi objek sebagai string untuk debugging/logging.
     */
    @Override
    public String toString() {
        return "InventoryItem{" +
                "id='" + id + '\'' +
                ", itemName='" + itemName + '\'' +
                '}';
    }
}
