package model.shop;

import java.util.ArrayList;
import java.util.List;

public class Shop {
    private final List<Item> items;

    public Shop() {
        this.items = new ArrayList<>();
    }

    public void addItem(Item item) {
        items.add(item);
    }

    public List<Item> getItems() {
        return items;
    }
}

