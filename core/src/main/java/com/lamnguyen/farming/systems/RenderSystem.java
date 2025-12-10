package com.lamnguyen.farming.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.lamnguyen.farming.entities.Inventory;
import com.lamnguyen.farming.entities.CropType;
import com.lamnguyen.farming.entities.ItemType;
import com.lamnguyen.farming.entities.Player;
import com.lamnguyen.farming.world.WorldGrid;

import java.util.Map;

public class RenderSystem {

    public void renderWorld(ShapeRenderer shape, SpriteBatch batch, WorldGrid world) {
        // Draw grass + dirt
        shape.begin(ShapeRenderer.ShapeType.Filled);
        world.renderFill(batch);
        shape.end();

        // Draw crops
        batch.begin();
        world.renderCrops(batch);
        batch.end();

        // Draw grid lines
        shape.begin(ShapeRenderer.ShapeType.Line);
        shape.setColor(0, 0, 0, 1);
        world.renderGridLines(shape);
        shape.end();
    }

    public void renderPlayer(SpriteBatch batch, Player player) {
        batch.begin();
        batch.draw(player.getSprite(), player.x, player.y);
        batch.end();
    }

    public void renderUI(SpriteBatch batch, Texture whitePixelTexture, Inventory inventory, BitmapFont font) {
        batch.begin();
        renderInventory(whitePixelTexture, batch, inventory, font);
        batch.end();
    }

    // Your existing inventory rendering method
    public void renderInventory(Texture whitePixelTexture, SpriteBatch batch, Inventory inventory, BitmapFont font) {
        int boxSize = 48;
        int padding = 8;
        int x = 10;
        int y = Gdx.graphics.getHeight() - boxSize - 10;

        for (Map.Entry<ItemType, Integer> entry : inventory.getAll().entrySet()) {
            ItemType item = entry.getKey();
            int amount = entry.getValue();

            // Draw box background
            batch.setColor(0.15f, 0.15f, 0.15f, 1);
            batch.draw(whitePixelTexture, x, y, boxSize, boxSize);
            batch.setColor(Color.WHITE);

            // Draw item icon
            if (item != null && item.icon != null) {
                batch.draw(item.icon, x + 4, y + 4, boxSize - 8, boxSize - 8);
            }

            // Draw quantity
            font.draw(batch, String.valueOf(amount), x + boxSize - 12, y + 14);

            // Move down for next box
            y -= (boxSize + padding);
        }
    }
}


