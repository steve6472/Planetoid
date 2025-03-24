package steve6472.planetoid.system;

import steve6472.planetoid.Systems;
import steve6472.planetoid.api.Render;
import steve6472.planetoid.world.World;

import java.util.function.Function;

/**
 * Created by steve6472
 * Date: 3/23/2025
 * Project: Planetoid <br>
 */
@FunctionalInterface
public interface RenderSystem
{
    Function<Object[], RenderSystem> WRAPPER = (objs) -> (a, b) -> Systems.wrapped(objs, a, b);

    void run(Render render, World currentWorld);
}
