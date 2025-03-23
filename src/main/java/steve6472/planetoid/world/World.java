package steve6472.planetoid.world;

import dev.dominion.ecs.api.Dominion;
import dev.dominion.ecs.api.Entity;
import steve6472.core.log.Log;
import steve6472.core.registry.Key;
import steve6472.core.util.Profiler;
import steve6472.planetoid.event.EntitySpawnEvent;
import steve6472.planetoid.event.PlanetoidEvents;
import steve6472.planetoid.system.WorldSystems;

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
    public final WorldSystems systems;

    private final Dominion worldEcs;

    World(Key key, WorldSystems copyFrom, boolean copyEntries, Universe universe)
    {
        this.key = key;

        this.systems = new WorldSystems(this);
        if (copyFrom != null)
        {
            systems.fillFrom(copyFrom, copyEntries);
        }

        this.universe = universe;

        worldEcs = Dominion.create(key.toString());

        PlanetoidEvents.WORLD_CREATED.trigger(this);
    }

    public Dominion ecs()
    {
        return worldEcs;
    }

    public void tick()
    {
        systems.runSystems();
    }

    public Profiler profiler()
    {
        return systems.profiler;
    }

    public Entity spawnEntity(Object... components)
    {
        Entity entity = worldEcs.createEntity(components);

        // TODO: cancellable event
        EntitySpawnEvent event = new EntitySpawnEvent(this, entity);
        PlanetoidEvents.ENTITY_SPAWN.trigger(event);
        if (event.isCancelled())
        {
            worldEcs.deleteEntity(entity);
            return null;
        }

        return entity;
    }
}
