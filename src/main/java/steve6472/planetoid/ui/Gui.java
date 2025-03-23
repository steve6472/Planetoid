package steve6472.planetoid.ui;

import steve6472.planetoid.api.Render;

public abstract class Gui 
{
    public abstract void render(Render render);
    public abstract void tick();

    protected void clearScreen(Render render)
    {
        render.fillRectangle(0, 0, render.getWidth(), render.getHeight(), 0);
    }
}