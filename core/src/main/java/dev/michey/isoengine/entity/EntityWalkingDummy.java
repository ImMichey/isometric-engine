package dev.michey.isoengine.entity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import dev.michey.isoengine.IsoGame;
import dev.michey.isoengine.render.IsoRender;

public class EntityWalkingDummy extends Entity {

    private TextureRegion texture;

    private float delta;
    private Vector2 dir = new Vector2();

    public EntityWalkingDummy() {
        texture = IsoGame.engine.assets.getTextureRegion("walkingdummy");
        resetDir();
    }

    @Override
    public void tick(IsoRender r) {
        /*
        delta -= r.dt;

        if(delta <= 0) {
            resetDir();
        }

        float mx = dir.x * r.dt;
        float my = dir.y * r.dt;

        moveByTileUnits(mx, my);
        */

        float mx = IsoGame.engine.render.mouseGridX;
        float my = IsoGame.engine.render.mouseGridY;
        moveByTileUnits(mx - tileX, my - tileY);
    }

    @Override
    public void render(IsoRender r) {
        r.batch.setColor(Color.WHITE);
        r.batch.draw(texture, worldX, worldY);
    }

    private void resetDir() {
        delta = MathUtils.random(3.0f, 6.0f);
        dir.set(circularRandom(1.0f));
    }

    public static Vector2 circularRandom(float radius) {
        return circular(MathUtils.random(360f), radius);
    }

    public static Vector2 circular(float angle, float radius) {
        return new Vector2(
            MathUtils.cosDeg(angle) * radius,
            MathUtils.sinDeg(angle) * radius
        );
    }

}
