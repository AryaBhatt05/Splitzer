package com.example.Splitzer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class PeopleInputActivity extends AppCompatActivity {

    private EditText personCountInput;
    private LinearLayout namesContainer;
    private Button addNamesButton, proceedButton;
    private List<EditText> nameInputs = new ArrayList<>();
    private String rawText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people_input);

        rawText = getIntent().getStringExtra("rawText");

        personCountInput = findViewById(R.id.personCountInput);
        namesContainer = findViewById(R.id.namesContainer);
        addNamesButton = findViewById(R.id.addNamesButton);
        proceedButton = findViewById(R.id.proceedButton);

        addNamesButton.setOnClickListener(v -> {
            namesContainer.removeAllViews();
            nameInputs.clear();

            String countStr = personCountInput.getText().toString().trim();
            if (countStr.isEmpty()) {
                Toast.makeText(this, "Enter number of people", Toast.LENGTH_SHORT).show();
                return;
            }

            int count = Integer.parseInt(countStr);
            for (int i = 0; i < count; i++) {
                EditText nameInput = new EditText(this);
                nameInput.setHint("Person " + (i + 1));
                namesContainer.addView(nameInput);
                nameInputs.add(nameInput);
            }
        });

        proceedButton.setOnClickListener(v -> {
            List<Person> personList = new ArrayList<>();
            for (EditText editText : nameInputs) {
                String name = editText.getText().toString().trim();
                if (!name.isEmpty()) {
                    personList.add(new Person(name));
                }
            }

            if (personList.isEmpty()) {
                Toast.makeText(this, "Please enter at least one name", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(PeopleInputActivity.this, ReviewAndSaveActivity.class); // 👈 Step now changed
            intent.putExtra("rawText", rawText);
            intent.putExtra("peopleList", new Gson().toJson(personList));
            startActivity(intent);
        });
    }
}
