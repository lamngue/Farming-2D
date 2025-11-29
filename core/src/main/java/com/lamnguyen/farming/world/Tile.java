package com.lamnguyen.farming.world;

public class Tile {

    public int x;
    public int y;


    public TileType type;

    public Tile(int x, int y, TileType type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }
    public TileType getType() {
        return type;
    }

    public void setType(TileType type) {
        this.type = type;
    }
}
