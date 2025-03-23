package steve6472.planetoid;

import dev.dominion.ecs.api.Scheduler;
import dev.dominion.ecs.engine.SystemScheduler;
import dev.dominion.ecs.engine.system.Logging;
import steve6472.planetoid.aimguihax.Application;
import steve6472.planetoid.aimguihax.Debug;
import steve6472.planetoid.angine.RenderFrame;
import steve6472.planetoid.api.Input;
import steve6472.planetoid.api.Render;
import steve6472.core.SteveCore;
import steve6472.core.log.Log;
import steve6472.core.module.ModuleManager;
import steve6472.core.util.JarExport;
import steve6472.core.util.Profiler;
import steve6472.planetoid.event.PlanetoidEvents;
import steve6472.planetoid.sound.GameSound;
import steve6472.planetoid.world.Universe;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Logger;

/**
 * Created by steve6472
 * Date: 3/22/2025
 * Project: Planetoid <br>
 */
public abstract class PlanetoidApp
{
    private static final Logger LOGGER = Log.getLogger(PlanetoidApp.class);
    private static final ModuleManager MODULE_MANAGER = new ModuleManager();

    // Window settings
    private int pixelSize = 7;
    private int windowWidth = 128 * pixelSize;
    private int windowHeight = 128 * pixelSize;

    private RenderFrame frame;
    private Render render;
    private Input input;

    private final Profiler mainProfiler = new Profiler(10);
    private Scheduler mainScheduler;
    public static long tick = 0;

    public final Systems renderSystems = new Systems();
    private Universe universe;

    private Debug debug;

    protected void init()
    {
        SteveCore.DEFAULT_KEY_NAMESPACE = PlanetoidConstants.NAMESPACE;
        SteveCore.CORE_MODULE = PlanetoidConstants.NAMESPACE;
        LOGGER.info("Creating Contents of Registries");
        GameSound.init();
        GameSound.setGlobalVolume(0.05);

        // Create 'modules' folder
        JarExport.createFolderOrError(SteveCore.MODULES);
        // Export default 'planetoid' module
        File planetoidDefaultModule = new File(SteveCore.MODULES, PlanetoidConstants.NAMESPACE);
        JarExport.createFolderOrError(planetoidDefaultModule);
        try
        {
            JarExport.exportFolder("planetoid/module", planetoidDefaultModule);
        } catch (IOException | URISyntaxException e)
        {
            throw new RuntimeException(e);
        }

        MODULE_MANAGER.loadModules();
        PlanetoidEvents.LOAD_MODULES.trigger();
        PlanetoidEvents.LOAD_MODULES.printLast(LOGGER, "Loaded modules");

        PlanetoidRegistries.createContents();
    }

    protected void createFrame(String title)
    {
        this.frame = new RenderFrame("ECS Test", windowWidth, windowHeight, pixelSize);
        this.input = frame.input;
        this.render = frame.render;
    }

    protected void setWindowSize(int width, int height, int pixelSize)
    {
        this.pixelSize = pixelSize;
        this.windowWidth = width * pixelSize;
        this.windowHeight = height * pixelSize;
    }

    protected void startLoop()
    {
        int levelIndex = Logging.registerLoggingLevel(Logging.DEFAULT_LOGGING_LEVEL);
        mainScheduler = new SystemScheduler(3, new Logging.Context("main", levelIndex));

        mainScheduler.schedule(() ->
        {
            mainProfiler.start();
            PlanetoidEvents.TICK_PRE.trigger();

            PlanetoidEvents.FRAME_PRE.trigger(mainScheduler.deltaTime());
            renderSystems.run();
            PlanetoidEvents.FRAME_POST.trigger(mainScheduler.deltaTime());

            PlanetoidEvents.TICK_POST.trigger();
            mainProfiler.end();
            tick++;
        });

        mainScheduler.tickAtFixedRate(60);
    }

    protected void startDebugger()
    {
        LOGGER.info("Starting debugger");
        debug = new Debug(input, null);
        debug.profilers.add(new Debug.ProfilerEntry(mainProfiler, "Main"));
        debug.profilers.add(new Debug.ProfilerEntry(PlanetoidEvents.TICK_PRE.profiler(), "Tick Pre Event"));
        debug.profilers.add(new Debug.ProfilerEntry(PlanetoidEvents.FRAME_PRE.profiler(), "Frame Pre Event"));
        debug.profilers.add(new Debug.ProfilerEntry(renderSystems.profiler, "Render Systems"));
        debug.profilers.add(new Debug.ProfilerEntry(PlanetoidEvents.FRAME_POST.profiler(), "Frame Post Event"));
        debug.profilers.add(new Debug.ProfilerEntry(PlanetoidEvents.TICK_POST.profiler(), "Tick Post Event"));
        PlanetoidEvents.SETUP_DEBUG.trigger(debug);
        Application.launch(debug);
    }

    protected void createUniverse()
    {
        universe = new Universe();
    }
    protected void letThereBeLight() { createUniverse(); }

    protected void disableDominionLog()
    {
        System.setProperty("dominion.world.size", "LARGE");
        System.setProperty("dominion.show-banner", "false");
    }
}
