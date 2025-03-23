package steve6472.planetoid;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;

public final class Textures 
{
    public static final int ICON_SIZE = 8;
    public static final String TEXTURE_ATLAS_PATH = "texture_atlas.png";
    public static final String SPACE_ROCK_PATH = "spacerock_tilemap.png";

    @NotNull
    public static TextureData readTextureResource(String path)
    {
        try
        {
            URL resource = Textures.class.getResource("/" + path);
            if (resource == null)
            {
                throw new RuntimeException("Resource not found at " + path);
            }
            BufferedImage img = ImageIO.read(resource);
            int[] atlas = new int[img.getWidth() * img.getHeight()];

            for (int i = 0; i < img.getWidth(); i++)
            {
                for (int j = 0; j < img.getHeight(); j++)
                {
                    atlas[i + j * img.getWidth()] = img.getRGB(i, j);
                }
            }

            return new TextureData(atlas, img.getWidth(), img.getHeight());
        } catch (IOException e)
        {
            throw new RuntimeException("No texture atlas");
        }
    }

    public record TextureData(int[] pixels, int width, int height)
    {
        public int getPixel(int x, int y)
        {
            return pixels[x + y * width];
        }
    }

    /*
     * Atlases
     */

    public static final TextureData ENTITY_ATLAS = Textures.readTextureResource(TEXTURE_ATLAS_PATH);
    public static final TextureData UI_COMPONENTS = Textures.readTextureResource(TEXTURE_ATLAS_PATH);
}
