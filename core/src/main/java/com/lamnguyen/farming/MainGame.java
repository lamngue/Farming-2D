package com.lamnguyen.farming;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.lamnguyen.farming.entities.Inventory;
import com.lamnguyen.farming.entities.Player;


/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class MainGame extends Game {

    public SpriteBatch batch;
    public Player player;
    public Inventory inventory;
    @Override
    public void create() {
        batch = new SpriteBatch();
        player = new Player();
        inventory = new Inventory();
        setScreen(new IntroScreen(this));
    }

    @Override
    public void dispose() {
        batch.dispose();
        super.dispose();
    }

}


