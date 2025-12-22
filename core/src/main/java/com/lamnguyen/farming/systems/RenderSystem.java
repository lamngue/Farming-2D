package com.lamnguyen.farming.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
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
        batch.begin();
        renderSeedHotbar(
            whitePixelTexture,
            batch,
            player.inventory,
            font,
            player.selectedSeed   // highlight currently active seed
        );
        renderCropStorage(
            whitePixelTexture,
            batch,
            player.inventory,
            font
        );

        batch.end();
    }

    public void renderSeedHotbar(
        Texture whitePixelTexture,
        SpriteBatch batch,
        Inventory inventory,
        BitmapFont font,
        ItemType selectedSeed
    ) {
        int boxSize = 48;
        int padding = 8;
        int x = 10;
        int y = Gdx.graphics.getHeight() - boxSize - 10;

        for (ItemType item : ItemType.values()) {

            if (!item.isSeed()) continue;
            int amount = inventory.get(item);
            if (amount <= 0) continue;

            // selection highlight
            if (item == selectedSeed)
                batch.setColor(0.9f, 0.9f, 0.3f, 1);
            else
                batch.setColor(0.15f, 0.15f, 0.15f, 1);

            batch.draw(whitePixelTexture, x, y, boxSize, boxSize);
            batch.setColor(Color.WHITE);

            batch.draw(item.icon, x + 4, y + 4, boxSize - 8, boxSize - 8);

            font.draw(batch, String.valueOf(amount),
                x + boxSize - 12, y + 14);

            x += boxSize + padding;
        }
    }

    public void renderCropStorage(
        Texture whitePixelTexture,
        SpriteBatch batch,
        Inventory inventory,
        BitmapFont font
    ) {
        int boxSize = 48;
        int padding = 6;

        int x = Gdx.graphics.getWidth() - boxSize - 10;
        int y = Gdx.graphics.getHeight() - boxSize - 10;

        for (ItemType item : ItemType.values()) {

            if (item.isSeed()) continue;            // NOT seeds
            int amount = inventory.get(item);
            if (amount <= 0) continue;              // only owned items

            batch.setColor(0.15f, 0.15f, 0.15f, 1);
            batch.draw(whitePixelTexture, x, y, boxSize, boxSize);
            batch.setColor(Color.WHITE);

            batch.draw(item.icon, x + 4, y + 4, boxSize - 8, boxSize - 8);

            font.draw(batch, String.valueOf(amount),
                x + boxSize - 12, y + 14);

            y -= boxSize + padding;
        }
    }

}


