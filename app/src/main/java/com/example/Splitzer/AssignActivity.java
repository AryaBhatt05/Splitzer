package com.example.Splitzer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class AssignActivity extends AppCompatActivity {

    private List<Item> itemList;
    private List<Person> personList;
    private RecyclerView assignRecyclerView;
    private Button proceedSummaryBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign);

        assignRecyclerView = findViewById(R.id.assignRecyclerView);
        proceedSummaryBtn = findViewById(R.id.proceedSummaryBtn);

        // ✅ Get data from intent
        String itemJson = getIntent().getStringExtra("itemList");
        String personJson = getIntent().getStringExtra("personList");

        itemList = new Gson().fromJson(itemJson, new TypeToken<List<Item>>() {}.getType());
        personList = new Gson().fromJson(personJson, new TypeToken<List<Person>>() {}.getType());

        if (itemList == null || personList == null) {
            Toast.makeText(this, "Missing data", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Log.d("ASSIGN_DEBUG", "Loaded " + itemList.size() + " items and " + personList.size() + " people");

        // ✅ Setup RecyclerView
        assignRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        AssignAdapter adapter = new AssignAdapter(this, itemList, personList);
        assignRecyclerView.setAdapter(adapter);

        proceedSummaryBtn.setOnClickListener(v -> {
            for (Item item : itemList) {
                Log.d("ASSIGN_FINAL", item.getName() + " → " + item.getAssignedTo());
            }

            Intent intent = new Intent(AssignActivity.this, SummaryActivity.class);
            intent.putExtra("itemList", new Gson().toJson(itemList));
            intent.putExtra("personList", new Gson().toJson(personList));
            startActivity(intent);
        });
    }
}
