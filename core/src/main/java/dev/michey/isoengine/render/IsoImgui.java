package dev.michey.isoengine.render;

import dev.michey.isoengine.IsoGame;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.type.ImBoolean;

public class IsoImgui {

    public boolean uiActive = true;
    public final ImBoolean drawTilePos = new ImBoolean(false);

    public void render() {
        var rnd = IsoGame.engine.render;
        var cam = rnd.camera;

        ImGui.begin("Isometric Engine");

        coloredBulletText(0.0f, 1.0f, 1.0f, "Camera Zoom", String.valueOf(cam.zoom));
        coloredBulletText(0.0f, 1.0f, 1.0f, "Camera Pos", String.valueOf(cam.position));

        ImGui.separator();

        coloredBulletText(0.0f, 1.0f, 0.0f, "Mouse Screen Pos");
        ImGui.sameLine();
        ImGui.textColored(0.8f, 0.4f, 0.0f, 1.0f, String.valueOf(rnd.mouseX));
        ImGui.sameLine();
        ImGui.textColored(0.8f, 0.6f, 0.0f, 1.0f, String.valueOf(rnd.mouseY));

        coloredBulletText(0.0f, 1.0f, 0.0f, "Mouse World Pos");
        ImGui.sameLine();
        ImGui.textColored(0.8f, 0.4f, 0.0f, 1.0f, String.valueOf(rnd.mouseWorldX));
        ImGui.sameLine();
        ImGui.textColored(0.8f, 0.6f, 0.0f, 1.0f, String.valueOf(rnd.mouseWorldY));

        if(rnd.tileDataLoaded) {
            coloredBulletText(0.0f, 1.0f, 0.0f, "Mouse Grid Pos");
            ImGui.sameLine();
            ImGui.textColored(0.8f, 0.4f, 0.0f, 1.0f, String.valueOf(rnd.mouseGridX));
            ImGui.sameLine();
            ImGui.textColored(0.8f, 0.6f, 0.0f, 1.0f, String.valueOf(rnd.mouseGridY));

            coloredBulletText(0.0f, 1.0f, 0.0f, "Mouse Tile");
            ImGui.sameLine();
            ImGui.textColored(0.8f, 0.4f, 0.0f, 1.0f, String.valueOf(rnd.selectedTile));

            ImGui.separator();

            ImGui.checkbox("Draw Tile Pos", drawTilePos);
        }

        ImGui.end();
    }

    private void coloredBulletText(float r, float g, float b, String text) {
        ImGui.bullet();
        ImGui.sameLine();
        ImGui.pushStyleColor(ImGuiCol.Text, r, g, b, 1.0f);
        ImGui.text(text);
        ImGui.popStyleColor();
    }

    private void coloredBulletText(float r, float g, float b, String text, String value) {
        ImGui.bullet();
        ImGui.sameLine();
        ImGui.pushStyleColor(ImGuiCol.Text, r, g, b, 1.0f);
        ImGui.text(text);
        ImGui.popStyleColor();
        ImGui.sameLine();
        ImGui.text(value);
    }

}
