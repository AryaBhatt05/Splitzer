package com.example.Splitzer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ReviewAndSaveActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditableItemAdapter adapter;
    private TextView grandTotalText;
    private Button addItemBtn, saveContinueBtn;

    private List<EditableItem> editableItemList = new ArrayList<>();
    private List<Person> personList;
    private String rawText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_and_save);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        grandTotalText = findViewById(R.id.grandTotalText);
        addItemBtn = findViewById(R.id.addItemButton);
        saveContinueBtn = findViewById(R.id.saveBillButton);

        rawText = getIntent().getStringExtra("rawText");
        String peopleJson = getIntent().getStringExtra("peopleList");

        if (peopleJson == null || rawText == null) {
            Toast.makeText(this, "Missing data", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Type personListType = new TypeToken<List<Person>>() {}.getType();
        personList = new Gson().fromJson(peopleJson, personListType);

        parseItemsFromRawText(rawText);
        adapter = new EditableItemAdapter(editableItemList, this::recalculateGrandTotal);
        recyclerView.setAdapter(adapter);
        recalculateGrandTotal();

        ItemTouchHelper swipeHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                editableItemList.remove(position);
                adapter.notifyItemRemoved(position);
                recalculateGrandTotal();
            }
        });
        swipeHelper.attachToRecyclerView(recyclerView);

        addItemBtn.setOnClickListener(v -> {
            editableItemList.add(new EditableItem("", 1, 0.0));
            adapter.notifyItemInserted(editableItemList.size() - 1);
        });

        saveContinueBtn.setOnClickListener(v -> {
            List<Item> finalItems = new ArrayList<>();
            for (EditableItem e : editableItemList) {
                if (e.getName().trim().isEmpty()) continue;

                // Assuming e.getQuantity() is a valid method to get the quantity of the item
                Item item = new Item(e.getName(), e.getPrice(), e.getQuantity()); // Pass quantity along with name and price

                item.setSharedBy(e.getQuantity());
                finalItems.add(item);
            }

            if (finalItems.isEmpty()) {
                Toast.makeText(this, "Please add at least one valid item", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(ReviewAndSaveActivity.this, AssignActivity.class);
            intent.putExtra("itemList", new Gson().toJson(finalItems));
            intent.putExtra("personList", new Gson().toJson(personList));
            startActivity(intent);
            Log.d("DEBUG_FLOW", "Navigated to AssignActivity with " + finalItems.size() + " items.");
        });
    }

    private void parseItemsFromRawText(String text) {
        String[] lines = text.split("\n");

        // Loop through all lines
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();

            // Skip irrelevant lines based on common bill terms and header/footer words
            if (line.isEmpty() || containsIrrelevantTerm(line)) {
                continue;  // Skip these lines
            }

            // Check if the line has a food item pattern (looking for a valid food name, usually with letters)
            if (line.matches(".*[a-zA-Z].*") && !line.equalsIgnoreCase("particulars")) {
                String name = line;
                double price = 0.0;
                int quantity = 1;

                // Check if the next line contains a price (if so, it must be a number)
                if (i + 1 < lines.length && lines[i + 1].matches(".*\\d+(\\.\\d+)?")) {
                    try {
                        price = Double.parseDouble(lines[i + 1].replaceAll("[^\\d.]", "")); // Extract price
                        i++;  // Skip the next line as it's the price
                    } catch (Exception ignored) {}
                }

                // Check if quantity is mentioned in the current line (e.g., "2 x Item Name")
                if (line.matches("\\d+\\s*x\\s*.*")) {
                    try {
                        quantity = Integer.parseInt(line.split("\\s*x\\s*")[0]); // Extract quantity
                        name = line.split("\\s*x\\s*")[1].trim();  // Get the name of the item after 'x'
                    } catch (Exception ignored) {}
                }

                // Add the item to the list of editable items
                editableItemList.add(new EditableItem(name, quantity, price));
            }
        }
    }

    // Helper method to identify and skip irrelevant lines (headers/footers)
    private boolean containsIrrelevantTerm(String line) {
        // Define terms commonly found in the header or footer
        String[] irrelevantTerms = new String[]{
                "thank you", "visit again", "gst", "invoice", "bill number", "amount", "date", "counter order",
                "food total", "prices incl.", "tax", "total rs", "amount tendered", "emp. no.", "e.&o.e.",
                "gst no", "cgst", "sgst", "subtotal", "discount", "delivery", "payment", "particulars",
                "thank", "--", "tax invoice", "bill no", "(08:36 AM)", "Laxmi Caterers", "Bhakti Vedang", "T.No."
        };

        // Skip lines that contain any of the irrelevant terms
        for (String term : irrelevantTerms) {
            if (line.toLowerCase().contains(term)) {
                return true;  // Skip lines containing irrelevant terms
            }
        }

        return false;
    }

    private void recalculateGrandTotal() {
        double total = 0;
        for (EditableItem item : editableItemList) {
            total += item.getQuantity() * item.getPrice();
        }
        grandTotalText.setText(String.format("Grand Total: ₹%.2f", total));
    }
}
