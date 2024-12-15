package com.example.inventorymanagement;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ListInventoryActivity extends AppCompatActivity {

    private static final String TAG = "ListInventoryActivity"; // Untuk log debugging
    private RecyclerView recyclerView;
    private InventoryAdapter adapter;
    private List<InventoryItem> inventoryList = new ArrayList<>();
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_inventory);

        // Inisialisasi Firestore
        db = FirebaseFirestore.getInstance();

        // Konfigurasi RecyclerView
        recyclerView = findViewById(R.id.recyclerViewInventory);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Inisialisasi Adapter
        adapter = new InventoryAdapter(inventoryList, this::deleteInventory);
        recyclerView.setAdapter(adapter);

        // Memuat data inventaris
        loadInventory();
    }

    /**
     * Memuat daftar inventaris dari Firestore.
     */

    private void loadInventory() {
        CollectionReference inventoryRef = db.collection("inventory");

        inventoryRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        inventoryList.clear(); // Membersihkan daftar sebelum memuat ulang
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String id = document.getId();
                            String itemName = document.getString("itemName");

                            if (itemName != null) { // Cek jika field itemName tidak kosong
                                inventoryList.add(new InventoryItem(id, itemName));
                            } else {
                                Log.e(TAG, "Field 'itemName' kosong di dokumen: " + id);
                            }
                        }
                        // Memberi tahu adapter bahwa data telah berubah
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.e(TAG, "Gagal mengambil data: ", task.getException());
                        Toast.makeText(this, "Gagal memuat inventaris", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Menghapus item inventaris berdasarkan ID.
     *
     * @param id ID dokumen di Firestore.
     */
    private void deleteInventory(String id) {
        db.collection("inventory").document(id).delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Item inventaris berhasil dihapus", Toast.LENGTH_SHORT).show();
                    loadInventory(); // Memuat ulang data setelah penghapusan
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Gagal menghapus dokumen: ", e);
                    Toast.makeText(this, "Gagal menghapus item inventaris", Toast.LENGTH_SHORT).show();
                });
    }
}
