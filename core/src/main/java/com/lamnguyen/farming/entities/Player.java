package com.lamnguyen.farming.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;

public class Player {
    public int x = 10;
    public int y = 10;

    // Call this after updating player position
    public void clampPosition() {
        int maxX = (Gdx.graphics.getWidth() / 32) - 1;
        int maxY = (Gdx.graphics.getHeight() / 32) - 1;

        x = MathUtils.clamp(x, 0, maxX);
        y = MathUtils.clamp(y, 0, maxY);
    }
}
