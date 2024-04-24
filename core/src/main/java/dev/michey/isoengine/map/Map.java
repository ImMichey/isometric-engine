package dev.michey.isoengine.map;

import dev.michey.isoengine.IsoGame;
import dev.michey.isoengine.entity.Entity;
import dev.michey.isoengine.map.entity.EntityManager;
import dev.michey.isoengine.map.layer.Layer;
import dev.michey.isoengine.map.tile.Tile;

import java.util.*;

public class Map {

    public String mapVersion;
    public float tileSizeX, tileSizeY;
    public int mapDimensionX, mapDimensionY;

    private final ArrayList<RenderedInstance> baseTileList;
    private final ArrayList<RenderedInstance> dynamicInstanceList;
    // Unordered list of all layers + entities of this map
    private final ArrayList<Layer> layers;
    private final ArrayList<Entity> entities;

    private final EntityManager entityManager;
    private final Comparator<RenderedInstance> dynamicSorter;

    public Map(String mapVersion, float tileSizeX, float tileSizeY, int mapDimensionX, int mapDimensionY) {
        this.mapVersion = mapVersion;
        this.tileSizeX = tileSizeX;
        this.tileSizeY = tileSizeY;
        this.mapDimensionX = mapDimensionX;
        this.mapDimensionY = mapDimensionY;

        baseTileList = new ArrayList<>();
        dynamicInstanceList = new ArrayList<>();

        layers = new ArrayList<>();
        entities = new ArrayList<>();

        entityManager = new EntityManager();

        // return 1 = in front of
        // return -1 = behind of
        dynamicSorter = (o1, o2) -> {
            int tx1 = (int) o1.tileX;
            int ty1 = (int) o1.tileY;
            int l1 = o1.layer.layerIndex;

            int tx2 = (int) o2.tileX;
            int ty2 = (int) o2.tileY;
            int l2 = o2.layer.layerIndex;

            if (tx1 == tx2 && ty1 == ty2) {
                // Comparing tiles mostly, same tile context.
                if(l1 != l2) {
                    return Integer.compare(l1, l2);
                }

                if(o1.worldY < o2.worldY) {
                    return 1;
                } else if(o1.worldY > o2.worldY) {
                    return -1;
                }

                return 0;
            } else {
                // Comparing tiles + entities.
                if(tx1 < tx2) {
                    return 1;
                } else if(tx1 == tx2) {
                    if(ty1 < ty2) {
                        return 1;
                    } else {
                        return -1;
                    }
                }

                return -1;
            }
        };
    }

    public void addTileToMap(int layerIndex, Tile tile) {
        tile.updateWorldPosition();
        layers.get(layerIndex).addTile(tile);
        dynamicInstanceList.add(tile);
    }

    public void removeTileFromMap(Tile tile) {
        layers.get(tile.getLayer().layerIndex).removeTile(tile);
        dynamicInstanceList.remove(tile);
    }

    public void addEntityToMap(Entity entity) {
        Tile highestTile = getHighestTile((int) entity.tileX, (int) entity.tileY);
        highestTile.getLayer().addEntity(entity);

        entity.id = entityManager.generateEntityId();

        entity.updateWorldPosition();
        entities.add(entity);
        dynamicInstanceList.add(entity);
    }

    public void removeEntityFromMap(Entity entity) {
        entity.layer.removeEntity(entity);
        entities.remove(entity);
        dynamicInstanceList.remove(entity);
    }

    public void deleteAllEntities() {
        for(Layer layer : layers) {
            layer.getEntities().clear();
        }

        entities.clear();
    }

    public ArrayList<Entity> getAllEntities() {
        return entities;
    }

    public Tile getHighestTile(int tileX, int tileY) {
        for(int i = (layers.size() - 1); i >= 0; i--) {
            var layer = layers.get(i);
            Tile tile = layer.getTile(tileX, tileY);
            if(tile != null) return tile;
        }

        return null;
    }

    public void render() {
        for(Entity e : entities) {
            e.tick(IsoGame.engine.render);
        }

        for(int i = baseTileList.size() - 1; i >= 0; i--) {
            RenderedInstance ri = baseTileList.get(i);
            if(!ri.visible) continue;
            ri.render(IsoGame.engine.render);
        }

        dynamicInstanceList.sort(dynamicSorter);

        for(RenderedInstance ri : dynamicInstanceList) {
            if(!ri.visible) continue;
            ri.render(IsoGame.engine.render);
        }
    }

    public ArrayList<RenderedInstance> getBaseTileList() {
        return baseTileList;
    }

    public ArrayList<RenderedInstance> getDynamicInstanceList() {
        return dynamicInstanceList;
    }

    public Layer ensureLayerExistence(int layer) {
        int maxLayer = layers.size() - 1;

        if(layer > maxLayer) {
            int createLayers = layer - maxLayer;

            for(int i = 0; i < createLayers; i++) {
                layers.add(new Layer(this, maxLayer + i + 1));
            }
        }

        return layers.get(layer);
    }

    public ArrayList<Layer> getLayers() {
        return layers;
    }

}
