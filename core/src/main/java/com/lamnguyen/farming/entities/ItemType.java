package com.lamnguyen.farming.entities;

import com.badlogic.gdx.graphics.Texture;

public enum ItemType {

    WHEAT_SEED("items/wheat_seed.png", CropType.WHEAT),
    WHEAT_CROP("items/wheat_crop.png", null);

    public final String iconPath;
    public final CropType cropType; // Null for non-seed items

    public Texture icon; // load later

    ItemType(String iconPath, CropType cropType) {
        this.iconPath = iconPath;
        this.cropType = cropType;
    }

    public void loadTexture() {
        icon = new Texture(iconPath);
    }

    public boolean isSeed() {
        return cropType != null;
    }
}
