package com.lamnguyen.farming.state;

import com.badlogic.gdx.math.Rectangle;
import com.lamnguyen.farming.entities.ItemType;

import java.util.HashMap;
import java.util.Map;


public class ShopState {
    public enum ShopTab {
        BUY,
        SELL
    }
    public ShopTab activeTab = ShopTab.BUY;
    public ItemType selectedItem;
    public int selectedAmount = 1;

    // UI hitboxes
    public Rectangle panelBounds = new Rectangle();
    public Rectangle buyButton = new Rectangle();
    public Rectangle sellButton = new Rectangle();
    public Rectangle plusButton = new Rectangle();
    public Rectangle minusButton = new Rectangle();
    public Rectangle buyTab = new Rectangle();
    public Rectangle sellTab = new Rectangle();


    public Map<ItemType, Rectangle> itemButtons = new HashMap<>();

    public void reset() {
        selectedItem = null;
        selectedAmount = 1;
        itemButtons.clear();
    }
}
