package steve6472.planetoid.example;

import steve6472.core.util.RandomUtil;
import steve6472.planetoid.PlanetoidApp;
import steve6472.planetoid.PlanetoidConstants;
import steve6472.planetoid.SystemEntry;
import steve6472.planetoid.event.PlanetoidEvents;
import steve6472.planetoid.world.World;

/**
 * Created by steve6472
 * Date: 3/23/2025
 * Project: Planetoid <br>
 */
final class TestUniverseSystems
{
    public static void register()
    {
        PlanetoidEvents.CREATE_UNIVERSE_SYSTEMS.addListener(systems ->
        {
            systems.registerSystem(SystemEntry.of(PlanetoidConstants.key("random_direction"), TestUniverseSystems::randomDirectionSystem));
            systems.registerSystem(SystemEntry.of(PlanetoidConstants.key("move"), TestUniverseSystems::moveSystem));
            systems.registerSystem(SystemEntry.of(PlanetoidConstants.key("keep_in_bounds"), new KeepInBoundsSystem()));
        });
    }

    private static void randomDirectionSystem(World world)
    {
        if (PlanetoidApp.tick % 3 != 0)
            return;

        var entities = world.ecs().findEntitiesWith(Direction.class);
        entities.forEach(entityData ->
        {
            Direction dir = entityData.comp();
            double ang = Math.PI / 10d;
            dir.angle += RandomUtil.randomDouble(-ang, ang);
        });
    }

    private static void moveSystem(World world)
    {
        final float SPEED = 0.3f;
        var entities = world.ecs().findEntitiesWith(Direction.class, Position.class);
        entities.forEach(entityData ->
        {
            Direction dir = entityData.comp1();
            Position pos = entityData.comp2();
            pos.x += (float) Math.sin(dir.angle) * SPEED;
            pos.y += (float) Math.cos(dir.angle) * SPEED;
        });
    }
}
