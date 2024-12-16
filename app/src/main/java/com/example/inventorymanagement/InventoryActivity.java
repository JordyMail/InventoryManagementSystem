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
import androidx.core.content.FileProvider;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class InventoryActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private static final int REQUEST_STORAGE_PERMISSION = 1;

    private ImageView ivPhoto;
    private EditText etItemName, etPlace, etDescription, etItemCount;
    private Button btnSave;

    private Uri photoUri;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize UI components
        ivPhoto = findViewById(R.id.ivPhoto);
        etItemName = findViewById(R.id.etItemName);
        etPlace = findViewById(R.id.etPlace);
        etDescription = findViewById(R.id.etDescription);
        etItemCount = findViewById(R.id.etItemCount);
        btnSave = findViewById(R.id.btnSave);

        // Set listener for photo click
        ivPhoto.setOnClickListener(v -> {
            // Check for camera permission
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                // Check for storage permission
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                } else {
                    // Request storage permission
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
                }
            } else {
                // Request camera permission
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
            }
        });

        // Set listener for save button click
        btnSave.setOnClickListener(v -> saveInventory());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (photoUri != null) {
                ivPhoto.setImageURI(photoUri);
            } else {
                Toast.makeText(this, "Failed to capture image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile;
            try {
                photoFile = createImageFile();
                if (photoFile != null) {
                    photoUri = FileProvider.getUriForFile(
                            this,
                            "com.example.inventorymanagement.fileprovider",
                            photoFile
                    );
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            } catch (IOException e) {
                Toast.makeText(this, "Error creating file for photo", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Camera not available", Toast.LENGTH_SHORT).show();
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(null);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    private void saveInventory() {
        String itemName = etItemName.getText().toString().trim();
        String place = etPlace.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String itemCountStr = etItemCount.getText().toString().trim();

        // Validate input
        if (itemName.isEmpty() || place.isEmpty() || description.isEmpty() || itemCountStr.isEmpty()) {
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int itemCount;
        try {
            itemCount = Integer.parseInt(itemCountStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid item count", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if item already exists in Firestore
        db.collection("inventory")
                .whereEqualTo("itemName", itemName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        int existingItemCount = querySnapshot != null && !querySnapshot.isEmpty()
                                ? querySnapshot.getDocuments().get(0).getLong("itemCount").intValue()
                                : 0;

                        // Create a map for storing inventory data
                        Map<String, Object> inventory = new HashMap<>();
                        inventory.put("itemName", itemName);
                        inventory.put("place", place);
                        inventory.put("description", description);
                        inventory.put("photoUri", photoUri != null ? photoUri.toString() : null);
                        inventory.put("itemCount", existingItemCount + itemCount);

                        // Add or update the inventory item in Firestore
                        if (existingItemCount > 0) {
                            String documentId = querySnapshot.getDocuments().get(0).getId();
                            db.collection("inventory").document(documentId).update(inventory)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(this, "Inventory item updated successfully", Toast.LENGTH_SHORT).show();
                                        clearFields();
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to update item", Toast.LENGTH_SHORT).show());
                        } else {
                            db.collection("inventory").add(inventory)
                                    .addOnSuccessListener(documentReference -> {
                                        Toast.makeText(this, "Inventory item added successfully", Toast.LENGTH_SHORT).show();
                                        clearFields();
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to add item", Toast.LENGTH_SHORT).show());
                        }
                    } else {
                        Toast.makeText(this, "Error checking item", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void clearFields() {
        etItemName.setText("");
        etPlace.setText("");
        etDescription.setText("");
        etItemCount.setText("");
        ivPhoto.setImageResource(R.drawable.merah);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Check for storage permission as well
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
                }
            } else {
                Toast.makeText(this, "Camera permission is required to take a photo", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Storage permission is required to save the photo", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
