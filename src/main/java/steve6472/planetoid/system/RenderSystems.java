package steve6472.planetoid.system;

import steve6472.planetoid.Systems;
import steve6472.planetoid.api.Render;
import steve6472.planetoid.world.World;

/**
 * Created by steve6472
 * Date: 3/23/2025
 * Project: Planetoid <br>
 */
public class RenderSystems extends Systems<RenderSystem>
{
    private final Render render;
    private World currentWorld;

    public RenderSystems(Render render)
    {
        this.render = render;
    }

    public void setWorld(World world)
    {
        currentWorld = world;
    }

    @Override
    protected void run(RenderSystem system)
    {
        system.run(render, currentWorld);
    }
}
