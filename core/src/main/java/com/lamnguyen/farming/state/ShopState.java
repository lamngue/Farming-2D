package com.lamnguyen.farming.state;

import com.badlogic.gdx.utils.Array;
import com.lamnguyen.farming.entities.ItemType;



public class ShopState {

    public enum ShopTab { BUY, SELL }
    public ShopTab activeTab = ShopTab.BUY;

    public int selectedIndex = 0;   // index in visible item list
    public int selectedAmount = 1;

    public Array<ItemType> visibleItems = new Array<>();

    public void reset() {
        selectedIndex = 0;
        selectedAmount = 1;
        visibleItems.clear();
    }
}
