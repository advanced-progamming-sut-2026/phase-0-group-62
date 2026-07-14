package model.shop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Shop {
    private final List<Item> items;
    private final List<Item> permanentItems;
    private final Map<String, DailyOffer> dailyOffers;
    private long lastRefreshTime;

    public static class DailyOffer {
        private String id;
        private String plantType;
        private int price;
        private int discountPercentage;
        private int quantity;
        private boolean purchased;

        public DailyOffer(String id, String plantType, int price, int discountPercentage, int quantity) {
            this.id = id;
            this.plantType = plantType;
            this.price = price;
            this.discountPercentage = discountPercentage;
            this.quantity = quantity;
            this.purchased = false;
        }

        public String getId() { return id; }
        public String getPlantType() { return plantType; }
        public int getPrice() { return price; }
        public int getDiscountPercentage() { return discountPercentage; }
        public int getQuantity() { return quantity; }
        public boolean isPurchased() { return purchased; }
        public void setPurchased(boolean purchased) { this.purchased = purchased; }
        public int getDiscountedPrice() { 
            return (int) (price * (1 - discountPercentage / 100.0)); 
        }
    }

    public Shop() {
        this.items = new ArrayList<>();
        this.permanentItems = new ArrayList<>();
        this.dailyOffers = new HashMap<>();
        this.lastRefreshTime = System.currentTimeMillis();
        initializeShop();
    }

    private void initializeShop() {
        // Permanent items
        permanentItems.add(new Item("Pot", 2000));
        permanentItems.add(new Item("Plant Food", 3, "diamond"));
        permanentItems.add(new Item("Random Seed Pack", 1000));
        permanentItems.add(new Item("Choice Seed Pack", 5, "diamond"));
        permanentItems.add(new Item("Currency Exchange", 5, "diamond", 500, "coin"));
        
        // Add to items list
        items.addAll(permanentItems);
        
        // Initialize daily offers
        refreshDailyOffers();
    }

    public void refreshDailyOffers() {
        dailyOffers.clear();
        // Generate random daily offer
        String[] plants = {"peashooter", "sunflower", "wallnut", "snowpea", "repeater"};
        String plant = plants[(int) (Math.random() * plants.length)];
        dailyOffers.put(plant, new DailyOffer(
            "daily_" + System.currentTimeMillis(),
            plant,
            2000,
            20,
            10
        ));
        lastRefreshTime = System.currentTimeMillis();
    }

    public void addItem(Item item) {
        items.add(item);
    }

    public List<Item> getItems() {
        return items;
    }

    public Item getItemById(String id) {
        for (Item item : items) {
            if (item.getId().equals(id)) {
                return item;
            }
        }
        return null;
    }

    public DailyOffer getDailyOffer(String plantType) {
        return dailyOffers.get(plantType);
    }

    public Map<String, DailyOffer> getDailyOffers() {
        return new HashMap<>(dailyOffers);
    }

    public boolean purchaseItem(String itemId, int quantity) {
        Item item = getItemById(itemId);
        if (item == null) return false;
        // Purchase logic handled by controller
        return true;
    }

    public boolean purchaseDailyOffer(String plantType) {
        DailyOffer offer = dailyOffers.get(plantType);
        if (offer == null || offer.isPurchased()) return false;
        offer.setPurchased(true);
        return true;
    }

    public List<Item> getPermanentItems() {
        return new ArrayList<>(permanentItems);
    }

    public boolean needsRefresh() {
        // Refresh daily offers every 24 hours
        return System.currentTimeMillis() - lastRefreshTime > 24 * 60 * 60 * 1000;
    }
}
