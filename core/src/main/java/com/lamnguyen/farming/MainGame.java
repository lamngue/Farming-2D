package com.lamnguyen.farming;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.lamnguyen.farming.entities.Player;
import com.lamnguyen.farming.systems.InputSystem;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class MainGame extends ApplicationAdapter {
    SpriteBatch batch;
    ShapeRenderer shape;

    Player player = new Player();
    InputSystem input = new InputSystem();

    @Override
    public void create() {
        shape = new ShapeRenderer();   //
    }

    @Override
    public void render() {
        input.update(player);

        // Keep player within screen boundaries
        player.clampPosition();

        ScreenUtils.clear(0, 0.6f, 0, 1); // green background

        shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.rect(player.x * 32, player.y * 32, 32, 32);
        shape.end();
    }


}
