package dev.michey.isoengine.map.tile;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import dev.michey.isoengine.IsoGame;

public enum TileType {

    DIRT("tile_009"), WATER("tile_022"), STONE("tile_026"), IRON("tile_104"),
    ;

    public final String path;

    TileType(String path) {
        this.path = path;
    }

    public TextureRegion toTexture() {
        return IsoGame.engine.assets.getTextureRegion(path);
    }

}
