package com.lamnguyen.farming;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.lamnguyen.farming.entities.Crop;
import com.lamnguyen.farming.entities.CropType;
import com.lamnguyen.farming.entities.Player;
import com.lamnguyen.farming.systems.InputSystem;
import com.lamnguyen.farming.world.WorldGrid;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class MainGame extends ApplicationAdapter {

    ShapeRenderer shape;

    Player player = new Player();
    InputSystem input = new InputSystem();
    WorldGrid world;
    SpriteBatch batch;

    private static final int MAP_WIDTH = 25;
    private static final int MAP_HEIGHT = 18;

    private static final int DIRT_WIDTH = 5;
    private static final int DIRT_HEIGHT = 4;

    private final int dirtStartX = (MAP_WIDTH / 2) - (DIRT_WIDTH / 2);
    private final int dirtStartY = (MAP_HEIGHT / 2) - (DIRT_HEIGHT / 2);

    @Override
    public void create() {
        shape = new ShapeRenderer();
        batch = new SpriteBatch();

        world = new WorldGrid(MAP_WIDTH, MAP_HEIGHT);

        // Create your dirt patch
        world.createDirtPatch(dirtStartX, dirtStartY, DIRT_WIDTH, DIRT_HEIGHT);
    }

    @Override
    public void render() {

        // -------------------------
        // 1) Update input + player
        // -------------------------
        input.update(player);
        player.clampPosition();

        ScreenUtils.clear(0, 0.6f, 0, 1);

        int tx = player.x;
        int ty = player.y;

        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) world.plantCrop(tx, ty);
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) world.water(tx, ty);
        if (Gdx.input.isKeyJustPressed(Input.Keys.H)) world.harvest(tx, ty);

        world.update(Gdx.graphics.getDeltaTime());


        // -------------------------------------------------
        // 2) FILLED SHAPES → Grass, Dirt, Watered Dirt
        // -------------------------------------------------
        shape.begin(ShapeRenderer.ShapeType.Filled);
        world.renderFill(shape);     // grass + dirt only
        shape.end();


        // -------------------------------------------------
        // 3) SPRITE TEXTURES → Crops (using batch)
        // -------------------------------------------------
        batch.begin();
        world.renderCrops(batch);    // crops with images
        batch.end();


        // -------------------------------------------------
        // 4) PLAYER (still ShapeRenderer)
        // -------------------------------------------------
        shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.setColor(1, 1, 1, 1);
        shape.rect(
            player.x * WorldGrid.TILE_SIZE,
            player.y * WorldGrid.TILE_SIZE,
            WorldGrid.TILE_SIZE,
            WorldGrid.TILE_SIZE
        );
        shape.end();


        // -------------------------------------------------
        // 5) GRID LINES LAST (ShapeRenderer Line mode)
        // -------------------------------------------------
        shape.begin(ShapeRenderer.ShapeType.Line);
        shape.setColor(0, 0, 0, 1);
        world.renderGridLines(shape);
        shape.end();
    }





    @Override
    public void dispose() {
        shape.dispose();
    }
}


