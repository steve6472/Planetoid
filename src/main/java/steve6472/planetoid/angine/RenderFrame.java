package steve6472.planetoid.angine;

import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.*;
import java.util.List;

import javax.swing.JFrame;

import steve6472.planetoid.Textures;
import steve6472.planetoid.api.Input;
import steve6472.planetoid.api.Render;

public class RenderFrame extends Canvas
{
    private final JFrame frame;

    public List<RenderableImage> images = new ArrayList<>();

    public int screenWidth, screenHeight, pixelSize;

    public final Input input;
    public final Render render;

    public Set<DebugText> debugTextSet = new HashSet<>();

    public Runnable onClose;

    public RenderFrame(String frameTitle, int width, int height, int pixelSize)
    {
        Dimension d = new Dimension(width, height);
        setMaximumSize(d);
        setMinimumSize(d);
        setPreferredSize(d);
        frame = new JFrame(frameTitle);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.add(this, BorderLayout.CENTER);
        frame.pack();
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setLocation(frame.getX() - 300, frame.getY());
        frame.setVisible(true);
        frame.requestFocus();
        frame.addWindowStateListener(new CloseListener());

        this.screenWidth = width;
        this.screenHeight = height;
        this.pixelSize = pixelSize;

        BufferedImage image = new BufferedImage(width / pixelSize, height / pixelSize, BufferedImage.TYPE_INT_RGB);
        int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
        images.add(new RenderableImage(image, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR));

        UserInput input = new UserInput(width, height, pixelSize);
        var mouseListener = input.new PixelClickListener();
        addKeyListener(input.new InputKeyListener());
        addMouseListener(mouseListener);
        addMouseMotionListener(mouseListener);

        this.render = new Pixels(this, pixels);
        this.render.setAtlas(Textures.ENTITY_ATLAS);
        this.input = input;
    }

    public void render()
    {
        BufferStrategy bs = getBufferStrategy();
        if (bs == null)
        {
            createBufferStrategy(3);
            return;
        }
        
        //TODO: render here

        //game.render(this);
        
        Graphics g = bs.getDrawGraphics();
        Graphics2D g2d = (Graphics2D) g;

        for (RenderableImage image : images)
        {
            if (!image.enabled)
                continue;

            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, image.interpolation);

            g.drawImage(image.image, 0, 0, getWidth(), getHeight(), null);
        }

        g.setColor(Color.ORANGE);
        renderGraphics(g);

        g.dispose();
        bs.show();
    }

    private void renderGraphics(Graphics g)
    {
        for (DebugText debugText : debugTextSet)
        {
            Font font = new Font("monogram", Font.PLAIN, debugText.size * pixelSize);
//            g.drawString(debugText.str, debugText.x * pixelSize, debugText.y * pixelSize);
            int i = 0;
            String[] split = debugText.str.split("\n");
            int ascent = g.getFontMetrics(font).getAscent();
            g.setColor(Color.BLACK);
            for (String s : split)
            {
                drawCenteredString(g, s, debugText.x * pixelSize + 2, debugText.y * pixelSize + i * ascent - (split.length * ascent) / 2 + 2, font);
                i++;
            }
            i = 0;
            g.setColor(Color.WHITE);
            for (String s : split)
            {
                drawCenteredString(g, s, debugText.x * pixelSize, debugText.y * pixelSize + i * ascent - (split.length * ascent) / 2, font);
                i++;
            }
        }

        /*if (Debug.FRAME.get())
        {
            for (int i = 0; i < 32; i++)
            {
                for (int j = 0; j < 32; j++)
                {
                    g.setColor(Color.CYAN);
                    g.drawRect((int) (-render.offsetX() * 4) + i * 4 * 8, (int) (-render.offsetY() * 4) + j * 4 * 8, 32, 32);

                    g.setColor(Color.ORANGE);
                    g.drawString("" + ChunkColumn.AWFE[i][j], (int) (i * 4 * 8 + 10 - render.offsetX() * 4), (int) (j * 4 * 8 + 24 - render.offsetY() * 4));
                }
            }
        }*/
    }

    public void drawCenteredString(Graphics g, String text, int x, int y, Font font) {
        // Get the FontMetrics
        FontMetrics metrics = g.getFontMetrics(font);
        // Determine the X coordinate for the text
        x = x - metrics.stringWidth(text) / 2;
        // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top of the screen)
        y = y - metrics.getHeight() / 2 + metrics.getAscent() / 2;
        // Set the font
        g.setFont(font);
        // Draw the String
        g.drawString(text, x, y);
    }

    public JFrame getFrame()
    {
        return frame;
    }

    public int[] createAndAddImage(int width, int height, Object interpolation)
    {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
        images.add(new RenderableImage(image, interpolation));
        return pixels;
    }

    public record DebugText(String str, int x, int y, int size) {}

    public static final class RenderableImage
    {
        public final BufferedImage image;
        public Object interpolation;
        public boolean enabled = true;

        public RenderableImage(BufferedImage image, Object interpolation)
        {
            this.image = image;
            this.interpolation = interpolation;
        }
    }

    private class CloseListener implements WindowStateListener
    {
        @Override
        public void windowStateChanged(WindowEvent e)
        {
            if (onClose != null)
                onClose.run();
        }
    }
}
