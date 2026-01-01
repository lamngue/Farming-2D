package com.lamnguyen.farming.utils;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.lamnguyen.farming.world.WorldGrid;

public class CameraUtils {

    public static void smoothFollow(
        OrthographicCamera camera,
        float targetX,
        float targetY,
        float smoothing
    ) {
        camera.position.x += (targetX - camera.position.x) * smoothing;
        camera.position.y += (targetY - camera.position.y) * smoothing;
    }

    public static void clampCamera(
        OrthographicCamera camera,
        int worldWidthTiles,
        int worldHeightTiles
    ) {
        float worldWidth  = worldWidthTiles  * WorldGrid.TILE_SIZE;
        float worldHeight = worldHeightTiles * WorldGrid.TILE_SIZE;

        float halfViewW = (camera.viewportWidth  * camera.zoom) / 2f;
        float halfViewH = (camera.viewportHeight * camera.zoom) / 2f;

        camera.position.x = MathUtils.clamp(
            camera.position.x,
            halfViewW,
            worldWidth - halfViewW
        );

        camera.position.y = MathUtils.clamp(
            camera.position.y,
            halfViewH,
            worldHeight - halfViewH
        );

        camera.update();
    }

}
