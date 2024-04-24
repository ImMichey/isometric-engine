package dev.michey.isoengine.map;

import dev.michey.isoengine.map.layer.Layer;
import dev.michey.isoengine.render.IsoRender;

public abstract class RenderedInstance {

    // Position expressed in tile units
    public float tileX, tileY;
    // Position expressed in absolute world units (pixel)
    public float worldX, worldY;
    // Layer
    public Layer layer;
    // Visibility
    public boolean visible;

    public abstract void render(IsoRender r);
    public abstract void updateWorldPosition();

}
