package steve6472.planetoid.event;

import dev.dominion.ecs.api.Entity;
import steve6472.planetoid.world.World;

/**
 * Created by steve6472
 * Date: 3/23/2025
 * Project: Planetoid <br>
 */
public final class EntitySpawnEvent extends BaseCancellableEvent
{
    private final World world;
    private final Entity entity;

    public EntitySpawnEvent(World world, Entity entity)
    {
        this.world = world;
        this.entity = entity;
    }

    public World world()
    {
        return world;
    }

    public Entity entity()
    {
        return entity;
    }

    @Override
    public String toString()
    {
        return "EntitySpawnEvent[" + "world=" + world + ", " + "entity=" + entity + ']';
    }
}
