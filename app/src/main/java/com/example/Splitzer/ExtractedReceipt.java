package com.example.Splitzer;

public class ExtractedReceipt {
    public String text;
    public double total;
    public long timestamp;

    public ExtractedReceipt() {}

    public ExtractedReceipt(String text, double total, long timestamp) {
        this.text = text;
        this.total = total;
        this.timestamp = timestamp;
    }
}
