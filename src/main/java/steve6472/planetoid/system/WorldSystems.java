package steve6472.planetoid.system;

import steve6472.planetoid.Systems;
import steve6472.planetoid.world.World;

/**
 * Created by steve6472
 * Date: 3/23/2025
 * Project: Planetoid <br>
 */
public class WorldSystems extends Systems<WorldSystem>
{
    private final World world;

    public WorldSystems(World world)
    {
        this.world = world;
    }

    @Override
    protected void run(WorldSystem system)
    {
        system.run(world);
    }
}
