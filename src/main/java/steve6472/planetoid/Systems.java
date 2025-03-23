package steve6472.planetoid;

import steve6472.core.log.Log;
import steve6472.core.registry.Key;
import steve6472.core.util.Profiler;

import java.util.*;
import java.util.logging.Logger;

/**
 * Created by steve6472
 * Date: 3/1/2024
 * Project: Domin <br>
 */
public class Systems
{
    private static final Logger LOGGER = Log.getLogger(Systems.class);
    public final Map<Key, SystemEntry> systemEntries = new LinkedHashMap<>();
    public Profiler profiler = new Profiler(15);

    public void run()
    {
        runSystems();
    }

    private void runSystems()
    {
        profiler.start();
        for (SystemEntry entry : systemEntries.values())
        {
            if (!entry.enabled)
                continue;

            entry.profiler.start();
            try
            {
                entry.system.run();
            } catch (Exception exception)
            {
                LOGGER.severe("System '%s' threw an error: %s".formatted(entry.key, exception.getMessage()));
                exception.printStackTrace();
                // TODO: throw out info about the system that crashed, close app (if setting is set)
            }
            entry.profiler.end();
        }
        profiler.end();
    }

    public Systems createCopy()
    {
        Systems systems = new Systems();
        systems.systemEntries.putAll(systemEntries);
        return systems;
    }

    public Systems createSpecialCopy()
    {
        Systems systems = new Systems();
        systemEntries.forEach((key, value) -> {
            systems.systemEntries.put(key, value.copy());
        });
        return systems;
    }

    public SystemEntry getSystem(Key key)
    {
        return systemEntries.get(key);
    }

    public void registerSystem(SystemEntry system)
    {
        systemEntries.put(system.key, system);
    }

    public void registerSystemBefore(Key before, SystemEntry system)
    {
        Map<Key, SystemEntry> copy = new LinkedHashMap<>(systemEntries);
        systemEntries.clear();
        copy.forEach((key, value) -> {
            if (key.equals(before))
            {
                systemEntries.put(system.key, system);
            }
            systemEntries.put(key, value);
        });
    }

    public void registerSystemAfter(Key before, SystemEntry system)
    {
        Map<Key, SystemEntry> copy = new LinkedHashMap<>(systemEntries);
        systemEntries.clear();
        copy.forEach((key, value) -> {
            systemEntries.put(key, value);
            if (key.equals(before))
            {
                systemEntries.put(system.key, system);
            }
        });
    }
}
