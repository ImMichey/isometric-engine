package dev.michey.isoengine.map.tile;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import dev.michey.isoengine.IsoGame;
import dev.michey.isoengine.entity.Entity;
import dev.michey.isoengine.map.RenderedInstance;
import dev.michey.isoengine.map.layer.Layer;
import dev.michey.isoengine.render.IsoRender;

import java.util.ArrayList;
import java.util.Comparator;

public class Tile extends RenderedInstance {

    public TileType tileType;
    public int tileId;
    private final long tileKey;

    public TextureRegion tileTexture;
    public Color color = new Color(Color.WHITE);

    public Tile(Layer layer, TileType tileType, int tileX, int tileY, int tileId) {
        this.layer = layer;
        this.tileType = tileType;
        this.tileX = tileX;
        this.tileY = tileY;
        this.tileId = tileId;
        this.tileKey = TileUtils.toTileKey(tileX, tileY);
        updateTexture();
    }

    @Override
    public void render(IsoRender r) {
        r.batch.setColor(color);
        r.batch.draw(tileTexture, worldX, worldY);
    }

    @Override
    public void updateWorldPosition() {
        worldX = (tileX - tileY) * 32 / 2f;
        worldY = (tileY + tileX) * 16 / 2f + 8 * layer.layerIndex;
    }

    public void updateTexture() {
        tileTexture = tileType.toTexture();
    }

    public long tileKey() {
        return tileKey;
    }

    @Override
    public String toString() {
        return "TILE ID: " + tileKey + " L: " + layer.layerIndex + " TX/TY: " + tileX + "," + tileY + " WX/WY: " + worldX + "," + worldY;
    }

    public Layer getLayer() {
        return layer;
    }

}
