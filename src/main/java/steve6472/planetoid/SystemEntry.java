package steve6472.planetoid;

import steve6472.core.registry.Key;
import steve6472.core.util.Profiler;

import java.util.Locale;

/**
 * Created by steve6472
 * Date: 2/25/2024
 * Project: Domin <br>
 */
public class SystemEntry
{
    public Runnable system;
    public boolean enabled;
    public boolean debug;

    public final Key key;
    public String name;
    public String description;

    public Profiler profiler = new Profiler(15);

    private SystemEntry(Key key)
    {
        this.key = key;
    }

    public SystemEntry copy()
    {
        return of(key, debug, description, enabled, system);
    }

    /*
     *
     */

    public static SystemEntry of(Key key, Runnable system)
    {
        return of(key, "", system);
    }

    public static SystemEntry of(Key key, String description, Runnable system)
    {
        return of(key, false, description, true, system);
    }

    public static SystemEntry ofDebug(Key key, String description, Runnable system)
    {
        return of(key, true, description, true, system);
    }

    public static SystemEntry ofDebug(Key key, boolean enabled, String description, Runnable system)
    {
        return of(key, true, description, enabled, system);
    }

    public static SystemEntry of(Key key, boolean debug, String description, boolean enabled, Runnable system)
    {
        SystemEntry entry = new SystemEntry(key);
        entry.system = system;
        entry.enabled = enabled;
        entry.debug = debug;
        entry.name = key.id().toLowerCase(Locale.ROOT).replace('_', ' ');
        entry.description = description;
        return entry;
    }
}
