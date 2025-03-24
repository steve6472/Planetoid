package steve6472.planetoid;

import com.mojang.datafixers.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Created by steve6472
 * Date: 6/30/2024
 * Project: Domin <br>
 */
public class PlanetoidScheduler
{
    private static final List<Pair<Long, Runnable>> delayedTasks = new ArrayList<>();
    private static final List<Supplier<Boolean>> repeatingTasks = new ArrayList<>();

    private static final List<Supplier<Boolean>> newRepeatingTasks = new ArrayList<>();

    private PlanetoidScheduler() {}

    public static void scheduleDelayedTask(final long delay, Runnable runnable)
    {
        final long tck = PlanetoidApp.tick;
        delayedTasks.add(new Pair<>(tck + delay, runnable));
    }

    public static void scheduleRepeatingTask(Supplier<Boolean> task)
    {
        newRepeatingTasks.add(task);
    }

    public static void tick()
    {
        if (!delayedTasks.isEmpty())
        {
            boolean hasNext = true;
            while (hasNext)
            {
                if (delayedTasks.isEmpty())
                    break;

                Pair<Long, Runnable> longProcedurePair = delayedTasks.getFirst();
                if (longProcedurePair.getFirst() <= PlanetoidApp.tick)
                {
                    delayedTasks.removeFirst();
                    longProcedurePair.getSecond().run();
                } else
                {
                    hasNext = false;
                }
            }
        }

        repeatingTasks.addAll(newRepeatingTasks);
        newRepeatingTasks.clear();
        repeatingTasks.removeIf(Supplier::get);
        repeatingTasks.addAll(newRepeatingTasks);
        newRepeatingTasks.clear();
    }
}
