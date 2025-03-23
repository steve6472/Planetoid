package steve6472.planetoid;

import steve6472.planetoid.aimguihax.Application;
import steve6472.planetoid.aimguihax.Debug;
import steve6472.planetoid.angine.Pixels;
import steve6472.planetoid.angine.RenderFrame;
import steve6472.planetoid.api.Input;
import steve6472.planetoid.api.Render;
import steve6472.core.log.Log;
import steve6472.core.util.Profiler;
import steve6472.planetoid.ui.Gui;
import steve6472.planetoid.world.Universe;
import steve6472.planetoid.world.World;

import java.awt.*;
import java.util.logging.Logger;

/**
 * Created by steve6472
 * Date: 2/28/2024
 * Project: Domin <br>
 */
public class ClientApp
{
    // TODO: switch to 7
    public static final int PIXEL_SIZE = 7;
    public static final int WINDOW_WIDTH = 128 * PIXEL_SIZE;
    public static final int WINDOW_HEIGHT = 128 * PIXEL_SIZE;

    private static final Logger LOGGER = Log.getLogger(ClientApp.class);
    private World world;

    private final RenderFrame frame;

    private Gui currentScreen;
    private final Render guiRender;
    public final RenderFrame.RenderableImage emissionImageSettings;
    private final Render emission;
    private Debug debug;
    private final Render render;
    private final Input input;

//    public final Systems clientSystems;
    public final Profiler worldRenderProfiler = new Profiler(10);
    public final Profiler clientRenderProfiler = new Profiler(10);

    public ClientApp()
    {
        this.frame = new RenderFrame("ECS Test", WINDOW_WIDTH, WINDOW_HEIGHT, PIXEL_SIZE);
        this.input = frame.input;
        this.render = frame.render;

        Object emissionFiltering = Debug.FANCY_EMISSION.get() ? RenderingHints.VALUE_INTERPOLATION_BICUBIC : RenderingHints.VALUE_INTERPOLATION_BILINEAR;
        this.emission = new Pixels(frame, frame.createAndAddImage(WINDOW_WIDTH / PIXEL_SIZE, WINDOW_HEIGHT / PIXEL_SIZE, emissionFiltering));
        emissionImageSettings = frame.images.getLast();
        emissionImageSettings.enabled = false; // TODO: remove

        this.guiRender = new Pixels(frame, frame.createAndAddImage(WINDOW_WIDTH / PIXEL_SIZE, WINDOW_HEIGHT / PIXEL_SIZE, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR));
        this.guiRender.setAtlas(Textures.UI_COMPONENTS);

//        this.clientSystems = new Systems();

        createRenderingSystems();

//        openScreen(new GameHUD(this));
    }

    public void startDebug(Universe universe, Profiler mainScheduler)
    {
        // Debug has to be last
        this.debug = new Debug(input, universe);
        this.debug.profilers.add(new Debug.ProfilerEntry(mainScheduler, "Main"));
        Application.launch(debug);
    }

    public void runClientSystems()
    {
        if (world == null)
            return;

        synchronized (world.ecs())
        {
            //TODO: run screen clear here
            renderWorld();
//            clientSystems.run();
        }
    }

    private void renderWorld()
    {
        worldRenderProfiler.start();

        worldRenderProfiler.end();
    }

    public void render()
    {
        clientRenderProfiler.start();
        render.render();
//        if (currentScreen != null)
//            currentScreen.render(guiRender);
        clientRenderProfiler.end();
    }

    public Input getInput() 
    {
        return input;
    }

    public Render getRender() 
    {
        return render;
    }

    public World getWorld()
    {
        return world;
    }

    public void openScreen(Gui gui)
    {
        this.currentScreen = gui;
    }

    public Gui getCurrentScreen()
    {
        return currentScreen;
    }

    /*
     * Client systems 'n stuff
     */

    private void createRenderingSystems()
    {
        LOGGER.fine("Creating Rendering Systems");
        LOGGER.fine("Created Rendering Systems");
    }
}
