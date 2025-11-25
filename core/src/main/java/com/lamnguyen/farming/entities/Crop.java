package com.lamnguyen.farming.entities;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;


public class Crop {

    // Tile position
    public int tileX;
    public int tileY;

    // Crop state
    private float growthTimer = 0f;
    private int stage = 0; // 0 = seed, 1 = sprout, 2 = grown, 3 = ready

    private static final float TIME_TO_GROW = 3f; // seconds per stage
    private static final int MAX_STAGE = 3;

    private static final int TILE_SIZE = 32;

    public Crop(int tileX, int tileY) {
        this.tileX = tileX;
        this.tileY = tileY;
    }

    // Called in render/update loop
    public void update(float delta) {
        if (stage < MAX_STAGE) {
            growthTimer += delta;

            if (growthTimer >= TIME_TO_GROW) {
                stage++;
                growthTimer = 0f;
            }
        }
    }

    public boolean isFullyGrown() {
        return stage == MAX_STAGE;
    }

    // Later you can connect this to harvesting system
    public void harvest() {
        if (isFullyGrown()) {
            stage = 0;
        }
    }

    public void render(ShapeRenderer shape) {
        float x = tileX * TILE_SIZE;
        float y = tileY * TILE_SIZE;

        switch (stage) {
            case 0: // Seed
                shape.setColor(0.1f, 0.1f, 0.1f, 1);
                shape.circle(x + 16, y + 16, 4);
                break;

            case 1: // Small sprout
                shape.setColor(0f, 0.8f, 0f, 1);
                shape.rect(x + 14, y + 8, 4, 16);
                break;

            case 2: // Growing plant
                shape.setColor(0f, 0.6f, 0f, 1);
                shape.rect(x + 10, y + 6, 12, 20);
                break;

            case 3: // Fully grown
                shape.setColor(0f, 0.5f, 0f, 1);
                shape.rect(x + 6, y + 6, 20, 20);
                break;
        }
    }
}
