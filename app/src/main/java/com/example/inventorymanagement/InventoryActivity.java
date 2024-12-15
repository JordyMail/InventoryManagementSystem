package com.example.inventorymanagement;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_CAMERA_PERMISSION = 100;

    private ImageView ivPhoto;
    private EditText etItemName, etPlace, etDescription;
    private Button btnSave;

    private Uri photoUri;
    private List<Map<String, Object>> inventoryList; // List untuk menyimpan item inventaris

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        // Inisialisasi list inventaris
        inventoryList = new ArrayList<>();

        // Menghubungkan komponen UI
        ivPhoto = findViewById(R.id.ivPhoto);
        etItemName = findViewById(R.id.etItemName);
        etPlace = findViewById(R.id.etPlace);
        etDescription = findViewById(R.id.etDescription);
        btnSave = findViewById(R.id.btnSave);

        // Set listener untuk foto
        ivPhoto.setOnClickListener(v -> {
            // Cek izin akses kamera
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
            }
        });

        // Logika tombol simpan
        btnSave.setOnClickListener(v -> saveInventory());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE && photoUri != null) {
                ivPhoto.setImageURI(photoUri); // Menampilkan foto yang diambil
            }
        }
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } else {
            Toast.makeText(this, "Kamera tidak tersedia", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveInventory() {
        String itemName = etItemName.getText().toString().trim();
        String place = etPlace.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        // Validasi input
        if (itemName.isEmpty() || place.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Harap isi semua kolom", Toast.LENGTH_SHORT).show();
            return;
        }

        // Membuat map untuk menyimpan data inventaris
        Map<String, Object> inventory = new HashMap<>();
        inventory.put("itemName", itemName);
        inventory.put("place", place);
        inventory.put("description", description);
        inventory.put("photoUri", photoUri != null ? photoUri.toString() : null);

        // Menambahkan item baru ke dalam list inventaris
        inventoryList.add(inventory);

        // Memberi notifikasi kepada pengguna
        Toast.makeText(this, "Inventaris berhasil disimpan!", Toast.LENGTH_SHORT).show();

        // Mengosongkan input setelah menyimpan
        etItemName.setText("");
        etPlace.setText("");
        etDescription.setText("");
        ivPhoto.setImageResource(R.drawable.merah); // Reset area foto

        // Opsional, menampilkan atau mencatat daftar inventaris yang diperbarui
        // Sebagai contoh, mencatat daftar inventaris saat ini:
        for (Map<String, Object> item : inventoryList) {
            System.out.println(item);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Izin kamera diperlukan untuk mengambil foto", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
