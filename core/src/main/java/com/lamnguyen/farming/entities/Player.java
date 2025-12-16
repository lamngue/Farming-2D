package com.lamnguyen.farming.entities;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.lamnguyen.farming.world.WorldGrid;


public class Player {

    public enum ActionState {
        NONE,
        WATERING
    }

    public ActionState actionState = ActionState.NONE;


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

    private Animation<TextureRegion> waterUp;
    private Animation<TextureRegion> waterDown;
    private Animation<TextureRegion> waterLeft;
    private Animation<TextureRegion> waterRight;

    public Inventory inventory;
    public ItemType selectedSeed = ItemType.WHEAT_SEED;
    public float animTimer = 0;
    private TextureRegion currentFrame;
    int startTileX = 5;
    int startTileY = 5;
    public Player() {
        this.inventory = new Inventory();

        this.x = startTileX * WorldGrid.TILE_SIZE;
        this.y = startTileY * WorldGrid.TILE_SIZE;
        inventory.add(ItemType.WHEAT_SEED, 5); // starting seeds
        inventory.add(ItemType.WHEAT_CROP, 0);
        inventory.add(ItemType.CORN_SEED, 5); // starting seeds
        inventory.add(ItemType.CORN_CROP, 0);
    }

    private Animation<TextureRegion> createAnim(TextureAtlas atlas, String name, float frameDuration, Animation.PlayMode mode) {
        Array<TextureAtlas.AtlasRegion> regions = atlas.findRegions(name);
        if (regions.size == 0) {
            System.out.println("Warning: No frames found for: " + name);
            return null;
        }
        Animation<TextureRegion> anim = new Animation<>(frameDuration, regions);
        if (mode != null) anim.setPlayMode(mode);
        return anim;
    }

    public void loadTextures(TextureAtlas atlas) {
        walkUp = createAnim(atlas, "walking/walk_up", 0.15f, Animation.PlayMode.LOOP);
        walkDown = createAnim(atlas, "walking/walk_down", 0.15f, Animation.PlayMode.LOOP);
        walkLeft = createAnim(atlas, "walking/walk_left", 0.15f, Animation.PlayMode.LOOP);
        walkRight = createAnim(atlas, "walking/walk_right", 0.15f, Animation.PlayMode.LOOP);

        waterUp = createAnim(atlas, "watering/watering_up", 0.12f, null);
        waterDown = createAnim(atlas, "watering/watering_down", 0.12f, null);
        waterLeft = createAnim(atlas, "watering/watering_left", 0.12f, null);
        waterRight = createAnim(atlas, "watering/watering_right", 0.12f, null);

        if (walkDown != null) currentFrame = walkDown.getKeyFrame(0);
    }



    public void startWatering() {
        if (actionState != ActionState.WATERING) {
            actionState = ActionState.WATERING;
            animTimer = 0;
        }
    }


    public void updateAnimation(float dt) {

        // WATERING animation has priority
        if (actionState == ActionState.WATERING) {

            animTimer += dt;

            Animation<TextureRegion> anim = null;
            switch (direction) {
                case UP:    anim = waterUp; break;
                case DOWN:  anim = waterDown; break;
                case LEFT:  anim = waterLeft; break;
                case RIGHT: anim = waterRight; break;
            }

            currentFrame = anim.getKeyFrame(animTimer);

            if (anim.isAnimationFinished(animTimer)) {
                actionState = ActionState.NONE;
                animTimer = 0;
            }
            return;
        }

        // WALKING
        if (isMoving) {
            animTimer += dt;
            switch (direction) {
                case UP:    currentFrame = walkUp.getKeyFrame(animTimer, true); break;
                case DOWN:  currentFrame = walkDown.getKeyFrame(animTimer, true); break;
                case LEFT:  currentFrame = walkLeft.getKeyFrame(animTimer, true); break;
                case RIGHT: currentFrame = walkRight.getKeyFrame(animTimer, true); break;
            }
        } else {
            animTimer = 0;
        }
    }

    public TextureRegion getSprite() {
        return currentFrame;
    }


    public void clampPosition(int worldWidthInTiles, int worldHeightInTiles) {
        x = MathUtils.clamp(x, 0, worldWidthInTiles * WorldGrid.TILE_SIZE - 1);
        y = MathUtils.clamp(y, 0, worldHeightInTiles * WorldGrid.TILE_SIZE - 1);
    }


}
