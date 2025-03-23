package steve6472.planetoid.world;

import dev.dominion.ecs.api.Dominion;
import dev.dominion.ecs.api.Entity;
import steve6472.planetoid.Systems;
import steve6472.core.log.Log;
import steve6472.core.registry.Key;
import steve6472.core.util.Profiler;

import java.util.logging.Logger;

/**
 * Created by steve6472
 * Date: 2/28/2024
 * Project: Domin <br>
 */
public class World
{
    private static final Logger LOGGER = Log.getLogger(World.class);

    public final Universe universe;
    public final Key key;
    public final Systems systems;

    private final Dominion worldEcs;

    World(Key key, Systems systems, Universe universe)
    {
        this.key = key;
        this.systems = systems;
        this.universe = universe;

        worldEcs = Dominion.create(key.toString());
    }

    public Dominion ecs()
    {
        return worldEcs;
    }

    public void tick()
    {
        systems.run();
    }

    public Profiler profiler()
    {
        return systems.profiler;
    }

    public Entity spawnEntity(Object[] components)
    {
        Entity entity = worldEcs.createEntity(components);

        // TODO: spawn entity event

        return entity;
    }
}
