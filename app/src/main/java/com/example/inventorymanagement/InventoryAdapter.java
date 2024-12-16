package com.example.inventorymanagement;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso; // Add Picasso for image loading

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.ViewHolder> {

    private List<InventoryItem> inventoryList;
    private final OnDeleteClickListener onDeleteClickListener;
    private final Map<String, Integer> itemCountMap;

    public interface OnDeleteClickListener {
        void onDeleteClick(String itemId);
    }

    public InventoryAdapter(List<InventoryItem> inventoryList, OnDeleteClickListener onDeleteClickListener) {
        this.inventoryList = inventoryList != null ? inventoryList : new ArrayList<>();
        this.onDeleteClickListener = onDeleteClickListener;
        this.itemCountMap = new HashMap<>();
        updateItemCountMap();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inventory, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        InventoryItem item = inventoryList.get(position);

        if (item != null) {
            // Set item name or fallback to a default value
            String itemName = item.getItemName() != null ? item.getItemName() : "Unnamed Item";
            holder.tvItemName.setText(itemName);

            // Set item count
            holder.tvItemNumber.setText(String.valueOf(item.getItemCount()));

            // Load the image using Picasso (or any other image loading library)
            if (item.getPhotoUri() != null && !item.getPhotoUri().isEmpty()) {
                Uri photoUri = Uri.parse(item.getPhotoUri());
                Picasso.get().load(photoUri).into(holder.ivItemImage);  // Load image into ImageView
            } else {
                holder.ivItemImage.setImageResource(R.drawable.merah);  // Set a default placeholder
            }

            // Set delete button action
            holder.btnDelete.setOnClickListener(v -> {
                if (onDeleteClickListener != null) {
                    onDeleteClickListener.onDeleteClick(item.getId());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return inventoryList.size();
    }

    public void setAdapterData(List<InventoryItem> newInventoryList) {
        if (newInventoryList != null && !newInventoryList.equals(this.inventoryList)) {
            this.inventoryList = newInventoryList;
            updateItemCountMap();
            notifyDataSetChanged();
        }
    }

    private void updateItemCountMap() {
        itemCountMap.clear();
        for (InventoryItem item : inventoryList) {
            String itemName = item.getItemName() != null ? item.getItemName() : "Unnamed Item";
            itemCountMap.put(itemName, itemCountMap.getOrDefault(itemName, 0) + item.getItemCount());
        }
    }

    // Method to update the photo URI of a specific inventory item
    public void updateInventoryItemPhoto(String itemId, Uri newPhotoUri) {
        for (int i = 0; i < inventoryList.size(); i++) {
            InventoryItem item = inventoryList.get(i);
            if (item.getId().equals(itemId)) {
                item.setPhotoUri(newPhotoUri.toString());  // Update the photo URI
                notifyItemChanged(i);  // Notify that the item at position i has changed
                break;
            }
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvItemName, tvItemNumber;
        Button btnDelete;
        ImageView ivItemImage;  // ImageView for displaying item image

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvItemNumber = itemView.findViewById(R.id.tvItemNumber);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            ivItemImage = itemView.findViewById(R.id.ivItemImage);  // Initialize ImageView
        }
    }
}
