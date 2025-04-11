package com.example.Splitzer;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.EditText;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;

public class EditableItemAdapter extends RecyclerView.Adapter<EditableItemAdapter.ItemViewHolder> {

    private final List<EditableItem> items;
    private final OnItemChangedListener listener;

    public interface OnItemChangedListener {
        void onItemChanged();
    }

    public EditableItemAdapter(List<EditableItem> items, OnItemChangedListener listener) {
        this.items = items;
        this.listener = listener;
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        EditText nameEditText, qtyEditText, priceEditText;
        TextView totalText;
        ImageView deleteIcon;

        public ItemViewHolder(View view) {
            super(view);
            nameEditText = view.findViewById(R.id.itemName);
            qtyEditText = view.findViewById(R.id.itemQty);
            priceEditText = view.findViewById(R.id.itemPrice);
            totalText = view.findViewById(R.id.itemTotal);
            deleteIcon = view.findViewById(R.id.deleteItem);
        }
    }

    @NonNull
    @Override
    public EditableItemAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item, parent, false);
        return new ItemViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        EditableItem item = items.get(position);

        // Remove existing watchers to avoid conflict
        if (holder.nameEditText.getTag() instanceof TextWatcher)
            holder.nameEditText.removeTextChangedListener((TextWatcher) holder.nameEditText.getTag());

        if (holder.qtyEditText.getTag() instanceof TextWatcher)
            holder.qtyEditText.removeTextChangedListener((TextWatcher) holder.qtyEditText.getTag());

        if (holder.priceEditText.getTag() instanceof TextWatcher)
            holder.priceEditText.removeTextChangedListener((TextWatcher) holder.priceEditText.getTag());

        // Set values
        holder.nameEditText.setText(item.getName());
        holder.qtyEditText.setText(String.valueOf(item.getQuantity()));
        holder.priceEditText.setText(String.valueOf(item.getPrice()));
        holder.totalText.setText("₹" + item.getTotal());

        // Add watcher
        TextWatcher watcher = new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void afterTextChanged(Editable s) {
                try {
                    item.setName(holder.nameEditText.getText().toString());
                    item.setQuantity(Integer.parseInt(holder.qtyEditText.getText().toString()));
                    item.setPrice(Double.parseDouble(holder.priceEditText.getText().toString()));
                    holder.totalText.setText("₹" + item.getTotal());
                    if (listener != null) listener.onItemChanged();
                } catch (Exception ignored) {}
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        };

        // Reassign cleanly
        holder.nameEditText.addTextChangedListener(watcher);
        holder.qtyEditText.addTextChangedListener(watcher);
        holder.priceEditText.addTextChangedListener(watcher);

        // Attach tag for cleanup later
        holder.nameEditText.setTag(watcher);
        holder.qtyEditText.setTag(watcher);
        holder.priceEditText.setTag(watcher);

        // Delete icon click
        holder.deleteIcon.setOnClickListener(v -> {
            if (position != RecyclerView.NO_POSITION) {
                items.remove(position);
                notifyItemRemoved(position);
                if (listener != null) listener.onItemChanged();
            }
        });
    }


    @Override
    public int getItemCount() {
        return items.size();
    }
}
