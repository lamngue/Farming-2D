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

public class RenderSystem {

    public void renderWorld(ShapeRenderer shape, SpriteBatch batch, WorldGrid world, OrthographicCamera camera) {

        // --- Filled tiles ---
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        world.renderFill(batch);
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


    private void renderSeedHotbar(Texture whitePixelTexture, SpriteBatch batch, Inventory inventory, BitmapFont font, ItemType selectedSeed) {
        int boxSize = 48;
        int padding = 8;
        int x = 10; // left padding
        int y = Gdx.graphics.getHeight() - boxSize - 10; // top-left corner

        for (ItemType item : ItemType.values()) {
            if (!item.isSeed()) continue;
            int amount = inventory.get(item);
            if (amount <= 0) continue;

            // selection highlight
            if (item == selectedSeed) {
                batch.setColor(0.9f,0.9f,0.3f,1f);
            } else {
                batch.setColor(0.15f,0.15f,0.15f,1f);
            }
            batch.draw(whitePixelTexture, x, y, boxSize, boxSize);
            batch.setColor(Color.WHITE);

            batch.draw(item.icon, x + 4, y + 4, boxSize - 8, boxSize - 8);
            font.draw(batch, String.valueOf(amount), x + boxSize - 12, y + 14);

            x += boxSize + padding;
        }
    }

    private void renderCropStorage(Texture whitePixelTexture, SpriteBatch batch, Inventory inventory, BitmapFont font) {
        int boxSize = 48;
        int padding = 6;

        int x = Gdx.graphics.getWidth() - boxSize - 10; // right-aligned
        int y = Gdx.graphics.getHeight() - boxSize - 10; // top-right

        for (ItemType item : ItemType.values()) {
            if (item.isSeed()) continue;
            int amount = inventory.get(item);
            if (amount <= 0) continue;

            batch.setColor(0.15f,0.15f,0.15f,1f);
            batch.draw(whitePixelTexture, x, y, boxSize, boxSize);
            batch.setColor(Color.WHITE);

            batch.draw(item.icon, x + 4, y + 4, boxSize - 8, boxSize - 8);
            font.draw(batch, String.valueOf(amount), x + boxSize - 12, y + 14);

            y -= boxSize + padding; // stack downwards
        }
    }


}


