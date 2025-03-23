package steve6472.planetoid;

import dev.dominion.ecs.api.Scheduler;
import dev.dominion.ecs.engine.SystemScheduler;
import dev.dominion.ecs.engine.system.Logging;
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

/**********************
 * Created by steve6472
 * On date: 12/29/2021
 * Project: ScriptIt
 * </br>
 * Art used: <a href="https://gvituri.itch.io/tiny-ranch">Tiny Ranch</a>
 * Font used: <a href="https://itch.io/t/214662/monogram-a-free-monospace-pixel-font">monogram</a>
 *
 ***********************/
public class Main
{
    private static final Logger LOGGER = Log.getLogger(Main.class);

    private final Scheduler mainScheduler;
    public static long tick = 0;
    public static Profiler mainSchedulerProfiler = new Profiler(10);

    public static final ModuleManager MODULE_MANAGER = new ModuleManager();

    public Universe universe;

    private Main() throws IOException, URISyntaxException
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
        JarExport.exportFolder("planetoid/module", planetoidDefaultModule);

        MODULE_MANAGER.loadModules();
        PlanetoidEvents.LOAD_MODULES.trigger();

        PlanetoidRegistries.createContents();

        ClientApp clientApp = new ClientApp();

        universe = new Universe();
//        World world = new World(universe, new FlatGrassGen());
        //World world = new World(universe, new SpaceGen());

        int levelIndex = Logging.registerLoggingLevel(Logging.DEFAULT_LOGGING_LEVEL);
        mainScheduler = new SystemScheduler(3, new Logging.Context("main", levelIndex));

        mainScheduler.schedule(() ->
        {
            mainSchedulerProfiler.start();

            universe.tick();

            clientApp.runClientSystems();
            clientApp.render();

            tick++;

            mainSchedulerProfiler.end();
        });

        mainScheduler.tickAtFixedRate(60);

        /*
         * Has to be last due to spawning a while (true) loop
         */
        LOGGER.info("Starting debugger");
        clientApp.startDebug(universe, mainSchedulerProfiler);
    }

    public static void main(String[] args) throws IOException, URISyntaxException
    {
        java.lang.System.setProperty("dominion.world.size", "LARGE");
        java.lang.System.setProperty("dominion.show-banner", "false");
        new Main();
    }
}
