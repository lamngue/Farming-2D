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
    private static final int TILE_SIZE = 32;

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
    }

    public void update(float delta) {
        if (isFullyGrown()) return;

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
        batch.draw(
            type.stages[growthStage],
            WorldGrid.renderOffsetX + tileX * WorldGrid.TILE_RENDER_SIZE,
            WorldGrid.renderOffsetY + tileY * WorldGrid.TILE_RENDER_SIZE,
            WorldGrid.TILE_RENDER_SIZE,
            WorldGrid.TILE_RENDER_SIZE
        );
    }

    public ItemType getHarvestItem() {
        switch (type) {
            case WHEAT: return ItemType.WHEAT_CROP;
            case CORN: return ItemType.CORN_CROP;
        }
        return null;
    }


    public float getGrowTimer() {
        return growTimer;
    }
}
