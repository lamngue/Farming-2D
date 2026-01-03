package com.lamnguyen.farming.entities;

import com.badlogic.gdx.graphics.Texture;

public enum ItemType {

    WHEAT_SEED("items/wheat_seed.png", CropType.WHEAT, 5, null),
    WHEAT_CROP("items/wheat_crop.png", null, null, 10),
    TOMATO_SEED("items/tomato_seed.png", CropType.TOMATO, 7, null),
    TOMATO_CROP("items/tomato_crop.png", null, null, 15),
    POTATO_SEED("items/potato_seed.png", CropType.POTATO, 10, null),
    POTATO_CROP("items/potato_crop.png", null, null, 20),
    PUMPKIN_SEED("items/pumpkin_seed.png", CropType.PUMPKIN, 12, null),
    PUMPKIN_CROP("items/pumpkin_crop.png", null, null, 25);


    public final String iconPath;
    public final CropType cropType;
    public final Integer buyPrice;
    public final Integer sellPrice;
    public Texture icon; // load later

    ItemType(String iconPath, CropType cropType, Integer buyPrice, Integer sellPrice) {
        this.iconPath = iconPath;
        this.cropType = cropType;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
    }

    public void loadTexture() {
        icon = new Texture(iconPath);
    }

    public boolean isSeed() {
        return cropType != null;
    }
}
