package steve6472.planetoid.example;

import steve6472.core.util.RandomUtil;
import steve6472.planetoid.ComponentSystem;
import steve6472.planetoid.PlanetoidApp;
import steve6472.planetoid.api.Render;
import steve6472.planetoid.world.World;

/**
 * Created by steve6472
 * Date: 3/24/2025
 * Project: Planetoid <br>
 */
public class TestMixedSystems
{
    @ComponentSystem("planetoid:render_center")
    private static void render(Render render, World world)
    {
        var entities = world.ecs().findEntitiesWith(Position.class);
        entities.forEach(entityData ->
        {
            Position pos = entityData.comp();
            render.setPixel((int) pos.x, (int) pos.y, 0xffff0000);
        });
    }

    @ComponentSystem("planetoid:random_speed")
    private static void randomSpeedSystem(World world)
    {
        if (PlanetoidApp.tick % 20 != 0)
            return;

        var entities = world.ecs().findEntitiesWith(Speed.class);
        entities.forEach(entityData ->
        {
            Speed speed = entityData.comp();
            speed.value = Math.clamp(speed.value + RandomUtil.randomDouble(-0.05, 0.05), 0.2, 1.0);
        });
    }
}
