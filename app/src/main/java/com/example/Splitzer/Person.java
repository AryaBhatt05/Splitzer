package com.example.Splitzer;

import java.util.ArrayList;
import java.util.List;

public class Person {
    private String name;
    private List<Item> itemsConsumed = new ArrayList<>();
    private double totalAmount = 0.0;

    public Person(String name) {
        this.name = name;
    }

    public String getName() { return name; }
    public List<Item> getItemsConsumed() { return itemsConsumed; }
    public double getTotalAmount() { return totalAmount; }

    public void addItem(Item item) {
        itemsConsumed.add(item);
        totalAmount += item.getSplitAmount();
    }
}