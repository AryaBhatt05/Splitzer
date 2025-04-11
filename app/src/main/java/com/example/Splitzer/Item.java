package com.example.Splitzer;

public class Item {
    private String name;
    private double price;
    private boolean shared;
    private int sharedBy;  // Number of people the item is shared with
    private String assignedTo;
    private double splitAmount; // Amount each person will pay
    private int quantity; // Add quantity field

    // Constructor with quantity
    public Item(String name, double price, int quantity) {
        this.name = name;
        this.price = price;
        this.shared = false;
        this.sharedBy = 1;
        this.assignedTo = "";
        this.splitAmount = price * quantity;  // Default to the full price (rate * qty)
        this.quantity = quantity;  // Set quantity
    }

    // Getters and Setters
    public String getName() { return name; }
    public double getPrice() { return price; }
    public boolean isShared() { return shared; }
    public int getSharedBy() { return sharedBy; }
    public String getAssignedTo() { return assignedTo; }
    public int getQuantity() { return quantity; }  // Add getter for quantity

    public void setShared(boolean shared) { this.shared = shared; }
    public void setSharedBy(int sharedBy) { this.sharedBy = sharedBy; }
    public void setAssignedTo(String assignedTo) { this.assignedTo = assignedTo; }
    public void setQuantity(int quantity) { this.quantity = quantity; }  // Add setter for quantity

    // Set the split amount manually if needed
    public void setSplitAmount(double amount) { this.splitAmount = amount; }

    // Calculate the split amount
    public double getSplitAmount() {
        if (shared && sharedBy > 1) {
            // If the item is shared, divide the total price by the number of people
            return (price * quantity) / sharedBy;
        } else {
            // If it's not shared, return the total price
            return price * quantity;
        }
    }
}
