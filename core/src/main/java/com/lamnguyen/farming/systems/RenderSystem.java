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
import com.badlogic.gdx.math.Rectangle;
import com.lamnguyen.farming.entities.CropType;
import com.lamnguyen.farming.entities.Inventory;
import com.lamnguyen.farming.entities.ItemType;
import com.lamnguyen.farming.entities.Player;
import com.lamnguyen.farming.state.ShopState;
import com.lamnguyen.farming.world.WorldGrid;
import com.lamnguyen.farming.world.WorldType;

import static com.lamnguyen.farming.state.ShopState.ShopTab.BUY;
import static com.lamnguyen.farming.state.ShopState.ShopTab.SELL;

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
        Player player,
        ShopState shop
    ) {
        int panelW = 420;
        int panelH = 220;


        int x = (Gdx.graphics.getWidth() - panelW) / 2;
        int y = 40;


        shop.panelBounds.set(x, y, panelW, panelH);

        batch.setColor(0, 0, 0, 0.75f);
        batch.draw(whitePixel, x, y, panelW, panelH);
        batch.setColor(Color.WHITE);
        String title = shop.activeTab == BUY ? "BUY CROPS" : "SELL CROPS";
        font.draw(batch, title, x + 16, y + panelH - 16);


        int rowY = y + panelH - 70;
        shop.itemButtons.clear();

        shop.buyTab.set(x + 16, y + panelH - 60, 80, 24);
        shop.sellTab.set(x + 104, y + panelH - 60, 80, 24);

        batch.setColor(shop.activeTab == BUY ? new Color(0.9f, 0.9f, 0.3f, 1): new Color(0.15f, 0.15f, 0.15f, 1));
        batch.draw(whitePixel, shop.buyTab.x, shop.buyTab.y, 80, 24);
        font.draw(batch, "BUY", shop.buyTab.x + 22, shop.buyTab.y + 18);

        batch.setColor(shop.activeTab == SELL ? new Color(0.9f, 0.9f, 0.3f, 1) : new Color(0.15f, 0.15f, 0.15f, 1));
        batch.draw(whitePixel, shop.sellTab.x, shop.sellTab.y, 80, 24);
        font.draw(batch, "SELL", shop.sellTab.x + 18, shop.sellTab.y + 18);
        batch.setColor(Color.WHITE);


        if (shop.activeTab == BUY) {
            for (ItemType item : ItemType.values()) {
                if (!item.isSeed()) continue;

                Rectangle r = new Rectangle(x + 16, rowY - 26, 200, 26);
                shop.itemButtons.put(item, r);


                if (item == shop.selectedItem) {
                    batch.setColor(0.9f, 0.9f, 0.3f, 1);
                    batch.draw(whitePixel, r.x, r.y, r.width, r.height);
                    batch.setColor(Color.WHITE);
                }

                font.draw(batch,
                    item.name() + " $" + item.buyPrice,
                    r.x + 8,
                    r.y + 18
                );

                rowY -= 32;
            }
        }

        if (shop.activeTab == SELL) {
            for (ItemType item : ItemType.values()) {
                if (item.isSeed()) continue;

                int amount = player.inventory.get(item);
                if (amount <= 0) continue;

                Rectangle r = new Rectangle(x + 16, rowY - 26, 200, 26);
                shop.itemButtons.put(item, r);


                if (item == shop.selectedItem) {
                    batch.setColor(0.9f, 0.9f, 0.3f, 1);
                    batch.draw(whitePixel, r.x, r.y, r.width, r.height);
                    batch.setColor(Color.WHITE);
                }

                font.draw(batch,
                    item.name() + " x" + amount,
                    r.x + 8,
                    r.y + 18
                );

                rowY -= 32;
            }
        }

        if (shop.selectedItem != null) {

            ItemType item = shop.selectedItem;
            int owned = player.inventory.get(item);

            if (shop.activeTab == SELL) {
                shop.selectedAmount = MathUtils.clamp(shop.selectedAmount, 1, owned);
            }
            if (shop.activeTab == BUY) {
                shop.selectedAmount = MathUtils.clamp(shop.selectedAmount, 1, 99);
            }

            int ctrlY = y + 24;

            // - button
            shop.minusButton.set(x + 220, ctrlY, 24, 24);
            batch.draw(whitePixel, shop.minusButton.x, shop.minusButton.y, 24, 24);
            font.draw(batch, "-", shop.minusButton.x + 8, ctrlY + 18);

            // + button
            shop.plusButton.set(x + 300, ctrlY, 24, 24);
            batch.draw(whitePixel, shop.plusButton.x, shop.plusButton.y, 24, 24);
            font.draw(batch, "+", shop.plusButton.x + 6, ctrlY + 18);

            // Quantity label
            font.draw(batch,
                "Qty: " + shop.selectedAmount,
                x + 250,
                ctrlY + 18
            );

            // Sell button
            if (shop.activeTab == SELL && !item.isSeed()) {
                shop.sellButton.set(x + 220, ctrlY + 40, 120, 28);
                batch.setColor(0.2f, 0.7f, 0.2f, 1);
                batch.draw(whitePixel,
                    shop.sellButton.x,
                    shop.sellButton.y,
                    shop.sellButton.width,
                    shop.sellButton.height
                );
                batch.setColor(Color.WHITE);

                int totalPrice = shop.selectedAmount * item.sellPrice;

                font.draw(batch,
                    "SELL $" + totalPrice,
                    shop.sellButton.x + 10,
                    shop.sellButton.y + 20
                );
            }
            if (shop.activeTab == BUY) {
                shop.buyButton.set(x + 220, ctrlY + 40, 120, 28);
                batch.setColor(0.2f, 0.7f, 0.2f, 1);
                batch.draw(whitePixel,
                    shop.buyButton.x,
                    shop.buyButton.y,
                    shop.buyButton.width,
                    shop.buyButton.height
                );
                batch.setColor(Color.WHITE);

                int totalPrice = shop.selectedAmount * item.buyPrice;

                font.draw(batch,
                    "BUY $" + totalPrice,
                    shop.buyButton.x + 10,
                    shop.buyButton.y + 20
                );
            }
        }
    }

    public void renderPlayer(SpriteBatch batch, Player player, OrthographicCamera camera) {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(player.getSprite(), player.x, player.y);
        batch.end();
    }

    public void renderUI(SpriteBatch batch, Texture whitePixelTexture, BitmapFont font, Player player) {
        // Use screen coordinates
        OrthographicCamera uiCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        uiCamera.setToOrtho(false); // origin bottom-left
        batch.setProjectionMatrix(uiCamera.combined);


        // --- Money at top-center ---
        String moneyText = "Money: $" + player.money;
        GlyphLayout layout = new GlyphLayout(font, moneyText);
        float moneyX = (Gdx.graphics.getWidth() - layout.width) / 2f;
        float moneyY = Gdx.graphics.getHeight() - 10; // 10px from top
        font.draw(batch, moneyText, moneyX, moneyY);

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


