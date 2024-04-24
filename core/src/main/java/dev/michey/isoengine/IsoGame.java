package dev.michey.isoengine;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics;
import dev.michey.isoengine.assets.IsoAssets;
import dev.michey.isoengine.input.UserInput;
import dev.michey.isoengine.map.Map;
import dev.michey.isoengine.map.MapLoader;
import dev.michey.isoengine.map.layer.Layer;
import dev.michey.isoengine.map.tile.Tile;
import dev.michey.isoengine.render.IsoImgui;
import dev.michey.isoengine.render.IsoRender;
import imgui.ImFontAtlas;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import org.lwjgl.glfw.GLFW;

public class IsoGame extends ApplicationAdapter {

    /** Singleton engine instance */
    public static IsoGame engine;

    /** Assets */
    public IsoAssets assets;

    /** Rendering */
    public IsoRender render;

    /** ImGui */
    public ImGuiImplGlfw imGuiGlfw;
    public ImGuiImplGl3 imGuiGl3;
    public IsoImgui isoImgui;

    /** Maps */
    public final MapLoader mapLoader;
    public Map map;

    public IsoGame() {
        engine = this;
        mapLoader = new MapLoader();
    }

    @Override
    public void create() {
        assets = new IsoAssets();
        render = new IsoRender();

        imGuiGlfw = new ImGuiImplGlfw();
        imGuiGl3 = new ImGuiImplGl3();
        isoImgui = new IsoImgui();

        {
            // Initialize ImGui
            long pointer = ((Lwjgl3Graphics) Gdx.graphics).getWindow().getWindowHandle();
            GLFW.glfwMakeContextCurrent(pointer);
            ImGui.createContext();

            ImGuiIO io = ImGui.getIO();
            // io.setWantSaveIniSettings(false); 						For production build.

            String customFont = Gdx.files.local("Roboto-Regular.ttf").file().getAbsolutePath();

            ImFontAtlas atlas = io.getFonts();
            atlas.addFontFromFileTTF(customFont, 16);
            atlas.build();

            imGuiGlfw.init(pointer, true);
            imGuiGl3.init("#version 330");

        }

        map = mapLoader.loadMap("debug.map");
        render.updateViewFrustum();

        Gdx.input.setInputProcessor(new UserInput());

        /*
        String[] vars = new String[] {"a", "a", "a", "b", "b", "c", "d"};

        for(int i = 0; i < 400; i++) {
            String fullLine = "";

            for(int j = 0; j < 400; j++) {
                fullLine += vars[MathUtils.random(0, vars.length - 1)] + ";";
            }

            System.out.println(fullLine.substring(0, fullLine.length() - 1));
        }
        */
    }

    public void toggleFullscreen() {
        boolean fs = Gdx.graphics.isFullscreen();

        if(fs) {
            // Switch to windowed mode.
            Gdx.graphics.setUndecorated(false);
            Gdx.graphics.setWindowedMode(1280, 720);
        } else {
            // Switch to fullscreen mode.
            Gdx.graphics.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode());
        }
    }

    @Override
    public void resize(int width, int height) {
        render.resize(width, height);
    }

    @Override
    public void render() {
        render.update();
        render.render();
    }

    @Override
    public void dispose() {
        render.dispose();
    }

}
