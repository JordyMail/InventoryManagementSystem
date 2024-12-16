package com.example.inventorymanagement;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.ViewHolder> {

    private List<InventoryItem> inventoryList;
    private final OnDeleteClickListener onDeleteClickListener;

    /**
     * Interface for handling delete actions.
     */
    public interface OnDeleteClickListener {
        void onDeleteClick(String id);
    }

    /**
     * Constructor for InventoryAdapter.
     *
     * @param inventoryList          List of inventory items.
     * @param onDeleteClickListener  Listener for delete button actions.
     */
    public InventoryAdapter(List<InventoryItem> inventoryList, OnDeleteClickListener onDeleteClickListener) {
        this.inventoryList = inventoryList != null ? inventoryList : new ArrayList<>(); // Null safety
        this.onDeleteClickListener = onDeleteClickListener;
    }

    /**
     * Creates a ViewHolder for RecyclerView items.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inventory, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Binds data to the ViewHolder.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        InventoryItem item = inventoryList.get(position);

        if (item != null) { // Null safety
            holder.tvItemName.setText(item.getItemName() != null ? item.getItemName() : "No Name Available"); // Fallback for missing name
            holder.btnDelete.setOnClickListener(v -> {
                if (onDeleteClickListener != null) {
                    onDeleteClickListener.onDeleteClick(item.getId());
                }
            });
        }
    }

    /**
     * Returns the total number of items in the list.
     */
    @Override
    public int getItemCount() {
        return inventoryList.size();
    }

    /**
     * Updates the inventory data in the adapter and refreshes the view.
     *
     * @param newInventoryList The updated inventory list.
     */
    public void setAdapterData(List<InventoryItem> newInventoryList) {
        if (newInventoryList != null && !newInventoryList.equals(this.inventoryList)) {
            this.inventoryList = newInventoryList;
            notifyDataSetChanged();
        }
    }

    /**
     * ViewHolder class for binding item views.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvItemName;
        Button btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
