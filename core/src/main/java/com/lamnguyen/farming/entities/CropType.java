package com.lamnguyen.farming.entities;

import com.badlogic.gdx.graphics.Texture;

public enum CropType {

    WHEAT("crops/wheat_stage_", 4, 10,
        ItemType.WHEAT_SEED,
        ItemType.WHEAT_CROP);
    public final Texture[] stages;
    public final int maxGrowthStage;
    public final int sellPrice;
    public final String basePath;
    public final ItemType seedItem;
    public final ItemType harvestItem;

    CropType(String basePath, int stagesCount, int sellPrice,
             ItemType seedItem,
             ItemType harvestItem) {

        this.basePath = basePath;
        this.maxGrowthStage = stagesCount - 1;
        this.sellPrice = sellPrice;
        this.seedItem = seedItem;
        this.harvestItem = harvestItem;

        this.stages = new Texture[stagesCount];
    }
        public void loadTextures() {
            for (int i = 0; i <= maxGrowthStage; i++) {
                stages[i] = new Texture(basePath + i + ".png");
            }
        }
    }


