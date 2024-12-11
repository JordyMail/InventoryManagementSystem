package com.example.invetorymanagementsystem;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class AddNewGroupActivity extends AppCompatActivity {

    private static final String TAG = "AddNewGroupActivity";
    private static final int PICK_IMAGE = 1;

    private FirebaseAuth auth;
    private DatabaseReference databaseRef;
    private StorageReference storageRef;

    private EditText groupName, groupDescription;
    private Spinner groupPlace;
    private ImageView groupPhoto;
    private Button saveGroupButton;
    private Uri photoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_group);

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference("Groups");
        storageRef = FirebaseStorage.getInstance().getReference("group_photos");

        // UI Elements
        groupName = findViewById(R.id.group_name);
        groupDescription = findViewById(R.id.group_description);
        groupPlace = findViewById(R.id.group_place);
        groupPhoto = findViewById(R.id.group_photo_placeholder);
        saveGroupButton = findViewById(R.id.save_group_button);

        // Add Photo Button Click Listener
        groupPhoto.setOnClickListener(v -> openGallery());

        // Save Group Button Click Listener
        saveGroupButton.setOnClickListener(v -> saveGroup());
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
            groupPhoto.setImageURI(photoUri);
        }
    }

    private void saveGroup() {
        String name = groupName.getText().toString().trim();
        String description = groupDescription.getText().toString().trim();
        String place = groupPlace.getSelectedItem().toString();

        // Validate inputs
        if (name.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (photoUri == null) {
            Toast.makeText(this, "Please add a photo!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Generate a unique ID for the group
        String groupId = databaseRef.push().getKey();

        // Upload the photo to Firebase Storage
        StorageReference photoRef = storageRef.child(groupId + ".jpg");
        photoRef.putFile(photoUri).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Get the photo URL
                photoRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String photoUrl = uri.toString();

                    // Save the group details to Firebase Database
                    saveGroupToDatabase(groupId, name, description, place, photoUrl);
                });
            } else {
                Toast.makeText(this, "Failed to upload photo!", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Photo upload failed: " + task.getException().getMessage());
            }
        });
    }

    private void saveGroupToDatabase(String groupId, String name, String description, String place, String photoUrl) {
        // Create a map to represent the group data
        Map<String, Object> groupData = new HashMap<>();
        groupData.put("groupId", groupId);
        groupData.put("name", name);
        groupData.put("description", description);
        groupData.put("place", place);
        groupData.put("photoUrl", photoUrl);

        // Save the data to Firebase Realtime Database
        databaseRef.child(groupId).setValue(groupData).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Group saved successfully!", Toast.LENGTH_SHORT).show();
                finish(); // Close the activity
            } else {
                Toast.makeText(this, "Failed to save group!", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Database save failed: " + task.getException().getMessage());
            }
        });
    }
}
