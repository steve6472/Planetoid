package steve6472.planetoid.example;

import org.jetbrains.annotations.NotNull;
import steve6472.core.util.RandomUtil;
import steve6472.planetoid.PlanetoidApp;
import steve6472.planetoid.PlanetoidAppQuick;
import steve6472.planetoid.PlanetoidConstants;
import steve6472.planetoid.Systems;
import steve6472.planetoid.event.PlanetoidEvents;
import steve6472.planetoid.system.RenderSystem;
import steve6472.planetoid.system.WorldSystem;
import steve6472.planetoid.world.World;

import java.util.function.Function;

/**
 * Created by steve6472
 * Date: 3/22/2025
 * Project: Planetoid <br>
 */
class PlanetoidExampleQuick extends PlanetoidAppQuick
{
    @Override
    protected void registerEvents()
    {
        PlanetoidEvents.CREATE_RENDER_SYSTEMS.addListener(systems -> systems.registerSystems(TestRenderSystems.class, RenderSystem.WRAPPER));

        PlanetoidEvents.CREATE_RENDER_SYSTEMS.addListener(systems -> systems.registerSystems(TestMixedSystems.class, RenderSystem.WRAPPER));
        PlanetoidEvents.CREATE_UNIVERSE_SYSTEMS.addListener(systems -> systems.registerSystems(TestMixedSystems.class, WorldSystem.WRAPPER));

        TestUniverseSystems.register();
    }

    @Override
    protected @NotNull World createWorlds()
    {
        return universe().createWorld(PlanetoidConstants.key("example_quick"));
    }

    @Override
    protected void preLoop()
    {
        for (int i = 0; i < 16; i++)
        {
            getCurrentWorld().spawnEntity(new Position(64, 64), new Direction(RandomUtil.randomRadian()), new Speed(0.3));
        }
    }

    public static void main(String[] args)
    {
        new PlanetoidExampleQuick().start("Planetoid Example Quick", 128, 128, 3, false);
    }
}
