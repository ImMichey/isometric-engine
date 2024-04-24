package dev.michey.isoengine.entity;

import com.badlogic.gdx.math.MathUtils;
import dev.michey.isoengine.IsoGame;
import dev.michey.isoengine.map.RenderedInstance;
import dev.michey.isoengine.map.layer.Layer;
import dev.michey.isoengine.map.tile.Tile;
import dev.michey.isoengine.render.IsoRender;

public abstract class Entity extends RenderedInstance {

    /** The unique entity id. */
    public long id;

    public abstract void tick(IsoRender r);

    public void moveByTileUnits(float tx, float ty) {
        var map = IsoGame.engine.map;

        int oldTileX = (int) tileX;
        int oldTileY = (int) tileY;
        int oldLayer = layer.layerIndex;
        tileX += tx;
        tileY += ty;

        // Clamp to map bounds
        tileX = MathUtils.clamp(tileX, 0f, map.mapDimensionX - 0.001f);
        tileY = MathUtils.clamp(tileY, 0f, map.mapDimensionY - 0.001f);
        int newTileX = (int) tileX;
        int newTileY = (int) tileY;

        if(oldTileX != tileX || oldTileY != tileY) {
            // Switched tiles, compare layers.
            Tile newTile = map.getHighestTile(newTileX, newTileY);

            if((newTile.getLayer().layerIndex + 1) != oldLayer) {
                // Switched layers.
                layer.removeEntity(this);

                Layer addToLayer = map.ensureLayerExistence(newTile.getLayer().layerIndex + 1);
                addToLayer.addEntity(this);
            }
        }

        updateWorldPosition();
    }

    @Override
    public void updateWorldPosition() {
        worldX = (tileX - tileY) * 32 / 2f;
        worldY = (tileY + tileX) * 16 / 2f + 8 * layer.layerIndex;
    }

    @Override
    public String toString() {
        return "ENTITY ID: " + id  + " L: " + layer.layerIndex + " TX/TY: " + tileX + "," + tileY + " WX/WY: " + worldX + "," + worldY;
    }

}
