package com.lamnguyen.farming.world;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.lamnguyen.farming.entities.Crop;
import com.lamnguyen.farming.entities.CropType;

public class WorldGrid {

    public static final int TILE_SIZE = 32;

    private int width;
    private int height;
    private Tile[][] tiles;
    private Crop[][] crops;

    private boolean[][] watered;

    public WorldGrid(int width, int height) {
        this.width = width;
        this.height = height;

        tiles = new Tile[width][height];
        crops = new Crop[width][height];
        watered = new boolean[width][height];

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

    public void renderFill(ShapeRenderer shape) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {

                Tile tile = tiles[x][y];

                if (tile.getType() == TileType.GRASS)
                    shape.setColor(0, 0.6f, 0, 1);
                else if (tile.getType() == TileType.DIRT)
                    shape.setColor(0.6f, 0.4f, 0.2f, 1);

                shape.rect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }
    }




    // inside WorldGrid.java
    /**
     * Draws only the filled dirt tiles and any crops (no grid lines).
     * Assumes ShapeRenderer is already in ShapeType.Filled.
     */
    public void renderGrid(ShapeRenderer shape, SpriteBatch batch) {

        int dirtStartX = (width / 2 - 2);
        int dirtStartY = (height / 2 - 2);

        // --- 1) DRAW FILLED TILES WITH SHAPERENDERER ---
        for (int x = dirtStartX; x < dirtStartX + 5; x++) {
            for (int y = dirtStartY; y < dirtStartY + 4; y++) {

                if (!isInBounds(x, y)) continue;

                // Watered dirt = darker
                if (watered[x][y]) {
                    shape.setColor(0.35f, 0.25f, 0.15f, 1);
                } else {
                    shape.setColor(0.6f, 0.4f, 0.2f, 1);
                }

                shape.rect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }

        // --- 2) DRAW CROPS USING SPRITEBATCH ---
        batch.begin();

        for (int x = dirtStartX; x < dirtStartX + 5; x++) {
            for (int y = dirtStartY; y < dirtStartY + 4; y++) {

                if (!isInBounds(x, y)) continue;

                Crop crop = crops[x][y];
                if (crop != null) {
                    crop.render(batch);   // <--- uses image instead of shapes
                }
            }
        }

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

    public void plantCrop(int x, int y) {
        if (!isDirt(x, y)) return;
        if (crops[x][y] != null) return; // already planted

        crops[x][y] = new Crop(CropType.WHEAT, x, y);

        if (watered[x][y]) {
            crops[x][y].water();
        }
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
            crops[x][y] = null;
            watered[x][y] = false;
            crops[x][y].reset();
        }
    }

    public void update(float delta) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (crops[x][y] != null) {
                    crops[x][y].update(delta);
                }
            }
        }
    }


}
