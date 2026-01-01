package com.lamnguyen.farming;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.lamnguyen.farming.world.WorldType;

public class IntroScreen implements Screen {
    private final MainGame game;
    private Stage stage;
    private Skin skin;

    public IntroScreen(MainGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        Label title = new Label("Lam Farming Game", skin);
        title.setFontScale(2f);

        TextButton playBtn = new TextButton("New Game", skin);
        TextButton loadBtn = new TextButton("Load Game", skin);
        TextButton exitBtn = new TextButton("Exit", skin);

        table.add(title).padBottom(40).row();
        table.add(playBtn).width(200).pad(10).row();
        table.add(loadBtn).width(200).pad(10).row();
        table.add(exitBtn).width(200).pad(10);

        playBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new GameScreen(game, false, WorldType.FARM));
            }
        });

        loadBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new GameScreen(game, true, WorldType.FARM));
            }
        });

        exitBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0.1f, 1);
        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int w, int h) { stage.getViewport().update(w, h, true); }
    @Override public void hide() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void dispose() { stage.dispose(); skin.dispose(); }
}
