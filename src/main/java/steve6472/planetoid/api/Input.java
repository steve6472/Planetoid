package steve6472.planetoid.api;

import org.joml.Vector2i;

public interface Input
{
    boolean isKeyPressed(int key);
    boolean isMousePressed(int key);
    void setRenderOffsets(double x, double y);

    Vector2i getRawMouseLoc();
    Vector2i getPixelMouseLocOnScreen();
    Vector2i getPixelMouseLocInWorld();
    Vector2i getMouseTileLoc();
    boolean isMouseInWindow();
}
