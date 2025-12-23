package com.lamnguyen.farming;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ScreenUtils;
import com.lamnguyen.farming.entities.*;
import com.lamnguyen.farming.save.CropData;
import com.lamnguyen.farming.save.SaveData;
import com.lamnguyen.farming.save.SaveManager;
import com.lamnguyen.farming.systems.InputSystem;
import com.lamnguyen.farming.systems.RenderSystem;
import com.lamnguyen.farming.world.WorldGrid;

public class GameScreen  implements Screen {
    private final Game game;

    ShapeRenderer shape;


    Player player = new Player();
    InputSystem input = new InputSystem();
    WorldGrid world;
    SpriteBatch batch;
    OrthographicCamera camera;

    private static final int MAP_WIDTH = 25;
    private static final int MAP_HEIGHT = 18;
    RenderSystem renderSystem = new RenderSystem();
    Inventory inventory;
    BitmapFont font;
    private static final int DIRT_WIDTH = 5;
    private static final int DIRT_HEIGHT = 4;
    Texture whitePixelTexture;
    private final boolean loadGame;
    private SaveManager saveManager;

    public GameScreen(Game game, boolean loadGame) {
        this.game = game;

        this.loadGame = loadGame;
    }


    public Player getPlayer() {
        return player;
    }

    public WorldGrid getWorld() {
        return world;
    }


    private void startNewGame() {
        inventory.clear();
        player.x = 5 * WorldGrid.TILE_SIZE;
        player.y = 5 * WorldGrid.TILE_SIZE;
    }

    private void applySave(SaveData data) {

        // Player
        player.x = data.player.x;
        player.y = data.player.y;
        player.direction = data.player.direction;
        player.money = data.player.money;
        // Inventory
        player.inventory.clear();
        for (ObjectMap.Entry<String, Integer> e : data.inventory.items) {
            ItemType item = ItemType.valueOf(e.key);
            player.inventory.set(item, e.value);
        }

        // World
        for (CropData cd : data.world.crops) {
            CropType type = CropType.valueOf(cd.type);
            Crop crop = new Crop(type, cd.x, cd.y);
            crop.growthStage = cd.growthStage;
            crop.isWatered = cd.watered;
            crop.growTimer = cd.growTimer;
            crop.fertilizerLevel = cd.fertilizer;
            world.setCrop(cd.x, cd.y, crop);
        }
    }



    @Override
    public void show() {
        saveManager = new SaveManager();

        // --- World and Player ---
        world = new WorldGrid(MAP_WIDTH, MAP_HEIGHT);
        player = new Player();
        inventory = new Inventory();

        // Center dirt patch
        int dirtStartY = (MAP_HEIGHT - DIRT_HEIGHT) / 2;
        int dirtStartX = (MAP_WIDTH - DIRT_WIDTH) / 2;
        world.createDirtPatch(dirtStartX, dirtStartY, DIRT_WIDTH, DIRT_HEIGHT);

        // Load save or start new game
        if (loadGame && saveManager.hasSave()) {
            SaveData data = saveManager.load();
            applySave(data);
        } else {
            startNewGame();
        }

        // --- Rendering setup ---
        shape = new ShapeRenderer();
        batch = new SpriteBatch();

        // World camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false,
            MAP_WIDTH * WorldGrid.TILE_RENDER_SIZE,
            MAP_HEIGHT * WorldGrid.TILE_RENDER_SIZE
        );
        camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0);
        camera.update();

        font = new BitmapFont();

        // Load textures
        for (CropType crop : CropType.values()) crop.loadTextures();
        for (ItemType item : ItemType.values()) item.loadTexture();

        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("atlas/player.atlas"));
        player.loadTextures(atlas);

        // White pixel for UI boxes
        Pixmap pix = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pix.setColor(Color.WHITE);
        pix.fill();
        whitePixelTexture = new Texture(pix);
        pix.dispose();
    }


    @Override
    public void render(float delta) {
        // --- Save ---
        if (Gdx.input.isKeyJustPressed(Input.Keys.F5)) {
            SaveManager.save(this);
        }

        float dt = Gdx.graphics.getDeltaTime();

        // --- Update ---
        input.updatePlayer(player, dt);
        player.clampPosition(world.getWidth() - 1, world.getHeight() - 1);
        world.update(dt);
        player.updateAnimation(dt);
        input.updateWorld(player, world);
        input.updateSeedSelection(player);

        ScreenUtils.clear(0, 0.6f, 0, 1);

        // --- Render World ---
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        shape.setProjectionMatrix(camera.combined);

        // World tiles, crops, grid lines
        renderSystem.renderWorld(shape, batch, world, camera);

        // Player
        renderSystem.renderPlayer(batch, player, camera);

        // --- Render UI in screen coordinates ---
        renderSystem.renderUI(batch, whitePixelTexture, font, player);
    }


    @Override
    public void resize(int width, int height) {
        camera.viewportWidth  = MAP_WIDTH * WorldGrid.TILE_RENDER_SIZE;
        camera.viewportHeight = MAP_HEIGHT * WorldGrid.TILE_RENDER_SIZE;
        camera.position.set(
            camera.viewportWidth / 2f,
            camera.viewportHeight / 2f,
            0
        );
        camera.update();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        shape.dispose();
        batch.dispose();
    }
}
