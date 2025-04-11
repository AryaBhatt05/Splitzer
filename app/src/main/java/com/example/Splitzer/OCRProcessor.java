package com.example.Splitzer;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

public class OCRProcessor {
    private static final String TAG = "OCRProcessor";
    private final TextRecognizer recognizer;

    public OCRProcessor(Context context) {
        // ✅ Play Services version requires TextRecognizerOptions (e.g., Latin script)
        recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
    }

    public interface OCRListener {
        void onTextRecognized(String text);
    }

    public void extractTextFromImage(Bitmap bitmap, OCRListener listener) {
        if (bitmap == null) {
            listener.onTextRecognized("No image selected");
            return;
        }

        InputImage image = InputImage.fromBitmap(bitmap, 0);

        recognizer.process(image)
                .addOnSuccessListener(new OnSuccessListener<Text>() {
                    @Override
                    public void onSuccess(Text result) {
                        String extractedText = result.getText();
                        Log.d(TAG, "Extracted Text: " + extractedText);
                        listener.onTextRecognized(extractedText.isEmpty() ? "No text detected" : extractedText);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.e(TAG, "OCR failed: " + e.getMessage());
                        listener.onTextRecognized("OCR failed");
                    }
                });
    }
}
