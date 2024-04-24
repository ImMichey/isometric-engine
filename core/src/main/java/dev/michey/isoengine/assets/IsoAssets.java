package dev.michey.isoengine.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.HashMap;

public class IsoAssets {

    public AssetManager assetManager;
    public TextureAtlas isoTiles0Atlas;
    private final HashMap<String, TextureAtlas.AtlasRegion> atlasMap;

    public IsoAssets() {
        assetManager = new AssetManager();
        atlasMap = new HashMap<>();

        isoTiles0Atlas = new TextureAtlas(Gdx.files.internal("atlas/iso-tiles-0.atlas"));

        // Pre-process for efficiency
        for(TextureAtlas.AtlasRegion region : isoTiles0Atlas.getRegions()) {
            atlasMap.put(region.name, region);
        }
    }

    public TextureRegion getTextureRegion(String regionName) {
        return atlasMap.get(regionName);
    }

}
