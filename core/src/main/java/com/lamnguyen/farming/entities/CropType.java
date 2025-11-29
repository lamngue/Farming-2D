package com.lamnguyen.farming.entities;

import com.badlogic.gdx.graphics.Texture;

public enum CropType {

    WHEAT("wheat_stage_", 4, 10);

    public final Texture[] stages;
    public final int maxGrowthStage;
    public final int sellPrice;

    CropType(String basePath, int stagesCount, int sellPrice) {
        this.maxGrowthStage = stagesCount - 1;
        this.sellPrice = sellPrice;
        this.stages = new Texture[stagesCount];

        // Load textures: wheat_stage_0.png, wheat_stage_1.png…
        for (int i = 0; i < stagesCount; i++) {
            stages[i] = new Texture(basePath + i + ".png");
        }
    }
}


