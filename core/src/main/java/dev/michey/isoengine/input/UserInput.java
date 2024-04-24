package dev.michey.isoengine.input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import dev.michey.isoengine.IsoGame;
import dev.michey.isoengine.map.tile.Tile;
import dev.michey.isoengine.map.tile.TileType;
import dev.michey.isoengine.map.tile.TileUtils;
import dev.michey.isoengine.render.IsoRender;

public class UserInput extends InputAdapter {

    @Override
    public boolean scrolled(float v, float amount) {
        IsoGame.engine.render.handleZoom(amount);
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        IsoRender r = IsoGame.engine.render;
        int x = (int) r.mouseGridX;
        int y = (int) r.mouseGridY;

        Tile tile = TileUtils.getCurrentTile();

        if(tile != null) {
            if(button == 0) {
                //TileUtils.setTileAtLayer(tile.layer.layer + 1, x, y, TileUtils.randomTileType());
                TileUtils.setTileAtLayer(tile.getLayer().layerIndex + 1, x, y, TileType.DIRT);
            } else if(button == 1) {
                if(tile.getLayer().layerIndex > 0) {
                    TileUtils.setTileAtLayer(tile.getLayer().layerIndex, x, y, null);
                }
            }
        }

        return false;
    }

    @Override
    public boolean keyDown(int keycode) {
        /*
        if(keycode == Input.Keys.Y) {
            IsoGame.engine.render.camera.position.set(0, 0, 0);
            IsoGame.engine.render.camera.update();
        }

        if(keycode == Input.Keys.F3) {
            for(Entity e : IsoGame.engine.map.getAllEntities()) {
                e.moveByTileUnits(0.1f + MathUtils.random(0.2f), 0.1f + MathUtils.random(0.2f));
            }
        }

        if(keycode == Input.Keys.F4) {
            int spawn = 1;// MathUtils.random(3, 5);

            for(int i = 0; i < spawn; i++) {
                EntityWalkingDummy dummy = new EntityWalkingDummy();
                var map = IsoGame.engine.map;

                map.spawnEntity(MathUtils.random(0, map.mapDimensionX - 1f), MathUtils.random(0, map.mapDimensionY - 1f), dummy);
            }
        }

        if(keycode == Input.Keys.F5) {
            IsoGame.engine.map.deleteAllEntities();
        }

        if(keycode == Input.Keys.F6) {
            for(Entity e : IsoGame.engine.map.getAllEntities()) {
                System.out.println(e.toString());
            }
        }
        */

        return false;
    }

}
