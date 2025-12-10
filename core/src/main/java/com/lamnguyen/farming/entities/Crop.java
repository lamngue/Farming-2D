package com.lamnguyen.farming.entities;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Crop {

    public CropType type;

    // Tile position
    public int tileX;
    public int tileY;

    // Status
    private boolean isWatered;
    private int fertilizerLevel;
    private int growthStage;
    private float growTimer;
    private static final int TILE_SIZE = 32;

    // Settings
    private static final int MAX_FERTILIZER = 3;
    private static final float GROW_TIME = 2f; // seconds per stage

    public Crop(CropType type, int tileX, int tileY) {
        this.type = type;
        this.tileX = tileX;
        this.tileY = tileY;
        this.isWatered = false;
        this.fertilizerLevel = 0;
        this.growthStage = 0;
        this.growTimer = 0;
    }

    // Called in render() or update()
    public void update(float delta) {

        if (!isFullyGrown() && isWatered) {
            float speedBonus = 1 + (fertilizerLevel * 0.15f);
            growTimer += delta * speedBonus;
            if (growTimer >= GROW_TIME) {
                growTimer = 0;
                growthStage++;
                isWatered = false;

                if (fertilizerLevel > 0 && Math.random() > 0.7)
                    fertilizerLevel--;   // sometimes used up
            }
        }
    }

    // --------------------------
    // Actions
    // --------------------------

    public void water() {
        isWatered = true;
    }

    public void fertilize() {
        if (fertilizerLevel < MAX_FERTILIZER) {
            fertilizerLevel++;
        }
    }

    public boolean isFullyGrown() {
        return growthStage >= type.maxGrowthStage;
    }

    public int harvest() {
        if (isFullyGrown()) {
            return type.sellPrice + (fertilizerLevel * 5);
        }
        return 0;
    }

    // --------------------------
    // Getters
    // --------------------------

    public int getGrowthStage() {
        return growthStage;
    }

    public boolean getIsWatered() {
        return isWatered;
    }

    public int getFertilizerLevel() {
        return fertilizerLevel;
    }

    public int getSellPrice() {
        return type.sellPrice;
    }


    public void render(SpriteBatch batch) {
        Texture img = type.stages[growthStage];

        float x = tileX * TILE_SIZE;
        float y = tileY * TILE_SIZE;

        batch.draw(img, x, y, TILE_SIZE, TILE_SIZE);
    }

    public ItemType getHarvestItem() {
        switch (type) {
            case WHEAT: return ItemType.WHEAT_CROP;
            // add more crops later
        }
        return null;
    }
}
