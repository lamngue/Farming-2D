package com.lamnguyen.farming.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.lamnguyen.farming.entities.Inventory;
import com.lamnguyen.farming.entities.ItemType;
import com.lamnguyen.farming.entities.Player;
import com.lamnguyen.farming.world.WorldGrid;
import com.lamnguyen.farming.world.WorldType;

public class RenderSystem {

    private static final int SEED_BOX_SIZE = 64;
    private static final int STORAGE_BOX_SIZE = 64;
    private static final int BOX_PADDING = 10;

    public void renderWorld(ShapeRenderer shape, SpriteBatch batch, WorldGrid world, OrthographicCamera camera,  WorldType worldType) {

        // --- Filled tiles ---
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        world.renderFill(batch);
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

        batch.begin();

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

        batch.end();
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

        int x = 16;
        int y = Gdx.graphics.getHeight() - boxSize - 16;

        for (ItemType item : ItemType.values()) {
            if (!item.isSeed()) continue;

            int amount = inventory.get(item);
            if (amount <= 0) continue;

            batch.setColor(item == selectedSeed
                ? new Color(0.9f, 0.9f, 0.3f, 1)
                : new Color(0.15f, 0.15f, 0.15f, 1));

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

            x += boxSize + padding;
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


