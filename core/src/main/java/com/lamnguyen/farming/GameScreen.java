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
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ScreenUtils;
import com.lamnguyen.farming.entities.*;
import com.lamnguyen.farming.save.CropData;
import com.lamnguyen.farming.save.SaveData;
import com.lamnguyen.farming.save.SaveManager;
import com.lamnguyen.farming.state.ShopState;
import com.lamnguyen.farming.systems.InputSystem;
import com.lamnguyen.farming.systems.RenderSystem;
import com.lamnguyen.farming.utils.CameraUtils;
import com.lamnguyen.farming.world.WorldGrid;
import com.lamnguyen.farming.world.WorldType;

import java.util.Map;

public class GameScreen  implements Screen {
    private final MainGame game;
    private final WorldType worldType;

    ShapeRenderer shape;


    Player player = new Player();
    InputSystem input = new InputSystem();
    WorldGrid world;
    SpriteBatch batch;
    OrthographicCamera camera;
    private ShopState shopState = new ShopState();

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
    private boolean transitioning = false;
    private boolean shopOpen = false;
    private boolean shopTriggered = false; // prevents reopening while overlapping

    public GameScreen(MainGame game, boolean loadGame, WorldType type) {
        this.game = game;
        this.loadGame = loadGame;
        this.worldType = type;
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
        transitioning = false;

        saveManager = new SaveManager();

        // --- Create world ONCE ---
        world = new WorldGrid(MAP_WIDTH, MAP_HEIGHT);
        player = game.player;
        inventory = game.inventory;
        int dirtStartY = (MAP_HEIGHT - DIRT_HEIGHT) / 2;
        int dirtStartX = (MAP_WIDTH - DIRT_WIDTH) / 2;

        // --- World type setup ---
        if (worldType == WorldType.FARM) {

            world.createDirtPatch(dirtStartX, dirtStartY, DIRT_WIDTH, DIRT_HEIGHT);

            player.x = 5 * WorldGrid.TILE_SIZE;
            player.y = 5 * WorldGrid.TILE_SIZE;

        } else if (worldType == WorldType.GREEN_FIELD) {

            world.fillWithGrassOnly();

            player.x = WorldGrid.TILE_SIZE * 2f;
            player.y = (MAP_HEIGHT * WorldGrid.TILE_SIZE) / 2f;

        }

        // --- Load save OR start fresh ---
        if (loadGame && saveManager.hasSave() && worldType == WorldType.FARM) {
            SaveData data = saveManager.load();
            applySave(data);
        }

        // --- Rendering setup ---
        shape = new ShapeRenderer();
        batch = new SpriteBatch();
        font = new BitmapFont();

        // Camera (DO NOT recreate world after this)
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 640, 360);
        camera.zoom = 0.8f;
        camera.update();

        // Load textures
        for (CropType crop : CropType.values()) crop.loadTextures();
        for (ItemType item : ItemType.values()) item.loadTexture();

        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("atlas/player.atlas"));
        player.loadTextures(atlas);

        // White pixel
        Pixmap pix = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pix.setColor(Color.WHITE);
        pix.fill();
        whitePixelTexture = new Texture(pix);
        pix.dispose();

        Gdx.app.log("WORLD", "WorldType = " + worldType + ", crops loaded = " + (worldType == WorldType.FARM));
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
        player.clampPosition(world.getWidth(), world.getHeight());
        world.update(dt);
        player.updateAnimation(dt);

        float targetX = player.x + player.getWidth() / 2f;
        float targetY = player.y + player.getHeight() / 2f;

        float smoothing = 0.1f;

        CameraUtils.smoothFollow(camera, targetX, targetY, smoothing);
        CameraUtils.clampCamera(camera, world.getWidth(), world.getHeight());
        camera.update();
        input.updateWorld(player, world);
        input.updateSeedSelection(player);

        ScreenUtils.clear(0, 0.6f, 0, 1);

        if (worldType == WorldType.GREEN_FIELD) {

            Rectangle playerRect = new Rectangle(
                player.x,
                player.y,
                player.getSprite().getRegionWidth(),
                player.getSprite().getRegionHeight()
            );

            Rectangle shopBounds = world.getShopBounds();
            boolean overlappingShop = shopBounds.overlaps(playerRect);

            if (overlappingShop && !shopTriggered) {
                shopOpen = true;
                shopTriggered = true;
            }

            if (shopOpen && Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || !overlappingShop) {
                shopOpen = false;
            }

            if (!overlappingShop) {
                shopTriggered = false;
            }
        }




        // --- Render World ---
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        shape.setProjectionMatrix(camera.combined);

        // World tiles, crops, grid lines
        renderSystem.renderWorld(shape, batch, world, camera, worldType);

        // Player
        renderSystem.renderPlayer(batch, player, camera);

        // --- Render UI in screen coordinates ---
        OrthographicCamera uiCamera = new OrthographicCamera();
        uiCamera.setToOrtho(false,
            Gdx.graphics.getWidth(),
            Gdx.graphics.getHeight()
        );
        uiCamera.update();

        batch.setProjectionMatrix(uiCamera.combined);

        batch.begin();
        if (shopOpen) {
            handleShopMouseInput();
        }

        renderSystem.renderUI(batch, whitePixelTexture, font, player);

        if (shopOpen) {
            renderSystem.renderShopPanel(batch, whitePixelTexture, font, player, shopState);
        }

        batch.end();

        Rectangle exitZone = world.getExitZone(worldType);
        Rectangle playerRect = new Rectangle(
            player.x,
            player.y,
            player.getSprite().getRegionWidth(),
            player.getSprite().getRegionHeight()
        );

        if (exitZone.overlaps(playerRect) && !transitioning) {
            transitioning = true;

            WorldType next =
                (worldType == WorldType.FARM)
                    ? WorldType.GREEN_FIELD
                    : WorldType.FARM;

            game.setScreen(new LoadingScreen(game, next));
        }


    }


    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, 640, 480);
        camera.zoom = 0.8f;

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

    private void handleShopMouseInput() {

        if (!Gdx.input.justTouched()) return;

        float mx = Gdx.input.getX();
        float my = Gdx.graphics.getHeight() - Gdx.input.getY(); // flip Y

        Vector2 mouse = new Vector2(mx, my);



        if (shopState.buyTab.contains(mouse)) {
            shopState.activeTab = ShopState.ShopTab.BUY;
            shopState.selectedItem = null;
            shopState.selectedAmount = 1;
        }

        if (shopState.sellTab.contains(mouse)) {
            shopState.activeTab = ShopState.ShopTab.SELL;
            shopState.selectedItem = null;
            shopState.selectedAmount = 1;
        }

        // Click outside panel → close
        if (!shopState.panelBounds.contains(mouse)) {
            shopOpen = false;
            shopState.reset();
            return;
        }

        // Select item
        for (Map.Entry<ItemType, Rectangle> e : shopState.itemButtons.entrySet()) {
            if (e.getValue().contains(mouse)) {
                shopState.selectedItem = e.getKey();
                shopState.selectedAmount = 1;
                return;
            }
        }

        // Quantity buttons
        if (shopState.plusButton.contains(mouse)) {
            shopState.selectedAmount++;
            return;
        }

        if (shopState.minusButton.contains(mouse)) {
            shopState.selectedAmount = Math.max(1, shopState.selectedAmount - 1);
            return;
        }

        // Sell
        if (shopState.sellButton.contains(mouse)) {
            sellSelectedItem();
        }

        if (shopState.buyButton.contains(mouse)) {
            buySelectedItem();
        }
    }

    private void sellSelectedItem() {
        ItemType item = shopState.selectedItem;
        if (item == null || item.sellPrice == null) return;

        int owned = player.inventory.get(item);
        int sellAmount = Math.min(shopState.selectedAmount, owned);
        if (sellAmount <= 0) return;

        player.inventory.remove(item, sellAmount);
        player.addMoney(sellAmount * item.sellPrice);

        shopState.selectedAmount = 1;
    }



    private void buySelectedItem() {
        ItemType item = shopState.selectedItem;
        if (item == null || item.sellPrice == null) return;

        int cost = shopState.selectedAmount * item.sellPrice;
        if (player.money < cost) return;

        player.subtractMoney(cost);
        player.inventory.add(item, shopState.selectedAmount);

        shopState.selectedAmount = 1;
    }

}
