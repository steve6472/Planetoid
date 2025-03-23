package steve6472.planetoid.system;

import steve6472.planetoid.api.Render;
import steve6472.planetoid.world.World;

/**
 * Created by steve6472
 * Date: 3/23/2025
 * Project: Planetoid <br>
 */
@FunctionalInterface
public interface RenderSystem
{
    void run(Render render, World currentWorld);
}
