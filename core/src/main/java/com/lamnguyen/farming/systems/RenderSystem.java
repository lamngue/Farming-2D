package com.lamnguyen.farming.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.lamnguyen.farming.entities.Inventory;
import com.lamnguyen.farming.entities.ItemType;
import com.lamnguyen.farming.entities.Player;
import com.lamnguyen.farming.state.ShopState;
import com.lamnguyen.farming.world.WorldGrid;
import com.lamnguyen.farming.world.WorldType;

public class RenderSystem {

    private static final int SEED_BOX_SIZE = 60;
    private static final int STORAGE_BOX_SIZE = 60;
    private static final int BOX_PADDING = 10;

    public void renderWorld(ShapeRenderer shape, SpriteBatch batch, WorldGrid world, OrthographicCamera camera,  WorldType worldType) {

        // --- Filled tiles ---
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        world.renderFill(batch);
        world.renderShop(batch, worldType);
        world.renderExitArrow(batch, worldType);
        world.renderCrops(batch);
        batch.end();


        // --- Crops ---
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        world.renderCrops(batch);
        batch.end();

        // --- Grid lines ---
        shape.setProjectionMatrix(camera.combined);
        shape.begin(ShapeRenderer.ShapeType.Line);
        shape.setColor(0, 0, 0, 1);
        world.renderGridLines(shape);
        shape.end();
    }


    public void renderShopPanel(
        SpriteBatch batch,
        Texture whitePixel,
        BitmapFont font,
        ShopState shopState,
        Player player
    ) {
        int panelW = 420;
        int panelH = 220;


        int x = (Gdx.graphics.getWidth() - panelW) / 2;
        int y = 40;
        int rowY = y + panelH - 70;

        for (int i = 0; i < shopState.visibleItems.size; i++) {
            ItemType item = shopState.visibleItems.get(i);

            boolean selected = (i == shopState.selectedIndex);

            int rowH = 26;
            int rowW = 260;

            int rowX = x + 16;
            int rowYPos = rowY - i * (rowH + 6);

            if (selected) {
                batch.setColor(0.9f, 0.9f, 0.3f, 1);
                batch.draw(whitePixel, rowX, rowYPos - rowH + 4, rowW, rowH);
                batch.setColor(Color.WHITE);
            }

            String leftText = item.name();
            String rightText;

            if (shopState.activeTab == ShopState.ShopTab.BUY) {
                rightText = "$" + item.buyPrice;
            } else {
                rightText = "$" + item.sellPrice;
            }

            font.draw(batch, leftText, rowX + 8, rowYPos);
            font.draw(batch, rightText, rowX + rowW - 40, rowYPos);
        }

        if (!shopState.visibleItems.isEmpty()) {

            ItemType selected =
                shopState.visibleItems.get(shopState.selectedIndex);

            if (shopState.activeTab == ShopState.ShopTab.BUY) {
                shopState.selectedAmount =
                    MathUtils.clamp(shopState.selectedAmount, 1, 99);
            } else {
                int owned = player.inventory.get(selected);
                shopState.selectedAmount =
                    MathUtils.clamp(shopState.selectedAmount, 1, owned);
            }
        }

        int qtyY = rowY + 40;

        font.draw(batch, "Quantity:", x + 16, qtyY + 16);

        batch.draw(whitePixel, x + 120, qtyY, 24, 24);
        font.draw(batch, "-", x + 128, qtyY + 18);

        font.draw(batch,
            String.valueOf(shopState.selectedAmount),
            x + 160,
            qtyY + 18
        );

        batch.draw(whitePixel, x + 190, qtyY, 24, 24);
        font.draw(batch, "+", x + 198, qtyY + 18);
        String action =
            (shopState.activeTab == ShopState.ShopTab.BUY) ? "ENTER = BUY" : "ENTER = SELL";

        font.draw(batch, action, x + 16, y + 20);

    }

    public void renderPlayer(SpriteBatch batch, Player player, OrthographicCamera camera) {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(player.getSprite(), player.x, player.y);
        batch.end();
    }

    public void renderTopMessage(
        SpriteBatch batch,
        BitmapFont font,
        String message
    ) {
        if (message == null) return;

        Color oldColor = font.getColor().cpy(); // save current color

        font.setColor(Color.RED);

        GlyphLayout layout = new GlyphLayout(font, message);
        float x = (Gdx.graphics.getWidth() - layout.width) / 2f;
        float y = Gdx.graphics.getHeight() - 30;

        font.draw(batch, message, x, y);

        font.setColor(oldColor); // 🔑 RESTORE
    }


    public void renderUI(SpriteBatch batch, Texture whitePixelTexture, BitmapFont font, Player player) {
        // Use screen coordinates
        OrthographicCamera uiCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        uiCamera.setToOrtho(false); // origin bottom-left
        batch.setProjectionMatrix(uiCamera.combined);
        String text = "Money: $" + player.money;

        font.setColor(Color.WHITE);
        // --- Money at top-center ---
        GlyphLayout layout = new GlyphLayout(font, text);
        float moneyX = (Gdx.graphics.getWidth() - layout.width) / 2f;
        float moneyY = Gdx.graphics.getHeight() - 10; // 10px from top
        font.draw(batch, text, moneyX, moneyY);


        // --- Seed hotbar at top-left ---
        renderSeedHotbar(whitePixelTexture, batch, player.inventory, font, player.selectedSeed);

        // --- Crop storage at top-right ---
        renderCropStorage(whitePixelTexture, batch, player.inventory, font);

    }


    public void renderSeedHotbar(
        Texture whitePixelTexture,
        SpriteBatch batch,
        Inventory inventory,
        BitmapFont font,
        ItemType selectedSeed
    ) {
        int boxSize = SEED_BOX_SIZE;
        int padding = BOX_PADDING;

        int startX = 16;
        int startY = Gdx.graphics.getHeight() - boxSize - 16;

        int x = startX;
        int y = startY;

        int itemsPerRow = 3;
        int index = 0;

        for (ItemType item : ItemType.values()) {
            if (!item.isSeed()) continue;

            int amount = inventory.get(item);
            if (amount <= 0) continue;

            // Highlight selected seed
            batch.setColor(item == selectedSeed
                ? new Color(0.9f, 0.9f, 0.3f, 1)
                : new Color(0.15f, 0.15f, 0.15f, 1));

            batch.draw(whitePixelTexture, x, y, boxSize, boxSize);
            batch.setColor(Color.WHITE);

            batch.draw(
                item.icon,
                x + 6,
                y + 6,
                boxSize - 10,
                boxSize - 10
            );

            font.draw(
                batch,
                String.valueOf(amount),
                x + boxSize - 10,
                y + 12
            );

            index++;
            if (index % itemsPerRow == 0) {
                x = startX;
                y -= boxSize + padding;
            } else {
                x += boxSize + padding;
            }
        }
    }



    public void renderCropStorage(
        Texture whitePixelTexture,
        SpriteBatch batch,
        Inventory inventory,
        BitmapFont font
    ) {
        int boxSize = STORAGE_BOX_SIZE;
        int padding = BOX_PADDING;

        int x = Gdx.graphics.getWidth() - boxSize - 16;
        int y = Gdx.graphics.getHeight() - boxSize - 16;

        for (ItemType item : ItemType.values()) {
            if (item.isSeed()) continue;

            int amount = inventory.get(item);
            if (amount <= 0) continue;

            batch.setColor(0.15f, 0.15f, 0.15f, 1);
            batch.draw(whitePixelTexture, x, y, boxSize, boxSize);
            batch.setColor(Color.WHITE);

            batch.draw(item.icon,
                x + 6,
                y + 6,
                boxSize - 12,
                boxSize - 12
            );

            font.draw(batch,
                String.valueOf(amount),
                x + boxSize - 18,
                y + 20
            );

            y -= boxSize + padding;
        }
    }

    public void renderWorldExit(SpriteBatch batch, Texture arrowTex, WorldGrid world) {
        float x = world.getWidth() * WorldGrid.TILE_SIZE - 32;
        float y = (world.getHeight() * WorldGrid.TILE_SIZE) / 2f - 16;

        batch.draw(arrowTex, x, y, 32, 32);
    }


}


