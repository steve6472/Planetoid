package steve6472.planetoid.event;

import com.pploder.events.SimpleEvent;
import steve6472.core.util.Profiler;

import java.util.logging.Logger;

/**
 * Created by steve6472
 * Date: 3/23/2025
 * Project: Planetoid <br>
 */
public class ProfiledEvent<T> extends SimpleEvent<T>
{
    private final Profiler profiler;

    public ProfiledEvent()
    {
        profiler = new Profiler(10);
    }

    public ProfiledEvent(int measurementsToAverage)
    {
        profiler = new Profiler(measurementsToAverage);
    }

    public Profiler profiler()
    {
        return profiler;
    }

    @Override
    public void trigger(T t)
    {
        profiler.start();
        super.trigger(t);
        profiler.end();
    }

    public void printLast(Logger logger, String prefix)
    {
        logger.fine("%s in: %s ms".formatted(prefix, profiler().lastMilli()));
    }
}
