package steve6472.planetoid.event;

import com.pploder.events.Event;
import com.pploder.events.SimpleEvent;
import steve6472.planetoid.Systems;
import steve6472.planetoid.aimguihax.Debug;
import steve6472.planetoid.system.RenderSystems;
import steve6472.planetoid.world.World;
import steve6472.planetoid.system.WorldSystems;

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
    public static final Event<RenderSystems> CREATE_RENDER_SYSTEMS = new SimpleEvent<>();
    public static final Event<WorldSystems> CREATE_UNIVERSE_SYSTEMS = new SimpleEvent<>();
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

    /// Run when a new world is created
    public static final Event<World> WORLD_CREATED = new SimpleEvent<>();

    /// Run when a new world is created
    public static final Event<EntitySpawnEvent> ENTITY_SPAWN = new SimpleEvent<>();

}
