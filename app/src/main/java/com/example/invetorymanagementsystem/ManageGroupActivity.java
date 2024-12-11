package com.example.invetorymanagementsystem;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ManageGroupsActivity extends AppCompatActivity {

    private static final String TAG = "ManageGroupsActivity";

    private EditText searchBar;
    private RecyclerView groupListRecyclerView;
    private GroupAdapter groupAdapter;
    private List<Group> groupList;

    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_groups);

        // Initialize Firebase
        databaseRef = FirebaseDatabase.getInstance().getReference("Groups");

        // UI Elements
        searchBar = findViewById(R.id.search_bar);
        groupListRecyclerView = findViewById(R.id.group_list);

        // RecyclerView Setup
        groupList = new ArrayList<>();
        groupAdapter = new GroupAdapter(groupList, this::onGroupItemClick, this::onDeleteGroupClick);
        groupListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        groupListRecyclerView.setAdapter(groupAdapter);

        // Load groups from Firebase
        loadGroups();

        // Search Bar Listener
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterGroups(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadGroups() {
        databaseRef.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                groupList.clear();
                for (DataSnapshot groupSnapshot : snapshot.getChildren()) {
                    Group group = groupSnapshot.getValue(Group.class);
                    groupList.add(group);
                }
                groupAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to load groups: " + error.getMessage());
            }
        });
    }

    private void filterGroups(String query) {
        List<Group> filteredList = new ArrayList<>();
        for (Group group : groupList) {
            if (group.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(group);
            }
        }
        groupAdapter.updateList(filteredList);
    }

    private void onGroupItemClick(Group group) {
        // Navigate to Edit Group Activity
        Intent intent = new Intent(this, EditGroupActivity.class);
        intent.putExtra("groupId", group.getGroupId());
        startActivity(intent);
    }

    private void onDeleteGroupClick(Group group) {
        // Delete group from Firebase
        databaseRef.child(group.getGroupId()).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Group deleted successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to delete group!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

