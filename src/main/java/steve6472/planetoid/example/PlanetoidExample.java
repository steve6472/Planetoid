package steve6472.planetoid.example;

import steve6472.planetoid.PlanetoidApp;
import steve6472.planetoid.PlanetoidConstants;
import steve6472.planetoid.world.World;

/**
 * Created by steve6472
 * Date: 3/22/2025
 * Project: Planetoid <br>
 */
class PlanetoidExample extends PlanetoidApp
{
    private PlanetoidExample()
    {
        disableDominionLog();
        init();
        setWindowSize(128, 128, 7);

        TestRenderSystems.register();
        TestUniverseSystems.register();

        createFrame("Planetoid Example");

        letThereBeLight();

        World testWorld = universe().createWorld(PlanetoidConstants.key("example"));
        testWorld.spawnEntity(new Position(64, 64), new Direction());
        testWorld.spawnEntity(new Position(64, 64), new Direction());
        testWorld.spawnEntity(new Position(64, 64), new Direction());
        testWorld.spawnEntity(new Position(64, 64), new Direction());
        testWorld.spawnEntity(new Position(64, 64), new Direction());
        testWorld.spawnEntity(new Position(64, 64), new Direction());
        testWorld.spawnEntity(new Position(64, 64), new Direction());
        setWorld(testWorld);

        startLoop();
        startDebugger();
    }

    public static void main(String[] args)
    {
        PlanetoidExample test = new PlanetoidExample();
    }
}
