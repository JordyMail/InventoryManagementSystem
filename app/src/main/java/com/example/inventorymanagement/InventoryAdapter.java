package com.example.inventorymanagement;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.ViewHolder> {

    private List<InventoryItem> inventoryList;
    private OnDeleteClickListener onDeleteClickListener;

    /**
     * Interface untuk menangani aksi hapus item.
     */
    public interface OnDeleteClickListener {
        void onDeleteClick(String id);
    }

    /**
     * Konstruktor untuk InventoryAdapter.
     */
    public InventoryAdapter(List<InventoryItem> inventoryList, OnDeleteClickListener onDeleteClickListener) {
        this.inventoryList = inventoryList != null ? inventoryList : new java.util.ArrayList<>(); // Null safety
        this.onDeleteClickListener = onDeleteClickListener;
    }

    /**
     * Membuat ViewHolder untuk RecyclerView.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inventory, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Mengikat data ke ViewHolder.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        InventoryItem item = inventoryList.get(position);

        if (item != null) { // Null safety
            holder.tvItemName.setText(item.getItemName() != null ? item.getItemName() : "No Name"); // Handle itemName kosong

            holder.btnDelete.setOnClickListener(v -> {
                if (onDeleteClickListener != null) {
                    onDeleteClickListener.onDeleteClick(item.getId());
                }
            });
        }
    }

    /**
     * Mendapatkan jumlah item dalam RecyclerView.
     */
    @Override
    public int getItemCount() {
        return inventoryList.size();
    }

    /**
     * Metode untuk memperbarui data dalam adapter.
     */
    public void setAdapterData(List<InventoryItem> newInventoryList) {
        if (newInventoryList != null) {
            this.inventoryList = newInventoryList;
            notifyDataSetChanged();
        }
    }

    /**
     * Kelas ViewHolder untuk mengikat view di item layout.
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
