package steve6472.planetoid.event;

import com.pploder.events.Event;
import com.pploder.events.SimpleEvent;
import steve6472.planetoid.Systems;
import steve6472.planetoid.aimguihax.Debug;

/**
 * Created by steve6472
 * Date: 3/22/2025
 * Project: Planetoid <br>
 */
public class PlanetoidEvents
{
    /*
     * Setup
     */
    public static final ProfiledEvent<Void> LOAD_MODULES = new ProfiledEvent<>(1);
    public static final ProfiledEvent<Systems> CREATE_RENDER_SYSTEMS = new ProfiledEvent<>(1);
    public static final ProfiledEvent<Systems> CREATE_WORLD_SYSTEMS = new ProfiledEvent<>(1);
    public static final Event<Debug> SETUP_DEBUG = new SimpleEvent<>();

    /*
     * Running
     */
    /// Event that runs at the start of each frame
    public static final ProfiledEvent<Void> TICK_PRE = new ProfiledEvent<>();
    /// Event that runs at the end of each frame
    public static final ProfiledEvent<Void> TICK_POST = new ProfiledEvent<>();

    /// Event that runs each frame (before render systems)
    public static final ProfiledEvent<Double> FRAME_PRE = new ProfiledEvent<>();
    /// Event that runs each frame (after render systems)
    public static final ProfiledEvent<Double> FRAME_POST = new ProfiledEvent<>();

}
