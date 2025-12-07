package com.lamnguyen.farming;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
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

    @Override
    public void create() {
        shape = new ShapeRenderer();
        batch = new SpriteBatch();

        world = new WorldGrid(MAP_WIDTH, MAP_HEIGHT);

        // Create your dirt patch
        int dirtStartY = (MAP_HEIGHT / 2) - (DIRT_HEIGHT / 2);
        int dirtStartX = (MAP_WIDTH / 2) - (DIRT_WIDTH / 2);
        world.createDirtPatch(dirtStartX, dirtStartY, DIRT_WIDTH, DIRT_HEIGHT);
        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("atlas/player.atlas"));
        player.loadTextures(atlas);
    }


    @Override
    public void render() {
        float dt = Gdx.graphics.getDeltaTime();

        input.updatePlayer(player, dt);
        player.clampPosition(world.getWidth() - 1, world.getHeight() - 1);
        ScreenUtils.clear(0, 0.6f, 0, 1);

        world.update(dt);
        player.updateAnimation(dt);
        input.updateWorld(player, world);

        // -----------------------------------------
        // 1) DRAW FILLED SHAPES (grass + dirt)
        // -----------------------------------------
        shape.begin(ShapeRenderer.ShapeType.Filled);

        world.renderFill(batch);       // grass + base dirt


        // -----------------------------------------
        // 2) DRAW CROPS WITH SPRITEBATCH
        // -----------------------------------------
        batch.begin();
        world.renderCrops(batch);
        batch.end();

        // --- 3. Draw player ---
        batch.begin();
        batch.draw(
            player.getSprite(),
            player.x,
            player.y
        );

        batch.end();

        shape.end();
        // -----------------------------------------
        // 3) GRID LINES LAST
        // -----------------------------------------
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


