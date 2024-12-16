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

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class InventoryActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_CAMERA_PERMISSION = 100;

    private ImageView ivPhoto;
    private EditText etItemName, etPlace, etDescription;
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
        btnSave = findViewById(R.id.btnSave);

        // Set listener for photo click
        ivPhoto.setOnClickListener(v -> {
            // Check for camera permission
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
            }
        });

        // Set listener for save button click
        btnSave.setOnClickListener(v -> saveInventory());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                if (data != null && data.getExtras() != null) {
                    // Set the image URI to ivPhoto
                    photoUri = data.getData();
                    ivPhoto.setImageURI(photoUri);
                }
            }
        }
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create a file URI for the photo
            Uri photoURI = Uri.parse("file://path/to/photo"); // This should be replaced with the actual file path if storing the photo in storage
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } else {
            Toast.makeText(this, "Camera not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveInventory() {
        String itemName = etItemName.getText().toString().trim();
        String place = etPlace.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        // Validate input
        if (itemName.isEmpty() || place.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a map for storing inventory data
        Map<String, Object> inventory = new HashMap<>();
        inventory.put("itemName", itemName);
        inventory.put("place", place);
        inventory.put("description", description);
        inventory.put("photoUri", photoUri != null ? photoUri.toString() : null);

        // Add the inventory item to Firestore
        db.collection("inventory")
                .add(inventory)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Inventory item added successfully", Toast.LENGTH_SHORT).show();
                    clearFields();  // Clear the input fields after saving

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to add item", Toast.LENGTH_SHORT).show();
                });
    }

    private void clearFields() {
        // Clear input fields after saving
        etItemName.setText("");
        etPlace.setText("");
        etDescription.setText("");
        ivPhoto.setImageResource(R.drawable.merah);  // Reset photo
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Camera permission is required to take a photo", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
