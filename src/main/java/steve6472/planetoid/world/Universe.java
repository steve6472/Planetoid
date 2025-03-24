package steve6472.planetoid.world;

import steve6472.core.log.Log;
import steve6472.core.registry.Key;
import steve6472.planetoid.event.PlanetoidEvents;
import steve6472.planetoid.system.WorldSystems;

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

    private final Map<Key, World> worlds;
    public final WorldSystems systems;

    public Universe()
    {
        systems = new WorldSystems(null);
        worlds = new LinkedHashMap<>();
        createSystems();
    }

    /// Uses the Universe systems
    public World createWorld(Key key)
    {
        if (worlds.containsKey(key))
            throw new RuntimeException("Tried to create World with duplicate key '%s'".formatted(key));
        World world = new World(key, systems, false, this);
        worlds.put(key, world);
        return world;
    }

    /// Simply copies systems instad of adding the Universe systems.
    /// This allows for disabling World systems without affecting the Universe
    public World createSpecialWorld(Key key)
    {
        if (worlds.containsKey(key))
            throw new RuntimeException("Tried to create special World with duplicate key '%s'".formatted(key));
        World world = new World(key, systems, true, this);
        worlds.put(key, world);
        return world;
    }

    public World getWorld(Key key)
    {
        return worlds.get(key);
    }

    public void tick()
    {
        worlds.forEach((_, world) -> world.tick());
    }

    private void createSystems()
    {
        LOGGER.finer("Creating Universe Systems");
        PlanetoidEvents.CREATE_UNIVERSE_SYSTEMS.trigger(systems);
    }
}
