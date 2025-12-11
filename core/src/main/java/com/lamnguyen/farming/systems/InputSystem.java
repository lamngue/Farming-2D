package com.lamnguyen.farming.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import com.lamnguyen.farming.entities.Crop;
import com.lamnguyen.farming.entities.ItemType;
import com.lamnguyen.farming.entities.CropType;
import com.lamnguyen.farming.entities.Player;
import com.lamnguyen.farming.world.WorldGrid;

public class InputSystem {

    public void updatePlayer(Player player, float dt) {
        player.isMoving = false;

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            player.y += Player.SPEED * dt;
            player.direction = Player.Direction.UP;
            player.isMoving = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            player.y -= Player.SPEED * dt;
            player.direction = Player.Direction.DOWN;
            player.isMoving = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            player.x -= Player.SPEED * dt;
            player.direction = Player.Direction.LEFT;
            player.isMoving = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            player.x += Player.SPEED * dt;
            player.direction = Player.Direction.RIGHT;
            player.isMoving = true;
        }
    }

    public void updateWorld(Player player, WorldGrid world) {
        int w = player.getSprite().getRegionWidth();
        int h = player.getSprite().getRegionHeight();

        float centerX = player.x + w / 2f;
        float centerY = player.y + h / 2f;

        int tileX = (int) (centerX / WorldGrid.TILE_SIZE);
        int tileY = (int) (centerY / WorldGrid.TILE_SIZE);
        tileX = MathUtils.clamp(tileX, 0, world.getWidth() - 1);
        tileY = MathUtils.clamp(tileY, 0, world.getHeight() - 1);

        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            ItemType seed = player.selectedSeed;

            if (!seed.isSeed()) return;

            if (player.inventory.get(seed) > 0) {
                CropType crop = seed.cropType;

                // Plant crop and check success
                boolean planted = world.plantCrop(tileX, tileY, crop);
                if (planted) {
                    player.inventory.remove(seed, 1);
                }
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) world.water(tileX, tileY);
        if (Gdx.input.isKeyJustPressed(Input.Keys.H)) {
            Crop c = world.getCrop(tileX, tileY);
            if (c != null && c.isFullyGrown()) {
                ItemType harvest = c.getHarvestItem();
                if (harvest != null) {
                    player.inventory.add(harvest, 1);
                    world.harvest(tileX, tileY);
                }
            }

        }
    }

    public void updateSeedSelection(Player player) {

        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            player.selectedSeed = ItemType.WHEAT_SEED;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            player.selectedSeed = ItemType.CORN_SEED;
        }

    }

}

