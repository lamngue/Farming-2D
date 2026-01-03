package com.lamnguyen.farming.entities;

import com.badlogic.gdx.graphics.Texture;

public enum CropType {

    WHEAT("crops/wheat/wheat_stage_", 4, 5f, 15f,
        ItemType.WHEAT_SEED,
        ItemType.WHEAT_CROP),
    TOMATO("crops/tomato/tomato_stage_",4, 10f, 30f,
        ItemType.TOMATO_SEED,
        ItemType.TOMATO_CROP),

    POTATO("crops/potato/potato_stage_",4, 15f, 50f,
         ItemType.POTATO_SEED,
         ItemType.POTATO_CROP),

    PUMPKIN("crops/pumpkin/pumpkin_stage_",4, 20f, 50f,
           ItemType.PUMPKIN_SEED,
           ItemType.PUMPKIN_CROP);

    public final Texture[] stages;
    public final int maxGrowthStage;
    public final float growthTime;
    public final float wiltTime;
    public final String basePath;
    public final ItemType seedItem;
    public final ItemType harvestItem;
    public Texture wiltedTexture;
    CropType(String basePath, int stagesCount, float growthTime, float wiltTime,
             ItemType seedItem,
             ItemType harvestItem) {

        this.basePath = basePath;
        this.maxGrowthStage = stagesCount - 1;
        this.seedItem = seedItem;
        this.harvestItem = harvestItem;
        this.growthTime = growthTime;
        this.wiltTime = wiltTime;
        this.stages = new Texture[stagesCount];
    }
        public void loadTextures() {
            for (int i = 0; i <= maxGrowthStage; i++) {
                stages[i] = new Texture(basePath + i + ".png");
            }
            wiltedTexture =  new Texture(basePath + "wilt.png");
        }
    }


