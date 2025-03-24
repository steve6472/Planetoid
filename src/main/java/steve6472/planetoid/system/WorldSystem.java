package steve6472.planetoid.system;

import steve6472.planetoid.Systems;
import steve6472.planetoid.world.World;

import java.util.function.Function;

/**
 * Created by steve6472
 * Date: 3/23/2025
 * Project: Planetoid <br>
 */
@FunctionalInterface
public interface WorldSystem
{
    Function<Object[], WorldSystem> WRAPPER = (objs) -> a -> Systems.wrapped(objs, a);

    void run(World world);
}
