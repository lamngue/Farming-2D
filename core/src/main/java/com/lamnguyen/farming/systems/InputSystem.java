package com.lamnguyen.farming.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.lamnguyen.farming.entities.Player;

public class InputSystem {

    public void update(Player player) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.W)) player.y++;
        if (Gdx.input.isKeyJustPressed(Input.Keys.S)) player.y--;
        if (Gdx.input.isKeyJustPressed(Input.Keys.A)) player.x--;
        if (Gdx.input.isKeyJustPressed(Input.Keys.D)) player.x++;
    }
}
