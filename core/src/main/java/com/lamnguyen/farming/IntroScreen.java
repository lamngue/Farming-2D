package com.lamnguyen.farming;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;

public class IntroScreen implements Screen {

    private final Game game;

    private SpriteBatch batch;
    private BitmapFont titleFont;
    private BitmapFont buttonFont;

    private Rectangle playButton;
    private Texture whitePixel;

    public IntroScreen(Game game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();

        titleFont = new BitmapFont();
        titleFont.getData().setScale(2.5f);

        buttonFont = new BitmapFont();
        buttonFont.getData().setScale(1.5f);

        // Button dimensions
        int btnWidth = 220;
        int btnHeight = 60;

        playButton = new Rectangle(
            Gdx.graphics.getWidth() / 2f - btnWidth / 2f,
            Gdx.graphics.getHeight() / 2f - btnHeight / 2f,
            btnWidth,
            btnHeight
        );

        // White pixel texture
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        whitePixel = new Texture(pixmap);
        pixmap.dispose();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.1f, 0.6f, 0.1f, 1);

        handleInput();

        batch.begin();

        // ---- Title ----
        titleFont.draw(
            batch,
            "Lam Farming Game",
            Gdx.graphics.getWidth() / 2f - 160,
            Gdx.graphics.getHeight() - 100
        );

        // ---- Play Button ----
        batch.setColor(0.2f, 0.2f, 0.2f, 1);
        batch.draw(
            whitePixel,
            playButton.x,
            playButton.y,
            playButton.width,
            playButton.height
        );

        batch.setColor(Color.WHITE);
        buttonFont.draw(
            batch,
            "PLAY GAME",
            playButton.x + 40,
            playButton.y + 40
        );

        batch.end();
    }

    private void handleInput() {
        if (Gdx.input.justTouched()) {
            Vector3 touch = new Vector3(
                Gdx.input.getX(),
                Gdx.input.getY(),
                0
            );

            // Flip Y because screen coords are inverted
            touch.y = Gdx.graphics.getHeight() - touch.y;

            if (playButton.contains(touch.x, touch.y)) {
                game.setScreen(new GameScreen(game));
            }
        }
    }

    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        batch.dispose();
        titleFont.dispose();
        buttonFont.dispose();
        whitePixel.dispose();
    }
}
