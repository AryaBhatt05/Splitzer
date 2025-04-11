package com.example.Splitzer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ImageView receiptImage;
    private Bitmap selectedBitmap;
    private OCRProcessor ocrProcessor;
    private RecyclerView recyclerView;
    private List<String> rawTextLines = new ArrayList<>();
    private Button nextButton, saveBillButton;
    private TextView totalText;
    private String rawText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Bill OCR");
        toolbar.setNavigationIcon(R.drawable.ic_logout); // Ensure ic_logout.xml exists
        toolbar.setNavigationOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        });

        // Initialize UI elements
        receiptImage = findViewById(R.id.receiptImage);
        recyclerView = findViewById(R.id.recyclerView);
        nextButton = findViewById(R.id.nextButton);
        saveBillButton = findViewById(R.id.saveBillButton);
//        totalText = findViewById(R.id.totalText);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ocrProcessor = new OCRProcessor(this);

        findViewById(R.id.selectImageButton).setOnClickListener(v -> pickImageFromGallery());
        findViewById(R.id.processButton).setOnClickListener(v -> processImage());

        nextButton.setOnClickListener(v -> {
            if (rawText.isEmpty()) {
                Toast.makeText(this, "Please process the image first", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(MainActivity.this, PeopleInputActivity.class);
                intent.putExtra("rawText", rawText);
                startActivity(intent);
            }
        });

        // Handle Save Bill Button
        saveBillButton.setOnClickListener(v -> saveBillToFirebase());
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    try {
                        InputStream stream = getContentResolver().openInputStream(imageUri);
                        selectedBitmap = BitmapFactory.decodeStream(stream);
                        receiptImage.setImageBitmap(selectedBitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    private void processImage() {
        if (selectedBitmap == null) {
            Toast.makeText(this, "Please select an image first", Toast.LENGTH_SHORT).show();
            return;
        }

        ocrProcessor.extractTextFromImage(selectedBitmap, text -> runOnUiThread(() -> {
            rawText = text;
            rawTextLines = Arrays.asList(text.split("\n"));
            recyclerView.setAdapter(new TextAdapter(rawTextLines));
//            updateTotalFromOCR(rawTextLines);
        }));
    }

//    private void updateTotalFromOCR(List<String> lines) {
//        double total = 0.0;
//
//        // Improved Regex pattern to look for total-related keywords and values after them
//        Pattern totalPattern = Pattern.compile(".*(?:total|amount\\s*tendered|total\\s*rs|amount\\s*rs|grand\\s*total).*\\s*(\\d+(?:\\.\\d{1,2})?)", Pattern.CASE_INSENSITIVE);
//
//        // Check each line for matching the total pattern
//        for (String line : lines) {
//            Matcher matcher = totalPattern.matcher(line);
//            if (matcher.find()) {
//                try {
//                    total = Double.parseDouble(matcher.group(1));  // Get the captured amount
//                    break;
//                } catch (Exception ignored) {
//                }
//            }
//        }
//
//        // Display the extracted total value
//        if (total > 0) {
//            totalText.setText(String.format("Total: ₹%.2f", total));
//        } else {
//            totalText.setText("Total: ₹0");
//        }
//    }

    private void saveBillToFirebase() {
        if (rawText.isEmpty()) {
            Toast.makeText(this, "No OCR data to save!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save bill details to Firebase without the total amount
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("users").child(uid).child("bills");

        String billId = ref.push().getKey();
        BillDetails billDetails = new BillDetails(rawText, System.currentTimeMillis()); // Removed total amount

        if (billId != null) {
            ref.child(billId).setValue(billDetails)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(MainActivity.this, "Bill saved successfully!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(MainActivity.this, "Failed to save bill: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

}
