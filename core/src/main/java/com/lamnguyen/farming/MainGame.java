package com.lamnguyen.farming;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.lamnguyen.farming.entities.Player;
import com.lamnguyen.farming.systems.InputSystem;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class MainGame extends ApplicationAdapter {

    ShapeRenderer shape;

    Player player = new Player();
    InputSystem input = new InputSystem();

    // Tile constants
    private static final int TILE_SIZE = 32;
    private static final int MAP_WIDTH = 25;
    private static final int MAP_HEIGHT = 18;

    // 20 dirt tiles = 5 columns x 4 rows
    private static final int DIRT_WIDTH = 5;
    private static final int DIRT_HEIGHT = 4;

    // Top-left position of dirt patch (centered)
    private final int dirtStartX = (MAP_WIDTH / 2) - (DIRT_WIDTH / 2);
    private final int dirtStartY = (MAP_HEIGHT / 2) - (DIRT_HEIGHT / 2);

    @Override
    public void create() {
        shape = new ShapeRenderer();
    }

    @Override
    public void render() {
        input.update(player);

        player.clampPosition();

        ScreenUtils.clear(0, 0.6f, 0, 1); // green background

        shape.begin(ShapeRenderer.ShapeType.Filled);

    // 🟫 Draw 20 dirt tiles (5x4)
        shape.setColor(0.55f, 0.35f, 0.16f, 1);

        for (int x = 0; x < DIRT_WIDTH; x++) {
            for (int y = 0; y < DIRT_HEIGHT; y++) {
                shape.rect(
                    (dirtStartX + x) * TILE_SIZE,
                    (dirtStartY + y) * TILE_SIZE,
                    TILE_SIZE,
                    TILE_SIZE
                );
            }
        }

        // ⬜ Draw player
        shape.setColor(1, 1, 1, 1);
        shape.rect(player.x * TILE_SIZE, player.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);

        shape.end();

        // 🔲 Draw grid lines for dirt tiles
        shape.begin(ShapeRenderer.ShapeType.Line);
        shape.setColor(0, 0, 0, 1); // Black grid

        for (int x = 0; x < DIRT_WIDTH; x++) {
            for (int y = 0; y < DIRT_HEIGHT; y++) {
                shape.rect(
                    (dirtStartX + x) * TILE_SIZE,
                    (dirtStartY + y) * TILE_SIZE,
                    TILE_SIZE,
                    TILE_SIZE
                );
            }
        }

        shape.end();
    }

    @Override
    public void dispose() {
        shape.dispose();
    }
}

