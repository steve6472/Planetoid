package test;

import steve6472.planetoid.PlanetoidApp;

/**
 * Created by steve6472
 * Date: 3/22/2025
 * Project: Planetoid <br>
 */
class Test extends PlanetoidApp
{
    private Test()
    {
        disableDominionLog();
        init();
        setWindowSize(128, 128, 7);
        createFrame("Test Frame");
        startLoop();

        letThereBeLight();

        startDebugger();
    }

    public static void main(String[] args)
    {
        Test test = new Test();
    }
}
