package com.lamnguyen.farming.save;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.lamnguyen.farming.GameScreen;
import com.lamnguyen.farming.entities.Crop;
import com.lamnguyen.farming.entities.ItemType;
import com.lamnguyen.farming.world.WorldGrid;

public class SaveManager {

    private static final String SAVE_FILE = "savegame.json";

    // --------------------
    // SAVE
    // --------------------
    public static void save(GameScreen game) {

        SaveData data = new SaveData();

        // Player
        data.player = new PlayerData();
        data.player.x = game.getPlayer().x;
        data.player.y = game.getPlayer().y;
        data.player.direction = game.getPlayer().direction;

        // Inventory
        data.inventory = new InventoryData();
        for (ItemType item : ItemType.values()) {
            int amount = game.getPlayer().inventory.get(item);
            if (amount > 0) {
                data.inventory.items.put(item.name(), amount);
            }
        }

        // World / crops
        data.world = new WorldData();
        WorldGrid world = game.getWorld();

        for (int x = 0; x < world.getWidth(); x++) {
            for (int y = 0; y < world.getHeight(); y++) {
                Crop c = world.getCrop(x, y);
                if (c != null) {
                    CropData cd = new CropData();
                    cd.x = x;
                    cd.y = y;
                    cd.type = c.type.name();
                    cd.growthStage = c.getGrowthStage();
                    cd.watered = c.getIsWatered();
                    cd.growTimer = c.getGrowTimer();
                    cd.fertilizer = c.getFertilizerLevel();
                    data.world.crops.add(cd);
                }
            }
        }

        Json json = new Json();
        FileHandle file = Gdx.files.local(SAVE_FILE);
        file.writeString(json.prettyPrint(data), false);

        Gdx.app.log("SAVE", "Game saved");
    }

    // --------------------
    // LOAD
    // --------------------
    public static SaveData load() {

        FileHandle file = Gdx.files.local(SAVE_FILE);
        if (!file.exists()) return null;

        Json json = new Json();
        return json.fromJson(SaveData.class, file);
    }

    // --------------------
    // EXISTS?
    // --------------------
    public static boolean hasSave() {
        return Gdx.files.local(SAVE_FILE).exists();
    }
}

