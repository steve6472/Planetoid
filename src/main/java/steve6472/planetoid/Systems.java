package steve6472.planetoid;

import steve6472.core.log.Log;
import steve6472.core.registry.Key;
import steve6472.core.util.Profiler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.logging.Logger;

/**
 * Created by steve6472
 * Date: 3/1/2024
 * Project: Domin <br>
 */
public abstract class Systems<T>
{
    private static final Logger LOGGER = Log.getLogger(Systems.class);
    public final Map<Key, SystemEntry<T>> systemEntries = new LinkedHashMap<>();
    public Profiler profiler = new Profiler(15);

    protected abstract void run(T system);

    public void runSystems()
    {
        profiler.start();
        for (SystemEntry<T> entry : systemEntries.values())
        {
            if (!entry.enabled)
                continue;

            entry.profiler.start();
            try
            {
                run(entry.system);
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

    public void fillFrom(Systems<T> systems, boolean copyEntries)
    {
        if (copyEntries)
        {
            systemEntries.putAll(systems.systemEntries);
        } else
        {
            systems.systemEntries.forEach((key, value) -> systemEntries.put(key, value.copy()));
        }
    }

    public SystemEntry<T> getSystem(Key key)
    {
        return systemEntries.get(key);
    }

    public void registerSystem(SystemEntry<T> system)
    {
        systemEntries.put(system.key, system);
    }

    public static void wrapped(Object[] objs, Object... args)
    {
        try
        {
            ((Method) objs[0]).invoke(null, args);
        } catch (IllegalAccessException | InvocationTargetException e)
        {
            throw new RuntimeException(e);
        }
    }

    public void registerSystems(Class<?> clazz, Function<Object[], T> wrapper)
    {
        T typeDummyInstance = wrapper.apply(null);
        Method[] methods = typeDummyInstance.getClass().getDeclaredMethods();
        if (methods.length == 0)
            throw new RuntimeException("System has no methods to run!");
        Method interfaceMethod = methods[0];
        Class<?>[] typeParameterTypes = interfaceMethod.getParameterTypes();

        Method[] declaredMethods = clazz.getDeclaredMethods();
        for (Method declaredMethod : declaredMethods)
        {
            declaredMethod.setAccessible(true);
            ComponentSystem annotation = declaredMethod.getAnnotation(ComponentSystem.class);
            if (annotation == null)
                continue;

            Class<?>[] methodParameterTypes = declaredMethod.getParameterTypes();
            if (!Arrays.equals(typeParameterTypes, methodParameterTypes))
                continue;

            if (annotation.type() != Void.class && !annotation.type().equals(typeDummyInstance.getClass()))
                continue;

            String value = annotation.value();
            Key key;
            if (value.contains(":"))
            {
                String[] split = value.split(":");
                key = Key.withNamespace(split[0], split[1]);
            } else
            {
                key = Key.defaultNamespace(value);
            }

            Object[] arr = {declaredMethod};
            T apply = wrapper.apply(arr);
            registerSystem(SystemEntry.of(key, apply));
        }
    }

    public void registerSystemBefore(Key before, SystemEntry<T> system)
    {
        Map<Key, SystemEntry<T>> copy = new LinkedHashMap<>(systemEntries);
        systemEntries.clear();
        copy.forEach((key, value) -> {
            if (key.equals(before))
            {
                systemEntries.put(system.key, system);
            }
            systemEntries.put(key, value);
        });
    }

    public void registerSystemAfter(Key before, SystemEntry<T> system)
    {
        Map<Key, SystemEntry<T>> copy = new LinkedHashMap<>(systemEntries);
        systemEntries.clear();
        copy.forEach((key, value) -> {
            systemEntries.put(key, value);
            if (key.equals(before))
            {
                systemEntries.put(system.key, system);
            }
        });
    }

    @Override
    public String toString()
    {
        return "Systems{" + "systemEntries=" + systemEntries + ", profiler=" + profiler + '}';
    }
}
