package com.example.invetorymanagementsystem;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class AddNewItemActivity extends AppCompatActivity {

    private static final String TAG = "AddNewItemActivity";
    private static final int PICK_IMAGE = 1;

    private FirebaseAuth auth;
    private DatabaseReference databaseRef;
    private StorageReference storageRef;

    private EditText itemName, itemSupplier, itemPrice, itemDescription;
    private Spinner itemPlace, itemGroup;
    private Switch lowQuantityAlert;
    private ImageView itemPhoto;
    private Button saveButton;
    private Uri photoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_item);

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference("Items");
        storageRef = FirebaseStorage.getInstance().getReference("item_photos");

        // UI Elements
        itemName = findViewById(R.id.item_name);
        itemSupplier = findViewById(R.id.item_supplier);
        itemPrice = findViewById(R.id.item_price);
        itemDescription = findViewById(R.id.item_description);
        itemPlace = findViewById(R.id.item_place);
        itemGroup = findViewById(R.id.item_group);
        lowQuantityAlert = findViewById(R.id.low_quantity_alert);
        itemPhoto = findViewById(R.id.item_photo_placeholder);
        saveButton = findViewById(R.id.save_button);

        // Add Photo Button Click Listener
        itemPhoto.setOnClickListener(v -> openGallery());

        // Save Item Button Click Listener
        saveButton.setOnClickListener(v -> saveItem());
    }

    private void openGallery() {
        // Open the gallery to choose a photo
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            photoUri = data.getData();
            itemPhoto.setImageURI(photoUri);
        }
    }

    private void saveItem() {
        String name = itemName.getText().toString().trim();
        String supplier = itemSupplier.getText().toString().trim();
        String price = itemPrice.getText().toString().trim();
        String description = itemDescription.getText().toString().trim();
        boolean isLowQuantityAlert = lowQuantityAlert.isChecked();

        // Validate input fields
        if (name.isEmpty() || supplier.isEmpty() || price.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (photoUri == null) {
            Toast.makeText(this, "Please add a photo!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Generate a unique ID for the item
        String itemId = databaseRef.push().getKey();

        // Upload the photo to Firebase Storage
        StorageReference photoRef = storageRef.child(itemId + ".jpg");
        photoRef.putFile(photoUri).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Get the photo URL
                photoRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String photoUrl = uri.toString();

                    // Save the item details to Firebase Database
                    saveItemToDatabase(itemId, name, supplier, price, description, isLowQuantityAlert, photoUrl);
                });
            } else {
                Toast.makeText(this, "Failed to upload photo!", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Photo upload failed: " + task.getException().getMessage());
            }
        });
    }

    private void saveItemToDatabase(String itemId, String name, String supplier, String price, String description,
                                    boolean isLowQuantityAlert, String photoUrl) {
        // Create a map to represent the item data
        Map<String, Object> itemData = new HashMap<>();
        itemData.put("itemId", itemId);
        itemData.put("name", name);
        itemData.put("supplier", supplier);
        itemData.put("price", price);
        itemData.put("description", description);
        itemData.put("lowQuantityAlert", isLowQuantityAlert);
        itemData.put("photoUrl", photoUrl);

        // Save the data to Firebase Realtime Database
        databaseRef.child(itemId).setValue(itemData).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Item saved successfully!", Toast.LENGTH_SHORT).show();
                finish(); // Close the activity
            } else {
                Toast.makeText(this, "Failed to save item!", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Database save failed: " + task.getException().getMessage());
            }
        });
    }
}

