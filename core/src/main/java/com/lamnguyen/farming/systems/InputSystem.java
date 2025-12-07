package com.lamnguyen.farming.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
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
        int tileX = (int)((player.x + Player.WIDTH / 2f) / WorldGrid.TILE_SIZE);
        int tileY = (int)((player.y + Player.HEIGHT / 2f) / WorldGrid.TILE_SIZE);

        tileX = MathUtils.clamp(tileX, 0, world.getWidth() - 1);
        tileY = MathUtils.clamp(tileY, 0, world.getHeight() - 1);

        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) world.plantCrop(tileX, tileY);
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) world.water(tileX, tileY);
        if (Gdx.input.isKeyJustPressed(Input.Keys.H)) world.harvest(tileX, tileY);
    }
}

