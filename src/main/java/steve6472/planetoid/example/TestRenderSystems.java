package steve6472.planetoid.example;

import steve6472.planetoid.PlanetoidConstants;
import steve6472.planetoid.SystemEntry;
import steve6472.planetoid.api.Render;
import steve6472.planetoid.event.PlanetoidEvents;
import steve6472.planetoid.world.World;

/**
 * Created by steve6472
 * Date: 3/23/2025
 * Project: Planetoid <br>
 */
public class TestRenderSystems
{
    public static void register()
    {
        PlanetoidEvents.CREATE_RENDER_SYSTEMS.addListener(systems ->
        {
            systems.registerSystem(SystemEntry.of(PlanetoidConstants.key("render"), TestRenderSystems::render));
            systems.registerSystem(SystemEntry.of(PlanetoidConstants.key("render_direction"), TestRenderSystems::renderDirection));
        });
    }

    private static void render(Render render, World world)
    {
        var entities = world.ecs().findEntitiesWith(Position.class);
        entities.forEach(entityData ->
        {
            Position pos = entityData.comp();
            render.fillRectangle(pos.x - 2, pos.y - 2, 5, 5, 0xffff00ff);
        });
    }

    private static void renderDirection(Render render, World world)
    {
        var entities = world.ecs().findEntitiesWith(Direction.class, Position.class);
        entities.forEach(entityData ->
        {
            final float DISTANCE = 8f;
            Direction dir = entityData.comp1();
            Position pos = entityData.comp2();
            float x = pos.x + (float) Math.sin(dir.angle) * DISTANCE;
            float y = pos.y + (float) Math.cos(dir.angle) * DISTANCE;
            render.setPixel((int) x, (int) y, 0xff0000ff);
        });
    }
}
