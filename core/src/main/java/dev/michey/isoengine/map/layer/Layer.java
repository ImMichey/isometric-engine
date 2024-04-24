package dev.michey.isoengine.map.layer;

import dev.michey.isoengine.entity.Entity;
import dev.michey.isoengine.map.Map;
import dev.michey.isoengine.map.tile.Tile;
import dev.michey.isoengine.map.tile.TileUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class Layer {

    // The actual layer index
    public final int layerIndex;
    private final Map map;

    // List containing all tiles + entities of this current layer
    private final ArrayList<Tile> tiles;
    private final ArrayList<Entity> entities;

    // Lookup map for tiles
    private final HashMap<Long, Tile> tileMap;

    public Layer(Map map, int layer) {
        this.map = map;
        this.layerIndex = layer;

        this.tiles = new ArrayList<>();
        this.entities = new ArrayList<>();
        this.tileMap = new HashMap<>();
    }

    public void addTile(Tile tile) {
        tiles.add(tile);
        tileMap.put(tile.tileKey(), tile);
    }

    public void removeTile(Tile tile) {
        tiles.remove(tile);
        tileMap.remove(tile.tileKey());
    }

    public void addEntity(Entity entity) {
        entities.add(entity);
        entity.layer = this;
    }

    public void removeEntity(Entity entity) {
        entities.remove(entity);
        entity.layer = null;
    }

    public ArrayList<Entity> getEntities() {
        return entities;
    }

    public Tile getTile(int tileX, int tileY) {
        long key = TileUtils.toTileKey(tileX, tileY);
        return tileMap.get(key);
    }

    public ArrayList<Tile> getTiles() {
        return tiles;
    }

}
