package dev.michey.isoengine.map.tile;

import com.badlogic.gdx.math.MathUtils;
import dev.michey.isoengine.IsoGame;
import dev.michey.isoengine.map.layer.Layer;

public class TileUtils {

    /** Returns the highest tile at the desired layer. */
    public static Tile getCurrentTile() {
        var map = IsoGame.engine.map;
        var layers = map.getLayers();
        int mgx = (int) IsoGame.engine.render.mouseGridX;
        int mgy = (int) IsoGame.engine.render.mouseGridY;

        for(int i = (layers.size() - 1); i >= 0; i--) {
            var layer = layers.get(i);
            Tile tile = layer.getTile(mgx, mgy);
            if(tile != null) return tile;
        }

        // Should never return null as long as there is a map loaded with at least one layer.
        return null;
    }

    /** Sets the tile type at the desired layer at the position. */
    public static void setTileAtLayer(int layer, int tileX, int tileY, TileType type) {
        var map = IsoGame.engine.map;
        Layer l = map.ensureLayerExistence(layer); // Make sure the layer exists in the map
        Tile t = l.getTile(tileX, tileY);

        if(t == null) {
            if(type != null) {
                t = new Tile(l, type, tileX, tileY, tileX + tileY * map.mapDimensionX);
                map.addTileToMap(layer, t);
                IsoGame.engine.render.updateVisibilitySingle(t);
            }
        } else {
            if(type == null) {
                map.removeTileFromMap(t);
            } else {
                t.tileType = type;
                t.updateTexture();
            }
        }
    }

    /** Converts the tileX and tileY position to a unique identifier key. */
    public static long toTileKey(int tileX, int tileY) {
        long tmp = (tileY + ((tileX + 1) / 2));
        return tileX + (tmp * tmp);
    }

    /** Returns a random tile type. */
    public static TileType randomTileType() {
        return TileType.values()[MathUtils.random(TileType.values().length - 1)];
    }

}
