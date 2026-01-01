package com.lamnguyen.farming.entities;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.lamnguyen.farming.world.WorldGrid;


public class Player {

    public enum ActionState {
        NONE,
        WATERING,
        HOEING
    }

    public ActionState actionState = ActionState.NONE;


    public float x, y; // now in pixels
    public static final float SPEED = 120f; // pixels per second

    public enum Direction { UP, DOWN, LEFT, RIGHT }
    public Direction direction = Direction.DOWN;

    public boolean isMoving = false;
    public int money = 0;
    private Animation<TextureRegion> walkUp;
    private Animation<TextureRegion> walkDown;
    private Animation<TextureRegion> walkLeft;
    private Animation<TextureRegion> walkRight;

    private Animation<TextureRegion> waterUp;
    private Animation<TextureRegion> waterDown;
    private Animation<TextureRegion> waterLeft;
    private Animation<TextureRegion> waterRight;

    private Animation<TextureRegion> hoeUp;
    private Animation<TextureRegion> hoeDown;
    private Animation<TextureRegion> hoeLeft;
    private Animation<TextureRegion> hoeRight;

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
        inventory.add(ItemType.TOMATO_SEED, 5); // starting seeds
        inventory.add(ItemType.TOMATO_CROP, 0);
        inventory.add(ItemType.POTATO_SEED, 5); // starting seeds
        inventory.add(ItemType.POTATO_CROP, 0);
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

    public void addMoney(int amount) {
        money += amount;
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

        hoeUp = createAnim(atlas, "hoeing/hoeing_up", 0.12f, null);
        hoeDown = createAnim(atlas, "hoeing/hoeing_down", 0.12f, null);
        hoeLeft = createAnim(atlas, "hoeing/hoeing_left", 0.12f, null);
        hoeRight = createAnim(atlas, "hoeing/hoeing_right", 0.12f, null);

        if (walkDown != null) currentFrame = walkDown.getKeyFrame(0);
    }



    public void startWatering() {
        if (actionState != ActionState.WATERING) {
            actionState = ActionState.WATERING;
            animTimer = 0;
        }
    }

    public void startHoeing() {
        if (actionState != ActionState.HOEING) {
            actionState = ActionState.HOEING;
            animTimer = 0;
        }
    }

    public void updateAnimation(float dt) {

        if (actionState == ActionState.WATERING || actionState == ActionState.HOEING) {

            animTimer += dt;

            Animation<TextureRegion> anim = null;

            if (actionState == ActionState.WATERING) {
                switch (direction) {
                    case UP:    anim = waterUp; break;
                    case DOWN:  anim = waterDown; break;
                    case LEFT:  anim = waterLeft; break;
                    case RIGHT: anim = waterRight; break;
                }
            } else { // HOEING
                switch (direction) {
                    case UP:    anim = hoeUp; break;
                    case DOWN:  anim = hoeDown; break;
                    case LEFT:  anim = hoeLeft; break;
                    case RIGHT: anim = hoeRight; break;
                }
            }

            if (anim != null) {
                currentFrame = anim.getKeyFrame(animTimer);
            }

            // End of action → return to idle
            if (anim != null && anim.isAnimationFinished(animTimer)) {
                actionState = ActionState.NONE;
                animTimer = 0;
            }

            return; // skip walking
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


    public void clampPosition(int worldWidthTiles, int worldHeightTiles) {

        float worldWidth  = worldWidthTiles  * WorldGrid.TILE_SIZE;
        float worldHeight = worldHeightTiles * WorldGrid.TILE_SIZE;

        float maxX = worldWidth  - getWidth();
        float maxY = worldHeight - getHeight();

        x = MathUtils.clamp(x, 0, maxX);
        y = MathUtils.clamp(y, 0, maxY);
    }



    public int getHeight() {
        return getSprite().getRegionHeight();
    }

    public int getWidth() {
        return getSprite().getRegionWidth();
    }

}
