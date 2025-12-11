package com.lamnguyen.farming.entities;

import com.badlogic.gdx.graphics.Texture;

public enum CropType {

    WHEAT("crops/wheat_stage_", 4, 2, 10,  5f,
        ItemType.WHEAT_SEED,
        ItemType.WHEAT_CROP),
    CORN("crops/corn_stage_",4, 4, 20, 10f,
        ItemType.CORN_SEED,
        ItemType.CORN_CROP);

    public final Texture[] stages;
    public final int maxGrowthStage;
    public final int buyPrice;
    public final int sellPrice;
    public final float growthTime;
    public final String basePath;
    public final ItemType seedItem;
    public final ItemType harvestItem;

    CropType(String basePath, int stagesCount, int buyPrice, int sellPrice, float growthTime,
             ItemType seedItem,
             ItemType harvestItem) {

        this.basePath = basePath;
        this.maxGrowthStage = stagesCount - 1;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.seedItem = seedItem;
        this.harvestItem = harvestItem;
        this.growthTime = growthTime;
        this.stages = new Texture[stagesCount];
    }
        public void loadTextures() {
            for (int i = 0; i <= maxGrowthStage; i++) {
                stages[i] = new Texture(basePath + i + ".png");
            }
        }
    }


