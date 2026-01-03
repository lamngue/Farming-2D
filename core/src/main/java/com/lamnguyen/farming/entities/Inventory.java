package com.lamnguyen.farming.entities;


import java.util.HashMap;
import java.util.Map;

public class Inventory {

    private final Map<ItemType, Integer> items = new HashMap<>();

    public void add(ItemType item, int amount) {
        if (item == null) return; // skip null
        items.put(item, items.getOrDefault(item, 0) + amount);
    }

    public void set(ItemType item, int amount) {
        if (item == null) return; // skip null
        items.put(item, amount);
    }

    public void remove(ItemType item, int amount) {
        int current = items.getOrDefault(item, 0);
        if (current < amount) return;

        items.put(item, current - amount);
    }

    public int get(ItemType item) {
        return items.getOrDefault(item, 0);
    }

    public Map<ItemType, Integer> getAll() {
        return items;
    }

    public void clear() {

    }
}
