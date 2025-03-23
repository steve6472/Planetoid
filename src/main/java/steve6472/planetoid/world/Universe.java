package steve6472.planetoid.world;

import steve6472.core.log.Log;
import steve6472.core.registry.Key;
import steve6472.planetoid.Systems;
import steve6472.planetoid.event.PlanetoidEvents;

import java.util.*;
import java.util.logging.Logger;

/**
 * Created by steve6472
 * Date: 2/28/2024
 * Project: Domin <br>
 */
public class Universe
{
    private static final Logger LOGGER = Log.getLogger(Universe.class);

    public final Systems systems;
    public final Map<Key, World> worlds;

    public Universe()
    {
        systems = new Systems();
        worlds = new LinkedHashMap<>();
        createSystems();
    }

    /// Uses the Universe systems
    public World createWorld(Key key)
    {
        if (worlds.containsKey(key))
            throw new RuntimeException("Tried to create World with duplicate key '%s'".formatted(key));
        World world = new World(key, systems.createCopy(), this);
        worlds.put(key, world);
        return world;
    }

    /// Simply copies systems instad of adding the Universe systems.
    /// This allows for disabling World systems without affecting the Universe
    public World createSpecialWorld(Key key)
    {
        if (worlds.containsKey(key))
            throw new RuntimeException("Tried to create special World with duplicate key '%s'".formatted(key));
        World world = new World(key, systems.createSpecialCopy(), this);
        worlds.put(key, world);
        return world;
    }

    public void tick()
    {
        worlds.forEach((_, world) -> world.tick());
    }

    /*
     * Systems
     */

    public void runWorldSystems(World world)
    {
        synchronized (world.ecs())
        {
            systems.run();
        }
    }

    private void createSystems()
    {
        LOGGER.finer("Creating World Systems");
        PlanetoidEvents.CREATE_WORLD_SYSTEMS.trigger(systems);
        PlanetoidEvents.CREATE_WORLD_SYSTEMS.printLast(LOGGER, "Created World Systems");
    }
}
