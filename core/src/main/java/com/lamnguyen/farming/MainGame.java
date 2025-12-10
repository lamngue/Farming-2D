package com.lamnguyen.farming;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.lamnguyen.farming.entities.*;
import com.lamnguyen.farming.systems.InputSystem;
import com.lamnguyen.farming.systems.RenderSystem;
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
    RenderSystem renderSystem = new RenderSystem();
    Inventory inventory;
    BitmapFont font;
    private static final int DIRT_WIDTH = 5;
    private static final int DIRT_HEIGHT = 4;
    Texture whitePixelTexture;

    @Override
    public void create() {
        shape = new ShapeRenderer();
        batch = new SpriteBatch();
        // Initialize inventory
        inventory = new Inventory();
        inventory.add(ItemType.WHEAT_SEED, 5);
        inventory.add(ItemType.WHEAT_CROP, 2);

        // Load fonts
        font = new BitmapFont();
        font.getData().setScale(1f);

        // Load crop textures
        for (CropType crop : CropType.values()) {
            crop.loadTextures();
        }

        // Load item textures
        for (ItemType item : ItemType.values()) {
            item.loadTexture();
        }
        world = new WorldGrid(MAP_WIDTH, MAP_HEIGHT);


        // Create your dirt patch
        int dirtStartY = (MAP_HEIGHT / 2) - (DIRT_HEIGHT / 2);
        int dirtStartX = (MAP_WIDTH / 2) - (DIRT_WIDTH / 2);
        world.createDirtPatch(dirtStartX, dirtStartY, DIRT_WIDTH, DIRT_HEIGHT);
        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("atlas/player.atlas"));
        player.loadTextures(atlas);

        // White pixel for inventory box backgrounds
        Pixmap pix = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pix.setColor(Color.WHITE);
        pix.fill();
        whitePixelTexture = new Texture(pix);
        pix.dispose();
    }


    @Override
    public void render() {
        float dt = Gdx.graphics.getDeltaTime();

        // --- Update ---
        input.updatePlayer(player, dt);
        player.clampPosition(world.getWidth() - 1, world.getHeight() - 1);
        world.update(dt);
        player.updateAnimation(dt);
        input.updateWorld(player, world);

        // --- Clear screen ---
        ScreenUtils.clear(0, 0.6f, 0, 1);

        // --- Render everything via RenderSystem ---
        renderSystem.renderWorld(shape, batch, world);
        renderSystem.renderPlayer(batch, player);
        renderSystem.renderUI(batch, whitePixelTexture, player.inventory, font);
    }



    @Override
    public void dispose() {
        shape.dispose();
    }
}


