package com.lamnguyen.farming.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.lamnguyen.farming.world.WorldGrid;

public class Player {

    public float x, y; // now in pixels
    public static final float SPEED = 100f; // pixels per second

    public enum Direction { UP, DOWN, LEFT, RIGHT }
    public Direction direction = Direction.DOWN;
    public static final int WIDTH = 32;
    public static final int HEIGHT = 32;
    public boolean isMoving = false;

    private Animation<TextureRegion> walkUp;
    private Animation<TextureRegion> walkDown;
    private Animation<TextureRegion> walkLeft;
    private Animation<TextureRegion> walkRight;

    private float animTimer = 0;
    private TextureRegion currentFrame;
    int startTileX = 5;
    int startTileY = 5;
    public Player() {
        this.x = startTileX * WorldGrid.TILE_SIZE;
        this.y = startTileY * WorldGrid.TILE_SIZE;
        System.out.println("Player initialized at pixel: " + x + "," + y);

    }

    public void loadTextures(TextureAtlas atlas) {
        walkUp = new Animation<>(0.15f, atlas.findRegions("walk_up"), Animation.PlayMode.LOOP);
        walkDown = new Animation<>(0.15f, atlas.findRegions("walk_down"), Animation.PlayMode.LOOP);
        walkLeft = new Animation<>(0.15f, atlas.findRegions("walk_left"), Animation.PlayMode.LOOP);
        walkRight = new Animation<>(0.15f, atlas.findRegions("walk_right"), Animation.PlayMode.LOOP);
        currentFrame = walkDown.getKeyFrame(0);
    }

    public void updateAnimation(float dt) {
        if (isMoving) animTimer += dt;
        else animTimer = 0;

        switch (direction) {
            case UP:    currentFrame = walkUp.getKeyFrame(animTimer); break;
            case DOWN:  currentFrame = walkDown.getKeyFrame(animTimer); break;
            case LEFT:  currentFrame = walkLeft.getKeyFrame(animTimer); break;
            case RIGHT: currentFrame = walkRight.getKeyFrame(animTimer); break;
        }
    }

    public TextureRegion getSprite() {
        return currentFrame;
    }

    public int getTileX() {
        return Math.round(x / WorldGrid.TILE_SIZE);
    }

    public int getTileY() {
        return Math.round(y / WorldGrid.TILE_SIZE);
    }

    public void clampPosition(int worldWidthInTiles, int worldHeightInTiles) {
        x = MathUtils.clamp(x, 0, worldWidthInTiles * WorldGrid.TILE_SIZE - 1);
        y = MathUtils.clamp(y, 0, worldHeightInTiles * WorldGrid.TILE_SIZE - 1);
    }


}

