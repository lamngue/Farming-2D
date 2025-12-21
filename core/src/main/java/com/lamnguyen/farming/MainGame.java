package com.lamnguyen.farming;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;


/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class MainGame extends Game {

    public SpriteBatch batch;

    @Override
    public void create() {
        setScreen(new IntroScreen(this));
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}


