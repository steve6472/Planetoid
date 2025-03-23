package steve6472.planetoid;

import org.jetbrains.annotations.NotNull;
import steve6472.planetoid.world.World;

import java.util.Objects;

/**
 * Created by steve6472
 * Date: 3/24/2025
 * Project: Planetoid <br>
 */
public abstract class PlanetoidAppQuick extends PlanetoidApp
{
    protected abstract void registerEvents();

    /// @return The return value will be used to set the initial world
    protected abstract @NotNull World createWorlds();
    protected abstract void preLoop();

    public void start(String title, int width, int height, int pixelSize, boolean debug)
    {
        disableDominionLog();
        init();
        setWindowSize(width, height, pixelSize);

        registerEvents();

        createFrame(title);

        letThereBeLight();

        World world = createWorlds();
        Objects.requireNonNull(world, "Returned world from createWorlds() can NOT be null!");
        setWorld(world);

        preLoop();
        startLoop();

        if (debug)
            startDebugger();
    }
}
