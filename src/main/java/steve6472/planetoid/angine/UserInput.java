package steve6472.planetoid.angine;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Set;

import org.joml.Vector2i;
import steve6472.planetoid.Textures;
import steve6472.planetoid.api.Input;

public class UserInput implements Input
{
    public final Set<Integer> pressedKeys = new HashSet<>();
    public final Set<Integer> pressedMouseButtons = new HashSet<>();
    
    private int width;
    private int height;
    private int pixelSize;

    private boolean mouseInWindow = false;
    private int mouseX;
    private int mouseY;

    private double renderOffsetX, renderOffsetY;

    public UserInput(int width, int height, int pixelSize)
    {
        this.width = width;
        this.height = height;
        this.pixelSize = pixelSize;
    }

    @Override
    public boolean isKeyPressed(int key) 
    {
        return pressedKeys.contains(key);
    }

    @Override
    public boolean isMousePressed(int key) 
    {
        return pressedMouseButtons.contains(key);
    }

    public class PixelClickListener extends MouseAdapter 
    {
        @Override
        public void mousePressed(MouseEvent e) 
        {
            pressedMouseButtons.add(e.getButton());
        }

        @Override
        public void mouseReleased(MouseEvent e)
        {
            pressedMouseButtons.remove(e.getButton());

            int x = e.getX() / pixelSize;
            int y = e.getY() / pixelSize;

            if (x >= 0 && x < width / pixelSize && y >= 0 && y < height / pixelSize)
            {
                int pixelX = e.getX() / pixelSize + (int) Math.floor(renderOffsetX);
                int pixelY = e.getY() / pixelSize + (int) Math.floor(renderOffsetY);
//                eventQueue.add(new Click(pixelX, pixelY, Click.ClickType.fromButton(e.getButton()), Click.ClickModifier.NONE));
//
//                Vec2i mouseTileLoc = getMouseTileLoc();
//                eventQueue.add(new TileClick(mouseTileLoc.x, mouseTileLoc.y, pixelX, pixelY, Click.ClickType.fromButton(e.getButton()), Click.ClickModifier.NONE));
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) 
        {
            mouseX = e.getX();
            mouseY = e.getY();
        }

        @Override
        public void mouseDragged(MouseEvent e) 
        {
            mouseX = e.getX();
            mouseY = e.getY();
        }

        @Override
        public void mouseEntered(MouseEvent e) 
        {
            mouseInWindow = true;
        }

        @Override
        public void mouseExited(MouseEvent e) 
        {
            mouseInWindow = false;
        }
    }
    
    public class InputKeyListener extends KeyAdapter 
    {
        @Override
        public void keyPressed(KeyEvent e) {
            pressedKeys.add(e.getKeyCode());
        }

        @Override
        public void keyReleased(KeyEvent e) {
            pressedKeys.remove(e.getKeyCode());
        }
    }

    @Override
    public void setRenderOffsets(double x, double y) 
    {
        this.renderOffsetX = x;
        this.renderOffsetY = y;
    }

    @Override
    public Vector2i getRawMouseLoc()
    {
        return new Vector2i(mouseX, mouseY);
    }

    @Override
    public Vector2i getPixelMouseLocOnScreen()
    {
        return new Vector2i(mouseX / pixelSize, mouseY / pixelSize);
    }

    @Override
    public Vector2i getPixelMouseLocInWorld()
    {
        return new Vector2i(mouseX / pixelSize + (int) Math.floor(renderOffsetX), mouseY / pixelSize + (int) Math.floor(renderOffsetY));
    }

    @Override
    public Vector2i getMouseTileLoc()
    {
        int ox = ((int) Math.floor(renderOffsetX));
        int oy = ((int) Math.floor(renderOffsetY));
        Vector2i pw = getPixelMouseLocInWorld();
        if (pw.x < 0) ox -= Textures.ICON_SIZE - 1;
        if (pw.y < 0) oy -= Textures.ICON_SIZE - 1;
        return new Vector2i((mouseX / pixelSize + ox) / Textures.ICON_SIZE, (mouseY / pixelSize + oy) / Textures.ICON_SIZE);
    }

    @Override
    public boolean isMouseInWindow() 
    {
        return mouseInWindow;
    }
}
