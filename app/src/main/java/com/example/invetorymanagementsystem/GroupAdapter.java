package com.example.invetorymanagementsystem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder> {

    private final List<Group> groupList;
    private final OnGroupClickListener onGroupClickListener;
    private final OnDeleteClickListener onDeleteClickListener;

    public interface OnGroupClickListener {
        void onGroupClick(Group group);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(Group group);
    }

    public GroupAdapter(List<Group> groupList, OnGroupClickListener onGroupClickListener, OnDeleteClickListener onDeleteClickListener) {
        this.groupList = groupList;
        this.onGroupClickListener = onGroupClickListener;
        this.onDeleteClickListener = onDeleteClickListener;
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_list_item, parent, false);
        return new GroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        Group group = groupList.get(position);
        holder.groupName.setText(group.getName());
        holder.groupDescription.setText(group.getDescription());
        holder.itemView.setOnClickListener(v -> onGroupClickListener.onGroupClick(group));
        holder.deleteButton.setOnClickListener(v -> onDeleteClickListener.onDeleteClick(group));
    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }

    public void updateList(List<Group> newList) {
        groupList.clear();
        groupList.addAll(newList);
        notifyDataSetChanged();
    }

    public static class GroupViewHolder extends RecyclerView.ViewHolder {
        TextView groupName, groupDescription;
        ImageButton deleteButton;

        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            groupName = itemView.findViewById(R.id.group_name);
            groupDescription = itemView.findViewById(R.id.group_description);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }
    }
}
