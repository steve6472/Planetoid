package steve6472.planetoid;

import steve6472.core.registry.Key;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by steve6472
 * Date: 3/24/2025
 * Project: Planetoid <br>
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ComponentSystem
{
    /// Has to be parse to a valid [Key]
    String value();

    Class<?> type() default Void.class;
}
