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
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
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

public class GameScreen implements Screen {
    private final MainGame game;
    private final WorldType worldType;
    private String uiMessage = null;
    private float uiMessageTimer = 0f;
    private static final float UI_MESSAGE_DURATION = 3f;
    ShapeRenderer shape;
    private boolean screenChangeQueued = false;


    Player player = new Player();
    InputSystem input = new InputSystem();
    WorldGrid world;
    SpriteBatch batch;
    OrthographicCamera camera;
    private ShopState shopState = new ShopState();
    private OrthographicCamera uiCamera = new OrthographicCamera();

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
    private boolean shopOpen = false;
    private boolean shopTriggered = false; // prevents reopening while overlapping

    public GameScreen(MainGame game, boolean loadGame, WorldType type) {
        this.game = game;
        this.loadGame = loadGame;
        this.worldType = type;
        this.uiCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.shape = new ShapeRenderer();
        this.batch = game.batch;
        this.font = new BitmapFont();
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
            System.out.println("LOADING SAVED GAME");
            SaveData data = saveManager.load();
            applySave(data);
        }

        // --- Rendering setup ---


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

        // --- Handle shop input FIRST ---
        if (shopOpen) {
            handleShopKeyboardInput();
        }


        if (uiMessageTimer > 0) {
            uiMessageTimer -= delta;
            if (uiMessageTimer <= 0) {
                uiMessage = null;
            }
        }

        // --- Save ---
        if (Gdx.input.isKeyJustPressed(Input.Keys.F5)) {
            SaveManager.save(this);
        }

        float dt = Gdx.graphics.getDeltaTime();

        // --- Update ---

        if (!shopOpen) {
            input.updatePlayer(player, dt);
        }

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

            // Open shop ONLY when entering
            if (overlappingShop && !shopOpen && !shopTriggered) {
                shopOpen = true;
                shopTriggered = true;
                shopState.reset();
                updateShopItems();
            }

            // Reset trigger ONLY when fully leaving zone
            if (!overlappingShop && !shopOpen) {
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


        batch.setProjectionMatrix(uiCamera.combined);

        batch.begin();
        // --- Handle UI input FIRST ---

        if (shopOpen) {
            renderSystem.renderShopPanel(batch, whitePixelTexture, font, shopState, player);
        }
        if (uiMessage != null) {
            renderSystem.renderTopMessage(batch, font, uiMessage);
        }
        renderSystem.renderUI(batch, whitePixelTexture, font, player);
        font.setColor(Color.WHITE);

        batch.end();


        Rectangle exitZone = world.getExitZone(worldType);
        Rectangle playerRect = new Rectangle(
            player.x,
            player.y,
            player.getSprite().getRegionWidth(),
            player.getSprite().getRegionHeight()
        );

        if (!screenChangeQueued && exitZone.overlaps(playerRect)) {
            screenChangeQueued = true;

            WorldType next =
                (worldType == WorldType.FARM)
                    ? WorldType.GREEN_FIELD
                    : WorldType.FARM;

            // Delay screen change to NEXT FRAME
            Gdx.app.postRunnable(() -> {
                game.setScreen(new LoadingScreen(game, next));
            });
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
        font.dispose();
    }

    private void updateShopItems() {
        shopState.visibleItems.clear();

        for (ItemType item : ItemType.values()) {
            if (shopState.activeTab == ShopState.ShopTab.BUY && item.isSeed()) {
                shopState.visibleItems.add(item);
            }
            if (shopState.activeTab == ShopState.ShopTab.SELL && !item.isSeed()) {
                if (player.inventory.get(item) > 0) {
                    shopState.visibleItems.add(item);
                }
            }
        }

        shopState.selectedIndex = MathUtils.clamp(
            shopState.selectedIndex,
            0,
            Math.max(0, shopState.visibleItems.size - 1)
        );
    }

    private void handleShopKeyboardInput() {
        if (!shopOpen) return;

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            closeShop();
            return;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
            shopState.activeTab =
                shopState.activeTab == ShopState.ShopTab.BUY
                    ? ShopState.ShopTab.SELL
                    : ShopState.ShopTab.BUY;

            shopState.reset();
            updateShopItems();
            return;
        }

        if (shopState.visibleItems.isEmpty()) return;

        if (Gdx.input.isKeyJustPressed(Input.Keys.W)) {
            shopState.selectedIndex =
                Math.max(0, shopState.selectedIndex - 1);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            shopState.selectedIndex =
                Math.min(shopState.visibleItems.size - 1,
                    shopState.selectedIndex + 1);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.A)) {
            shopState.selectedAmount =
                Math.max(1, shopState.selectedAmount - 1);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.D)) {
            shopState.selectedAmount++;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            ItemType item = shopState.visibleItems.get(shopState.selectedIndex);

            if (shopState.activeTab == ShopState.ShopTab.BUY) {
                buyItem(item);
            } else {
                sellItem(item);
            }

            updateShopItems(); // refresh after inventory change
        }
    }


    private void closeShop() {
        shopOpen = false;
        shopTriggered = true;   // prevent immediate reopen while overlapping
        shopState.reset();
    }


    private void sellItem(ItemType item) {
        int owned = player.inventory.get(item);
        int amount = Math.min(shopState.selectedAmount, owned);

        if (amount <= 0) return;

        player.inventory.remove(item, amount);
        player.addMoney(amount * item.sellPrice);

        shopState.selectedAmount = 1;
    }



    private void buyItem(ItemType item) {
        int cost = shopState.selectedAmount * item.buyPrice;

        if (player.money < cost) {
            showUIMessage("Not enough money!");
            return;
        }

        player.subtractMoney(cost);
        player.inventory.add(item, shopState.selectedAmount);

        shopState.selectedAmount = 1;
    }

    private void showUIMessage(String message) {
        uiMessage = message;
        uiMessageTimer = UI_MESSAGE_DURATION;
    }


}
