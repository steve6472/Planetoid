package steve6472.planetoid.api;

import steve6472.planetoid.Icon;
import steve6472.planetoid.Textures;
import steve6472.core.util.ColorUtil;

public interface Render
{
    int getPixel(int index);
    void setPixel(int index, int color);

    int getPixelFromAtlas(int x, int y);

    void setAtlas(Textures.TextureData atlas);

    Textures.TextureData getAtlas();

    void render();
    
    int getWidth();
    int getHeight();
    int getIconSize();

    double offsetX();
    double offsetY();
    void setOffsetX(double offsetX);
    void setOffsetY(double offsetY);

    default void setPixel(int x, int y, int color)
    {
        x -= (int) Math.floor(offsetX());
        y -= (int) Math.floor(offsetY());
        if (x < 0 || x >= getWidth() || y < 0 || y >= getHeight())
            return;

        setPixel(x + y * getWidth(), color);
    }

    default int getPixel(int x, int y)
    {
        x -= (int) Math.floor(offsetX());
        y -= (int) Math.floor(offsetY());
        if (x < 0 || x >= getWidth() || y < 0 || y >= getHeight())
            return 0;

        return getPixel(x + y * getWidth());
    }

    default void setFromIcon(double x, double y, Icon icon, boolean background)
    {
        setFromAtlas((int) Math.floor(x), (int) Math.floor(y), icon.x(), icon.y(), background);
    }

    default void setFromIcon(double x, double y, Icon icon, boolean background, boolean flipX, boolean flipY)
    {
        setFromAtlas((int) Math.floor(x), (int) Math.floor(y), icon.x(), icon.y(), background, flipX, flipY);
    }

    default void setFromAtlas(int x, int y, int iconX, int iconY, boolean background)
    {
        copyFromAtlas(x, y, iconX * getIconSize(), iconY * getIconSize(), getIconSize(), getIconSize(), background, false, false);
    }

    default void setFromAtlas(int x, int y, int iconX, int iconY, boolean background, boolean flipX, boolean flipY)
    {
        copyFromAtlas(x, y, iconX * getIconSize(), iconY * getIconSize(), getIconSize(), getIconSize(), background, flipX, flipY);
    }

    default void copyFromAtlas(int x, int y, int atlasX, int atlasY, int width, int height, boolean background, boolean flipX, boolean flipY)
    {
        for (int i = 0; i < width; i++)
        {
            for (int j = 0; j < height; j++)
            {
                int px = flipY ? (width - i - 1) : i;
                int py = flipX ? (height - j - 1) : j;

                int rgb = getPixelFromAtlas(px + atlasX, py + atlasY);
                if (background)
                {
                    setPixel(i + x, j + y, rgb);
                }
                else
                {
                    int a = ColorUtil.getAlpha(rgb);
                    if (a == 0)
                        continue;
                    if (a != 255)
                        setPixel(i + x, j + y, ColorUtil.blendNoAlpha(rgb, getPixel(i + x, j + y), a / 255.0));
                    else
                        setPixel(i + x, j + y, rgb);
                }
            }
        }
    }

    default void fillRectangle(double x, double y, double width, double height, int color)
    {
        int X = (int) Math.floor(x);
        int Y = (int) Math.floor(y);
        int W = (int) Math.floor(width);
        int H = (int) Math.floor(height);

        for (int i = X; i < X + W; i++)
        {
            for (int j = Y; j < Y + H; j++)
            {
                setPixel(i, j, color);
            }
        }
    }

    default void drawDebugRectangle(double x, double y, double width, double height, int color, boolean flip)
    {
        int X = (int) Math.floor(x);
        int Y = (int) Math.floor(y);
        int W = (int) Math.floor(width);
        int H = (int) Math.floor(height);

        int a = ColorUtil.getAlpha(color);
        if (a == 0)
            return;

        for (int i = X; i < X + W; i++)
        {
            for (int j = Y; j < Y + H; j++)
            {
                if (i == X || i == X + W - 1 || j == Y || j == Y + H - 1)
                {
                    if ((i - X + j - Y) % 2 == (flip ? 1 : 0))
                    {
                      if (a != 255)
                          setPixel(i, j, ColorUtil.blendNoAlpha(color, getPixel(i, j), a / 255.0));
                      else
                          setPixel(i, j, color);
                    }
                }
            }
        }
    }
}
