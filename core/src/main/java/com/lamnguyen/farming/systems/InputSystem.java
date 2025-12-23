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
        if (player.actionState == Player.ActionState.WATERING) {
            player.isMoving = false;
            return;
        }
        if (player.actionState != Player.ActionState.NONE)
            return;
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

        // Convert to tile coordinates accounting for render offset
        int tileX = (int) ((centerX - WorldGrid.renderOffsetX) / WorldGrid.TILE_RENDER_SIZE);
        int tileY = (int) ((centerY - WorldGrid.renderOffsetY) / WorldGrid.TILE_RENDER_SIZE);
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
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            player.startWatering();
            world.water(tileX, tileY);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            player.startHoeing();

            Crop c = world.getCrop(tileX, tileY);
            if (c != null && c.isWilted) {
                world.removeCrop(tileX, tileY);
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.H)) {
            Crop c = world.getCrop(tileX, tileY);
            if (c != null && c.canHarvest()) {
                ItemType harvest = c.getHarvestItem();
                if (harvest != null) {
                    player.inventory.add(harvest, 1);
                    player.addMoney(c.type.sellPrice);

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
            player.selectedSeed = ItemType.TOMATO_SEED;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
            player.selectedSeed = ItemType.POTATO_SEED;
        }
    }

}

