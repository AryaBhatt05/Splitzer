package com.example.Splitzer;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AssignAdapter extends RecyclerView.Adapter<AssignAdapter.AssignViewHolder> {

    private final Context context;
    private final List<Item> items;
    private final List<Person> persons;

    public AssignAdapter(Context context, List<Item> items, List<Person> persons) {
        this.context = context;
        this.items = items;
        this.persons = persons;
    }

    @NonNull
    @Override
    public AssignViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_assign_item, parent, false);
        return new AssignViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AssignViewHolder holder, int position) {
        Item item = items.get(position);

        holder.itemName.setText(item.getName());
        holder.itemQty.setText("Qty: " + item.getSharedBy());
        holder.itemPrice.setText("₹" + item.getPrice());

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item);
        for (Person person : persons) {
            spinnerAdapter.add(person.getName());
        }
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.personSpinner.setAdapter(spinnerAdapter);

        holder.sharedCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.setShared(isChecked);
            if (isChecked) {
                showMultiPersonDialog(item);
            } else {
                item.setAssignedTo(holder.personSpinner.getSelectedItem().toString());
            }
        });

        holder.personSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> adapterView, View view, int i, long l) {
                if (!holder.sharedCheck.isChecked()) {
                    item.setAssignedTo(holder.personSpinner.getSelectedItem().toString());
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> adapterView) {}
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private void showMultiPersonDialog(Item item) {
        String[] names = new String[persons.size()];
        boolean[] selected = new boolean[persons.size()];

        for (int i = 0; i < persons.size(); i++) {
            names[i] = persons.get(i).getName();
        }

        new AlertDialog.Builder(context)
                .setTitle("Select people sharing this item")
                .setMultiChoiceItems(names, selected, (dialog, which, isChecked) -> selected[which] = isChecked)
                .setPositiveButton("OK", (dialog, which) -> {
                    StringBuilder sb = new StringBuilder();
                    int count = 0;
                    for (int i = 0; i < selected.length; i++) {
                        if (selected[i]) {
                            sb.append(names[i]).append(", ");
                            count++;
                        }
                    }
                    if (sb.length() > 2) sb.setLength(sb.length() - 2);
                    item.setAssignedTo(sb.toString());
                    item.setSharedBy(count);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    public static class AssignViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, itemQty, itemPrice;
        Spinner personSpinner;
        CheckBox sharedCheck;

        public AssignViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.itemName);
            itemQty = itemView.findViewById(R.id.itemQty);
            itemPrice = itemView.findViewById(R.id.itemPrice);
            personSpinner = itemView.findViewById(R.id.personSpinner);
            sharedCheck = itemView.findViewById(R.id.sharedCheckbox);
        }
    }
}
