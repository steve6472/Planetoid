package steve6472.planetoid.event;

/**
 * Created by steve6472
 * Date: 3/23/2025
 * Project: Planetoid <br>
 */
public interface Cancellable
{
    boolean isCancelled();
    void setCancelled(boolean cancel);
}
