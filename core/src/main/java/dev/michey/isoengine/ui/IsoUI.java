package dev.michey.isoengine.ui;

import com.badlogic.gdx.graphics.Texture;
import dev.michey.isoengine.render.IsoRender;

public class IsoUI {

    private Texture texture;
    public boolean selected;

    public IsoUI() {
        texture = new Texture("testobj.png");
    }

    public void render(IsoRender r) {
        if(selected) {
            r.hudBatch.draw(texture, 200, 200, 64, 64);
        }
    }

    public void test() {
        selected = !selected;
    }

}
