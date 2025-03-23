package steve6472.planetoid.angine;

import steve6472.planetoid.Textures;
import steve6472.planetoid.api.Render;

public class Pixels implements Render
{
    RenderFrame frame;
    protected int[] pixels;
    protected int[] atlasPixels;
    private int atlasWidth;
    private Textures.TextureData atlas;
    private double offsetX, offsetY;

    public Pixels(RenderFrame frame, int[] pixels)
    {
        this.frame = frame;
        this.pixels = pixels;
    }

    @Override
    public int getPixel(int index) 
    {
        return pixels[index];
    }

    @Override
    public void setPixel(int index, int color) 
    {
        pixels[index] = color;
    }

    @Override
    public void render() 
    {
        frame.render();
    }

    @Override
    public int getWidth() 
    {
        return frame.screenWidth / frame.pixelSize;
    }

    @Override
    public int getHeight() 
    {
        return frame.screenHeight / frame.pixelSize;
    }

    @Override
    public int getIconSize() 
    {
        return Textures.ICON_SIZE;
    }

    @Override
    public int getPixelFromAtlas(int x, int y) 
    {
        return atlasPixels[x + y * atlasWidth];
    }

    @Override
    public void setAtlas(Textures.TextureData atlas)
    {
        this.atlasWidth = atlas.width();
        this.atlasPixels = atlas.pixels();
        this.atlas = atlas;
    }

    @Override
    public Textures.TextureData getAtlas()
    {
        return atlas;
    }

    @Override
    public double offsetX() 
    {
        return offsetX;
    }

    @Override
    public double offsetY() 
    {
        return offsetY;
    }

    @Override
    public void setOffsetX(double offsetX) 
    {
        this.offsetX = offsetX;
        frame.input.setRenderOffsets(offsetX, offsetY);
    }

    @Override
    public void setOffsetY(double offsetY) 
    {
        this.offsetY = offsetY;
        frame.input.setRenderOffsets(offsetX, offsetY);
    }
}
