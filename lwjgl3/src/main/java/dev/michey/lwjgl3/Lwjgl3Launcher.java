package dev.michey.lwjgl3;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.backends.lwjgl3.*;
import dev.michey.isoengine.IsoGame;
import org.lwjgl.glfw.GLFW;

/** Launches the desktop (LWJGL3) application. */
public class Lwjgl3Launcher {

    public static void main(String[] args) {
        if (StartupHelper.startNewJvmIfRequired()) return; // This handles macOS support and helps on Windows.
        createApplication();
    }

    private static Lwjgl3Application createApplication() {
        return new Lwjgl3Application(new IsoGame(), getDefaultConfiguration());
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();

        boolean vsync = false;

        configuration.useVsync(vsync);
        if(vsync) configuration.setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate);

        configuration.setTitle("Isometric Engine");
        configuration.setAutoIconify(true);
        configuration.setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png");
        configuration.setOpenGLEmulation(Lwjgl3ApplicationConfiguration.GLEmulation.GL30, 3, 3);
        configuration.setAudioConfig(2048, 512, 9);

        int mode = 0; // 0 = Windowed, 1 = Borderless, 2 = Fullscreen

        if(mode == 0) {
            // Windowed mode, use preferred width + height
            configuration.setWindowedMode(1280, 720);
        } else if(mode == 1) {
            // Borderless fullscreen, use screen dimensions
            Graphics.DisplayMode displayMode = Lwjgl3ApplicationConfiguration.getDisplayMode();
            configuration.setWindowedMode(displayMode.width, displayMode.height);
            configuration.setDecorated(false);
        } else if(mode == 2) {
            // Exclusive fullscreen, use display mode/monitor
            configuration.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode());
        }

        /*
        configuration.setWindowListener(new Lwjgl3WindowAdapter() {
            @Override
            public void created(Lwjgl3Window window) {
                GLFW.glfwSetInputMode(window.getWindowHandle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_CAPTURED);
            }
        });
         */

        return configuration;
    }

}
