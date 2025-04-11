package com.example.Splitzer;

public class BillDetails {
    private String rawText;
    private long timestamp;

    // Constructor for BillDetails without the totalAmount field
    public BillDetails(String rawText, long timestamp) {
        this.rawText = rawText;
        this.timestamp = timestamp;
    }

    // Getters and setters for rawText and timestamp
    public String getRawText() {
        return rawText;
    }

    public void setRawText(String rawText) {
        this.rawText = rawText;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
