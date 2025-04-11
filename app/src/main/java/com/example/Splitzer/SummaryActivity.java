package com.example.Splitzer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Hashtable;
import java.util.List;

public class SummaryActivity extends AppCompatActivity {

    private List<Item> itemList;
    private List<Person> personList;
    private TextView summaryText;
    private Button downloadPdfButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        summaryText = findViewById(R.id.summaryText);
        downloadPdfButton = findViewById(R.id.downloadPdfButton);

        String itemJson = getIntent().getStringExtra("itemList");
        String personJson = getIntent().getStringExtra("personList");

        itemList = new Gson().fromJson(itemJson, new TypeToken<List<Item>>() {}.getType());
        personList = new Gson().fromJson(personJson, new TypeToken<List<Person>>() {}.getType());

        assignItemsToPeople();
        displaySummary();

        downloadPdfButton.setOnClickListener(v -> generatePDF());
    }

    private void assignItemsToPeople() {
        for (Item item : itemList) {
            if (item.getAssignedTo() == null || item.getAssignedTo().isEmpty()) continue;

            // Calculate total price (rate * quantity)
            double totalPrice = item.getPrice() * item.getQuantity();

            if (item.isShared()) {
                String[] sharedNames = item.getAssignedTo().split(",\\s*");
                int sharedCount = sharedNames.length;

                // Set the correct split amount (total price divided by the number of people sharing)
                item.setSplitAmount(totalPrice / sharedCount);

                // Assign the item to each person
                for (String name : sharedNames) {
                    for (Person p : personList) {
                        if (p.getName().equalsIgnoreCase(name.trim())) {
                            p.addItem(item);
                        }
                    }
                }
            } else {
                item.setSharedBy(1);
                item.setSplitAmount(totalPrice);  // No sharing, so the full amount is assigned to one person
                for (Person p : personList) {
                    if (p.getName().equalsIgnoreCase(item.getAssignedTo().trim())) {
                        p.addItem(item);
                    }
                }
            }
        }
    }

    private void displaySummary() {
        StringBuilder summary = new StringBuilder();
        for (Person person : personList) {
            summary.append("👤 ").append(person.getName()).append("\n");
            for (Item item : person.getItemsConsumed()) {
                summary.append("  • ").append(item.getName())
                        .append(" - ₹").append(String.format("%.2f", item.getSplitAmount()))
                        .append(item.isShared() ? " (shared)\n" : " (individual)\n");
            }
            summary.append("  💰 Total: ₹")
                    .append(String.format("%.2f", person.getTotalAmount()))
                    .append("\n\n");
        }
        summaryText.setText(summary.toString());
    }

    // Generate Fake QR Code with the message to send money to the logged-in user
    public Bitmap generateFakeQRCode(String username) {
        try {
            // Construct the message for the QR code
            String message = "Send money to " + username;

            // QR Code settings
            Hashtable<EncodeHintType, Object> hintMap = new Hashtable<>();
            hintMap.put(EncodeHintType.ERROR_CORRECTION, com.google.zxing.qrcode.decoder.ErrorCorrectionLevel.L);
            hintMap.put(EncodeHintType.MARGIN, 1);  // White margin around the QR code

            // Create the QR code
            BitMatrix bitMatrix = new MultiFormatWriter().encode(message, BarcodeFormat.QR_CODE, 300, 300, hintMap);

            // Convert BitMatrix to Bitmap
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF); // Black for QR code, white for background
                }
            }

            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Uri generatePDF() {
        String text = summaryText.getText().toString();

        // Generate Fake QR Code for the logged-in user's name (e.g., "Send money to John Doe")
        String username = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();  // Get logged-in user's name
        Bitmap qrCodeBitmap = generateFakeQRCode(username);  // Generate the fake QR code with a message

        PdfDocument document = new PdfDocument();
        Paint paint = new Paint();
        int pageHeight = 1120, pageWidth = 792;
        int y = 50;  // Starting position for the text
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        // Draw the text on the PDF
        for (String line : text.split("\n")) {
            canvas.drawText(line, 40f, y, paint);
            y += paint.descent() - paint.ascent() + 10;
            if (y > pageHeight - 100) {
                document.finishPage(page);
                pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, document.getPages().size() + 1).create();
                page = document.startPage(pageInfo);
                canvas = page.getCanvas();
                y = 50;
            }
        }

        // Embed the QR Code on the PDF (Center it horizontally)
        if (qrCodeBitmap != null) {
            int qrCodeSize = 200;  // Set the size of the QR code in the PDF
            int xPosition = (pageWidth - qrCodeSize) / 2;  // Center the QR code horizontally
            int yPosition = y + 50;  // Position it after the text, ensuring it doesn't overlap
            canvas.drawBitmap(qrCodeBitmap, xPosition, yPosition, paint);  // Position the QR code centered
        }

        document.finishPage(page);

        try {
            // Save the file in app-specific directory (Scoped Storage)
            File dir = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "PDFs");
            if (!dir.exists()) dir.mkdirs();

            File pdfFile = new File(dir, "Bill_Summary_" + System.currentTimeMillis() + ".pdf");

            FileOutputStream fos = new FileOutputStream(pdfFile);
            document.writeTo(fos);
            document.close();

            Toast.makeText(this, "PDF saved!", Toast.LENGTH_SHORT).show();

            // ✅ Auto-open the PDF
            Uri pdfUri = FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".provider",
                    pdfFile);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(pdfUri, "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(intent, "Open PDF"));

            return pdfUri;

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to save/open PDF", Toast.LENGTH_LONG).show();
        }
        return null;
    }
}
