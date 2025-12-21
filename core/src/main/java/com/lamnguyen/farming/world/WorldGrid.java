package com.lamnguyen.farming.world;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.lamnguyen.farming.entities.Crop;
import com.lamnguyen.farming.entities.CropType;

public class WorldGrid {

    public static final int TILE_SIZE = 32;

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    private int width;
    private int height;
    private Tile[][] tiles;
    private Crop[][] crops;


    private boolean[][] watered;

    private Texture grassTex;
    private Texture dirtTex;

    public WorldGrid(int width, int height) {
        this.width = width;
        this.height = height;

        tiles = new Tile[width][height];
        crops = new Crop[width][height];
        watered = new boolean[width][height];

//        grassTex = new Texture("grass_texture.png");
        dirtTex = new Texture("dirt_texture.png");
        // Default everything to grass
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                tiles[x][y] = new Tile(x, y, TileType.GRASS);
            }
        }
    }

    public void createDirtPatch(int startX, int startY, int patchWidth, int patchHeight) {
        for (int x = 0; x < patchWidth; x++) {
            for (int y = 0; y < patchHeight; y++) {
                int gridX = startX + x;
                int gridY = startY + y;

                if (isInBounds(gridX, gridY)) {
                    tiles[gridX][gridY].setType(TileType.DIRT);
                }
            }
        }
    }

    public void renderCrops(SpriteBatch batch) {

        int dirtStartX = (width / 2 - 2);
        int dirtStartY = (height / 2 - 2);

        for (int x = dirtStartX; x < dirtStartX + 5; x++) {
            for (int y = dirtStartY; y < dirtStartY + 4; y++) {

                Crop crop = crops[x][y];
                if (crop != null) {
                    crop.render(batch);
                }
            }
        }
    }


    public Tile getTile(int x, int y) {
        if (!isInBounds(x, y))
            return null;
        return tiles[x][y];
    }

    private boolean isInBounds(int x, int y) {
        return x >= 0 && y >= 0 && x < width && y < height;
    }

    // -------- DRAW --------

    public void renderFill(SpriteBatch batch) {

        int dirtStartX = (width / 2 - 2);
        int dirtStartY = (height / 2 - 2);

        batch.begin();
//
//        // --- Draw grass everywhere ---
//        for (int x = 0; x < width; x++) {
//            for (int y = 0; y < height; y++) {
//                batch.draw(grassTex, x * TILE_SIZE, y * TILE_SIZE);
//            }
//        }

        // --- Draw dirt patch ---
        for (int x = dirtStartX; x < dirtStartX + 5; x++) {
            for (int y = dirtStartY; y < dirtStartY + 4; y++) {

                if (!isInBounds(x, y)) continue;

                if (watered[x][y]) {
                    // tint darker
                    batch.setColor(0.6f, 0.45f, 0.3f, 1f);
                } else {
                    batch.setColor(1f, 1f, 1f, 1f);
                }

                batch.draw(dirtTex, x * TILE_SIZE, y * TILE_SIZE);
            }
        }

        batch.setColor(1f, 1f, 1f, 1f); // reset tint
        batch.end();
    }


    /**
     * Draws only the grid lines around the dirt patch.
     * Assumes ShapeRenderer is already in ShapeType.Line.
     */
    public void renderGridLines(ShapeRenderer shape) {
        int dirtStartX = (width / 2 - 2);
        int dirtStartY = (height / 2 - 2);

        // draw vertical lines for the dirt patch
        for (int x = dirtStartX; x <= dirtStartX + 5; x++) {
            float sx = x * TILE_SIZE;
            shape.line(sx, dirtStartY * TILE_SIZE, sx, (dirtStartY + 4) * TILE_SIZE);
        }

        // draw horizontal lines for the dirt patch
        for (int y = dirtStartY; y <= dirtStartY + 4; y++) {
            float sy = y * TILE_SIZE;
            shape.line(dirtStartX * TILE_SIZE, sy, (dirtStartX + 5) * TILE_SIZE, sy);
        }
    }



    public boolean isDirt(int x, int y) {
        int startX = (width / 2 - 2);
        int startY = (height / 2 - 2);

        return x >= startX && x < startX + 5 &&
            y >= startY && y < startY + 4;
    }

    public boolean plantCrop(int x, int y, CropType type) {
        if (!isDirt(x, y)) return false;
        if (crops[x][y] != null) return false;

        crops[x][y] = new Crop(type, x, y);

        if (watered[x][y]) crops[x][y].water();

        return true;
    }


    public Crop getCrop(int tileX, int tileY) {
        return crops[tileX][tileY];
    }

    public void water(int x, int y) {
        if (isDirt(x, y)) {
            watered[x][y] = true;
            if (crops[x][y] != null) {
               crops[x][y].water();
            }
        }
    }

    public void harvest(int x, int y) {
        if (crops[x][y] != null && crops[x][y].isFullyGrown()) {
            System.out.println("Sold for $" + crops[x][y].type.sellPrice);
            watered[x][y] = false;
            crops[x][y] = null;
        }
    }

    public void update(float delta) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Crop c = crops[x][y];
                if (c != null) {
                    boolean wasWatered = c.getIsWatered();
                    c.update(delta);
                    if (wasWatered && !c.getIsWatered()) {
                        watered[x][y] = false;  // soil dries after crop uses water
                    }
                }
            }
        }

    }


    public void setCrop(int x, int y, Crop crop) {
        if (x < 0 || y < 0 || x >= width || y >= height) return;
        crops[x][y] = crop;
    }

}
