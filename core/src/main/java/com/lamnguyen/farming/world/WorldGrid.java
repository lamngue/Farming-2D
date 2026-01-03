package com.lamnguyen.farming.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.lamnguyen.farming.entities.Crop;
import com.lamnguyen.farming.entities.CropType;

public class WorldGrid {
    private Rectangle leftExit;
    private Rectangle rightExit;
    private Texture arrowLeft;
    private Texture arrowRight;
    public static final int TILE_SIZE = 32;
    private static final int DIRT_WIDTH = 5;
    private static final int DIRT_HEIGHT = 4;
    private int width;
    private int height;
    private Tile[][] tiles;
    private Crop[][] crops;
    private Rectangle shopBounds;

    private boolean hasDirtPatch = false;

    private boolean[][] watered;

    private Texture dirtTex;
    private Texture grassTex;
    private Texture shopTex;

    public WorldGrid(int width, int height) {
        this.width = width;
        this.height = height;

        tiles = new Tile[width][height];
        crops = new Crop[width][height];
        watered = new boolean[width][height];

        dirtTex = new Texture("texture/dirt_texture.png");
        grassTex = new Texture("texture/grass_texture.png");
        shopTex = new Texture("buildings/grocery_shop.png");
        grassTex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        shopTex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        // Default everything to grass
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                tiles[x][y] = new Tile(x, y, TileType.GRASS);
            }
        }
        arrowLeft  = new Texture("ui/arrow_left.png");
        arrowRight = new Texture("ui/arrow_right.png");
        createExitZones();
        initShopBounds();
    }

    private void initShopBounds() {
        float x = getShopX();
        float y = getShopY();

        shopBounds = new Rectangle(
            x,
            y,
            shopTex.getWidth(),
            shopTex.getHeight()
        );
    }

    private void createExitZones() {
        float tile = TILE_SIZE;

        // LEFT exit (for GREEN_FIELD → FARM)
        leftExit = new Rectangle(
            0,
            (height * tile) / 2f - tile,
            tile,
            tile * 2
        );

        // RIGHT exit (for FARM → GREEN_FIELD)
        rightExit = new Rectangle(
            width * tile - tile,
            (height * tile) / 2f - tile,
            tile,
            tile * 2
        );
    }

    public Rectangle getExitZone(WorldType type) {
        if (type == WorldType.FARM) {
            return rightExit;
        } else {
            return leftExit;
        }
    }

    public Rectangle getShopBounds() {
        return shopBounds;
    }

    public float getShopX() {
        float worldPixelWidth = width * TILE_SIZE;
        return (worldPixelWidth - shopTex.getWidth()) / 2f;
    }

    public float getShopY() {
        float worldPixelHeight = height * TILE_SIZE;
        return worldPixelHeight * 0.6f; // upper half
    }


    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }


    public void createDirtPatch(int startX, int startY, int patchWidth, int patchHeight) {
        hasDirtPatch = true;
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

    private boolean isInBounds(int x, int y) {
        return x >= 0 && y >= 0 && x < width && y < height;
    }

    // -------- DRAW --------

    public void renderFill(SpriteBatch batch) {

        // grass
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                batch.draw(
                    grassTex,
                    x * TILE_SIZE,
                    y * TILE_SIZE,
                    TILE_SIZE,
                    TILE_SIZE
                );
            }
        }
        if (!hasDirtPatch) return;

        // dirt
        int dirtStartX = (width - DIRT_WIDTH) / 2;
        int dirtStartY = (height - DIRT_HEIGHT) / 2;

        for (int x = dirtStartX; x < dirtStartX + DIRT_WIDTH; x++) {
            for (int y = dirtStartY; y < dirtStartY + DIRT_HEIGHT; y++) {

                if (!isInBounds(x, y)) continue;

                batch.setColor(watered[x][y] ?
                    new Color(0.6f, 0.45f, 0.3f, 1f) :
                    Color.WHITE
                );

                batch.draw(
                    dirtTex,
                    x * TILE_SIZE,
                    y * TILE_SIZE,
                    TILE_SIZE,
                    TILE_SIZE
                );
            }
        }

        batch.setColor(Color.WHITE);
    }



    public void renderGridLines(ShapeRenderer shape) {
        if (!hasDirtPatch) return;


        int dirtStartX = (width - DIRT_WIDTH) / 2;
        int dirtStartY = (height - DIRT_HEIGHT) / 2;

        for (int x = dirtStartX; x <= dirtStartX + DIRT_WIDTH; x++) {
            float px = x * TILE_SIZE;
            shape.line(px, dirtStartY * TILE_SIZE, px, (dirtStartY + DIRT_HEIGHT) * TILE_SIZE);
        }

        for (int y = dirtStartY; y <= dirtStartY + DIRT_HEIGHT; y++) {
            float py = y * TILE_SIZE;
            shape.line(dirtStartX * TILE_SIZE, py, (dirtStartX + DIRT_WIDTH) * TILE_SIZE, py);
        }
    }

    public void renderExitArrow(SpriteBatch batch, WorldType type) {
        Rectangle exit = getExitZone(type);

        Texture arrow = (type == WorldType.FARM)
            ? arrowRight
            : arrowLeft;

        batch.draw(
            arrow,
            exit.x,
            exit.y,
            exit.width,
            exit.height
        );
    }

    public void renderShop(SpriteBatch batch, WorldType worldType) {
        if (worldType != WorldType.GREEN_FIELD) return;

        batch.draw(
            shopTex,
            getShopX(),
            getShopY()
        );
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

    public void removeCrop(int x, int y) {
        if (!isInBounds(x, y)) return;

        crops[x][y] = null;
        watered[x][y] = false;
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

    public void fillWithGrassOnly() {
        hasDirtPatch = false;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                tiles[x][y].setType(TileType.GRASS);
                crops[x][y] = null;
                watered[x][y] = false;
            }
        }
    }

}
