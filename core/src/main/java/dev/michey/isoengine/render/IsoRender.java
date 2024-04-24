package dev.michey.isoengine.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import dev.michey.isoengine.IsoGame;
import dev.michey.isoengine.entity.Entity;
import dev.michey.isoengine.entity.EntityWalkingDummy;
import dev.michey.isoengine.map.Map;
import dev.michey.isoengine.map.RenderedInstance;
import dev.michey.isoengine.map.layer.Layer;
import dev.michey.isoengine.map.tile.Tile;
import dev.michey.isoengine.map.tile.TileUtils;
import dev.michey.isoengine.ui.IsoUI;
import imgui.ImGui;

public class IsoRender {

    /** Batches */
    public final SpriteBatch batch;
    public final SpriteBatch hudBatch;

    /** Fonts */
    private final BitmapFont font;

    /** Camera */
    public final OrthographicCamera camera;
    private final ScreenViewport viewport;
    private float currentCameraZoom = -1;
    private float targetCameraZoom = -1;
    private float cameraZoomDelta = -1;

    /** Timings */
    public float dt;

    /** UI */
    public IsoUI ui;

    /** User data */
    public int mouseX, mouseY;
    public float mouseWorldX, mouseWorldY;

    public boolean tileDataLoaded;
    public float mouseGridX, mouseGridY;
    public Tile selectedTile;

    public IsoRender() {
        ui = new IsoUI();

        batch = new SpriteBatch();
        hudBatch = new SpriteBatch();

        font = new BitmapFont();

        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        camera = new OrthographicCamera(30, 30 * (h / w));
        //camera.zoom = 0.5f;
        viewport = new ScreenViewport(camera);
    }

    public void resize(int width, int height) {
        viewport.update(width, height);
        hudBatch.setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, width, height));
        updateViewFrustum();
    }

    public void update() {
        dt = Gdx.graphics.getDeltaTime();

        mouseX = Gdx.input.getX();
        mouseY = Math.abs(Gdx.input.getY() - Gdx.graphics.getHeight());

        if(mouseX < 0) mouseX = 0;
        if(mouseX > Gdx.graphics.getWidth()) mouseX = Gdx.graphics.getWidth();
        if(mouseY < 0) mouseY = 0;
        if(mouseY > Gdx.graphics.getHeight()) mouseY = Gdx.graphics.getHeight();

        mouseWorldX = getMouseWorldX();
        mouseWorldY = getMouseWorldY();

        tileDataLoaded = IsoGame.engine.map != null;

        if(tileDataLoaded) {
            float tileSizeW = IsoGame.engine.map.tileSizeX;
            float tileSizeH = IsoGame.engine.map.tileSizeY;

            float normX = mouseWorldX / tileSizeW;
            float normY = mouseWorldY / tileSizeH;

            mouseGridX = (2 * normY + normX) - 1f;
            mouseGridY = (2 * normY - normX);

            mouseGridX = Math.clamp(mouseGridX, 0, IsoGame.engine.map.mapDimensionX - 1);
            mouseGridY = Math.clamp(mouseGridY, 0, IsoGame.engine.map.mapDimensionY - 1);

            if(selectedTile != null) {
                selectedTile.color.set(Color.WHITE);
            }

            var t = TileUtils.getCurrentTile();

            if(t != null) {
                t.color.set(Color.RED);
                selectedTile = t;
            }
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.F11)) {
            IsoGame.engine.toggleFullscreen();
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.F2)) {
            IsoGame.engine.isoImgui.uiActive = !IsoGame.engine.isoImgui.uiActive;
        }

        //

        if(Gdx.input.isKeyJustPressed(Input.Keys.Y)) {
            IsoGame.engine.render.camera.position.set(0, 0, 0);
            IsoGame.engine.render.camera.update();
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.F3)) {
            for(Entity e : IsoGame.engine.map.getAllEntities()) {
                e.moveByTileUnits(0.1f + MathUtils.random(0.2f), 0.1f + MathUtils.random(0.2f));
            }
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.F4)) {
            int spawn = 1;// MathUtils.random(8, 16);

            for(int i = 0; i < spawn; i++) {
                EntityWalkingDummy dummy = new EntityWalkingDummy();
                dummy.tileX = mouseGridX;
                dummy.tileY = mouseGridY;

                IsoGame.engine.map.addEntityToMap(dummy);
                IsoGame.engine.render.updateVisibilitySingle(dummy);
            }
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.F5)) {
            IsoGame.engine.map.deleteAllEntities();
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.F6)) {
            for(Entity e : IsoGame.engine.map.getAllEntities()) {
                System.out.println(e.toString());
            }
        }

        //

        float speed = 300f * (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) ? 4f : 1f);

        CAMERA_MOVEMENT_VECTOR.setZero();
        if(Gdx.input.isKeyPressed(Input.Keys.W)) CAMERA_MOVEMENT_VECTOR.add(0, 1);
        if(Gdx.input.isKeyPressed(Input.Keys.S)) CAMERA_MOVEMENT_VECTOR.add(0, -1);
        if(Gdx.input.isKeyPressed(Input.Keys.A)) CAMERA_MOVEMENT_VECTOR.add(-1, 0);
        if(Gdx.input.isKeyPressed(Input.Keys.D)) CAMERA_MOVEMENT_VECTOR.add(1, 0);

        /*
        if(mouseX <= 4) CAMERA_MOVEMENT_VECTOR.add(-1, 0);
        if(mouseX >= Gdx.graphics.getWidth() - 4) CAMERA_MOVEMENT_VECTOR.add(1, 0);
        if(mouseY <= 4) CAMERA_MOVEMENT_VECTOR.add(0, -1);
        if(mouseY >= Gdx.graphics.getHeight() - 4) CAMERA_MOVEMENT_VECTOR.add(0, 1);
        */

        if(!CAMERA_MOVEMENT_VECTOR.isZero()) {
            camera.translate(CAMERA_MOVEMENT_VECTOR.nor().scl(speed * dt));
            updateViewFrustum();
        }

        // Camera zoom animation
        if(cameraZoomDelta > 0) {
            cameraZoomDelta -= dt;

            if(cameraZoomDelta <= 0) {
                cameraZoomDelta = 0;
            }

            float progress = 1f - cameraZoomDelta / 0.125f;
            camera.zoom = currentCameraZoom + (targetCameraZoom - currentCameraZoom) * progress;

            if(camera.zoom <= 0.1f) {
                camera.zoom = 0.1f;
            }

            camera.update();
            updateViewFrustum();

            if(cameraZoomDelta <= 0) {
                currentCameraZoom = camera.zoom;
            }
        } else {
            camera.update();
        }
    }

    private final static Vector2 CAMERA_MOVEMENT_VECTOR = new Vector2();

    public void render() {
        ScreenUtils.clear(0, 0, 0, 1.0f);

        if(IsoGame.engine.isoImgui.uiActive) {
            IsoGame.engine.imGuiGlfw.newFrame();
            ImGui.newFrame();
        }

        batch.setProjectionMatrix(camera.combined);

        // MAP START
        batch.begin();
        IsoGame.engine.map.render();
        batch.end();

        // DEBUG START
        if(IsoGame.engine.isoImgui.drawTilePos.get()) {
            batch.begin();

            /*
            for(int i = 0; i < IsoGame.engine.map.layers.get(0).tiles.size(); i++) {
                if(i % 4 == 0) {
                    Tile t = IsoGame.engine.map.layers.get(0).tiles.get(i);
                    font.draw(batch, String.valueOf(t.tileId), t.worldX, t.worldY);
                }
            }
            */

            batch.end();
        }

        // HUD START
        hudBatch.begin();
        font.draw(hudBatch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 16, Gdx.graphics.getHeight() - 16);
        font.draw(hudBatch, "Move Entities (F3)", 16, Gdx.graphics.getHeight() - 32);
        font.draw(hudBatch, "Spawn Entities (F4)", 16, Gdx.graphics.getHeight() - 48);
        font.draw(hudBatch, "Delete Entities (F5)", 16, Gdx.graphics.getHeight() - 64);
        font.draw(hudBatch, "Dump Entities (F6)", 16, Gdx.graphics.getHeight() - 80);

        var map = IsoGame.engine.map;
        int tiles = 0;
        for(Layer l : map.getLayers()) {
            tiles += l.getTiles().size();
        }
        font.draw(hudBatch, "Data: Layers/Tiles/Entities: " + map.getLayers().size() + "/" + tiles + "/" + map.getAllEntities().size(), 16, Gdx.graphics.getHeight() - 128);

        int x = 0;
        for(Layer l : map.getLayers()) {
            font.draw(hudBatch, "Data [Layer " + l.layerIndex + "] " + l.getTiles().size() + " & " + l.getEntities().size(), 16, Gdx.graphics.getHeight() - 144 - 16 * x);
            x++;
        }

        ui.render(this);
        hudBatch.end();

        if(IsoGame.engine.isoImgui.uiActive) {
            IsoGame.engine.isoImgui.render();
            ImGui.render();
            IsoGame.engine.imGuiGl3.renderDrawData(ImGui.getDrawData());
        }
    }

    public void dispose() {
        batch.dispose();
    }

    public void updateViewFrustum() {
        Map map = IsoGame.engine.map;

        float thresholdX = map.tileSizeX;
        float thresholdY = map.tileSizeY;

        float x = getMouseWorldX(0) - thresholdX;
        float y = getMouseWorldY(0) - thresholdY;
        float x2 = getMouseWorldX(Gdx.graphics.getWidth()) + thresholdX;
        float y2 = getMouseWorldY(Gdx.graphics.getHeight()) + thresholdY;

        for(RenderedInstance inst : map.getBaseTileList()) {
            inst.visible = isVisibleCheck(inst, x, y, x2, y2);
        }

        for(RenderedInstance inst : map.getDynamicInstanceList()) {
            inst.visible = isVisibleCheck(inst, x, y, x2, y2);
        }
    }

    private boolean isVisibleCheck(RenderedInstance inst, float x, float y, float x2, float y2) {
        return inst.worldX >= x && inst.worldX < x2 && inst.worldY >= y && inst.worldY < y2;
    }

    public void updateVisibilitySingle(RenderedInstance instance) {
        Map map = IsoGame.engine.map;

        float thresholdX = map.tileSizeX;
        float thresholdY = map.tileSizeY;

        float x = getMouseWorldX(0) - thresholdX;
        float y = getMouseWorldY(0) - thresholdY;
        float x2 = getMouseWorldX(Gdx.graphics.getWidth()) + thresholdX;
        float y2 = getMouseWorldY(Gdx.graphics.getHeight()) + thresholdY;

        instance.visible = isVisibleCheck(instance, x, y, x2, y2);
    }

    public float getMouseWorldX() {
        float cx = camera.position.x;
        float vw = camera.viewportWidth; // 1280 -> window width
        float cz = camera.zoom;
        return cx - vw * 0.5f * cz + mouseX * cz;
    }

    public float getMouseWorldY() {
        float cy = camera.position.y;
        float vh = camera.viewportHeight; // 720 -> window height
        float cz = camera.zoom;
        return cy - vh * 0.5f * cz + mouseY * cz;
    }

    public float getMouseWorldX(float mx) {
        float cx = camera.position.x;
        float vw = camera.viewportWidth; // 1280 -> window width
        float cz = camera.zoom;
        return cx - vw * 0.5f * cz + mx * cz;
    }

    public float getMouseWorldY(float my) {
        float cy = camera.position.y;
        float vh = camera.viewportHeight; // 720 -> window height
        float cz = camera.zoom;
        return cy - vh * 0.5f * cz + my * cz;
    }

    private final static float[] SUPPORTED_ZOOM_LEVELS = new float[] {
        0.1f, 0.2f, 0.25f, 0.3334f, 0.5f, 1.0f, 2.0f, 3.0f, 4.0f
    };

    public void handleZoom(float amount) {
        currentCameraZoom = camera.zoom;
        targetCameraZoom = findClosestTarget(currentCameraZoom, amount);
        cameraZoomDelta = 0.125f;
    }

    private float findClosestTarget(float current, float direction) {
        float selectedTarget = current;

        if(direction < 0) {
            // Negative direction.
            float lastDst = Float.MAX_VALUE;

            for(float supported : SUPPORTED_ZOOM_LEVELS) {
                if(supported < current) {
                    float dst = Math.abs(current - supported);

                    if(dst < lastDst) {
                        lastDst = dst;
                        selectedTarget = supported;
                    }
                }
            }
        } else {
            // Positive direction.
            float lastDst = Float.MAX_VALUE;

            for(float supported : SUPPORTED_ZOOM_LEVELS) {
                if(supported > current) {
                    float dst = Math.abs(current - supported);

                    if(dst < lastDst) {
                        lastDst = dst;
                        selectedTarget = supported;
                    }
                }
            }
        }

        return selectedTarget;
    }

}
