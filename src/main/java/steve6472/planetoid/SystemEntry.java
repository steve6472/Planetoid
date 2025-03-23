package steve6472.planetoid;

import steve6472.core.registry.Key;
import steve6472.core.util.Profiler;

import java.util.Locale;

/**
 * Created by steve6472
 * Date: 2/25/2024
 * Project: Domin <br>
 */
public class SystemEntry<T>
{
    public T system;
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

    public SystemEntry<T> copy()
    {
        return of(key, debug, description, enabled, system);
    }

    /*
     *
     */

    public static <T> SystemEntry<T> of(Key key, T system)
    {
        return of(key, "", system);
    }

    public static <T> SystemEntry<T> of(Key key, String description, T system)
    {
        return of(key, false, description, true, system);
    }

    public static <T> SystemEntry<T> ofDebug(Key key, String description, T system)
    {
        return of(key, true, description, true, system);
    }

    public static <T> SystemEntry<T> ofDebug(Key key, boolean enabled, String description, T system)
    {
        return of(key, true, description, enabled, system);
    }

    public static <T> SystemEntry<T> of(Key key, boolean debug, String description, boolean enabled, T system)
    {
        SystemEntry<T> entry = new SystemEntry<>(key);
        entry.system = system;
        entry.enabled = enabled;
        entry.debug = debug;
        entry.name = key.id().toLowerCase(Locale.ROOT).replace('_', ' ');
        entry.description = description;
        return entry;
    }

    @Override
    public String toString()
    {
        return "SystemEntry{" + "enabled=" + enabled + ", debug=" + debug + ", key=" + key + ", name='" + name + '\'' + ", description='" + description + '\'' + ", profiler=" + profiler + '}';
    }
}
