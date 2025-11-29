package com.lamnguyen.farming.world;

import com.badlogic.gdx.graphics.Color;

public enum TileType {

    GRASS(new Color(0f, 0.6f, 0f, 1f)),
    DIRT(new Color(0.55f, 0.35f, 0.16f, 1f)),
    WATER(new Color(0.2f, 0.4f, 1f, 1f));

    public final Color color;

    TileType(Color color) {
        this.color = color;
    }
}
