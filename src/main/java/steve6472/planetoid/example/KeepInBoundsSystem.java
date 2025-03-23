package steve6472.planetoid.example;

import steve6472.planetoid.system.WorldSystem;
import steve6472.planetoid.world.World;

/**
 * Created by steve6472
 * Date: 3/23/2025
 * Project: Planetoid <br>
 */
class KeepInBoundsSystem implements WorldSystem
{
    @Override
    public void run(World world)
    {
        final int BORDER_OFFSET = 4;
        var entities = world.ecs().findEntitiesWith(Direction.class, Position.class);
        entities.forEach(entityData ->
        {
            Direction dir = entityData.comp1();
            Position pos = entityData.comp2();

            if (pos.x < BORDER_OFFSET || pos.x > 128 - BORDER_OFFSET)
                dir.angle = -dir.angle;

            if (pos.y < BORDER_OFFSET || pos.y > 128 - BORDER_OFFSET)
                dir.angle = Math.PI - dir.angle;
        });
    }
}
