package com.lamnguyen.farming.entities;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.lamnguyen.farming.world.WorldGrid;

public class Crop {

    public CropType type;

    // Tile position
    public int tileX;
    public int tileY;

    // Status
    public boolean isWatered;
    public int fertilizerLevel;
    public int growthStage;

    public float growTimer;
    private final float growthTime;
    public final float wiltTime;

    private float timeFullyGrown = 0f;
    private static final float WILT_TIME = 10f; // seconds before wilting
    public boolean isWilted = false;

    // Settings
    private static final int MAX_FERTILIZER = 3;

    public Crop(CropType type, int tileX, int tileY) {
        this.type = type;
        this.tileX = tileX;
        this.tileY = tileY;
        this.isWatered = false;
        this.fertilizerLevel = 0;
        this.growthStage = 0;
        this.growTimer = 0;
        this.growthTime = type.growthTime;
        this.wiltTime = type.wiltTime;
    }

    public void update(float delta) {
        if (isFullyGrown()) {
            if (!isWilted) {
                timeFullyGrown += delta;

                if (timeFullyGrown >= this.wiltTime) {
                    isWilted = true;
                }
            }
            return; // stop growing once fully grown
        }

        float speedBonus = 0.5f;

        // Watered crops grow faster
        if (isWatered) {
            speedBonus *= 2f;  // e.g., double speed if watered
        }

        speedBonus += fertilizerLevel * 0.15f;

        // Advance growth timer
        growTimer += delta * speedBonus;

        if (growTimer >= growthTime) {
            growTimer -= growthTime; // keep remainder
            growthStage++;

            // Optional: reduce fertilizer occasionally
            if (fertilizerLevel > 0 && Math.random() > 0.7) {
                fertilizerLevel--;
            }

            // Reset watered flag only when moving to the next stage
            isWatered = false;
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

    public boolean canHarvest() {
        return isFullyGrown() && !isWilted;
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

    public Texture getTexture() {
        if (isWilted) {
            return type.wiltedTexture;
        }
        return type.stages[growthStage];
    }


    public void render(SpriteBatch batch) {
        batch.draw(
            getTexture(),
            tileX * WorldGrid.TILE_SIZE,
            tileY * WorldGrid.TILE_SIZE,
            WorldGrid.TILE_SIZE,
            WorldGrid.TILE_SIZE
        );
    }


    public ItemType getHarvestItem() {
        switch (type) {
            case WHEAT: return ItemType.WHEAT_CROP;
            case POTATO: return ItemType.POTATO_CROP;
            case TOMATO: return ItemType.TOMATO_CROP;
            case PUMPKIN: return ItemType.PUMPKIN_CROP;
        }
        return null;
    }


    public float getGrowTimer() {
        return growTimer;
    }
}
