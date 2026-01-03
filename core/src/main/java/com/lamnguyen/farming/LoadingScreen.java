package com.lamnguyen.farming;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.ScreenUtils;
import com.lamnguyen.farming.world.WorldType;

public class LoadingScreen implements Screen {

    private final MainGame game;
    private final WorldType nextWorld;
    private float timer;

    public LoadingScreen(MainGame game, WorldType nextWorld) {
        this.game = game;
        this.nextWorld = nextWorld;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        timer += delta;

        ScreenUtils.clear(0, 0, 0, 1);

        if (timer > 1.0f) { // fake load delay
            game.setScreen(new GameScreen(game, false, nextWorld));
        }
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}

