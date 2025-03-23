package steve6472.planetoid.aimguihax.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.RECORD_COMPONENT, ElementType.FIELD})
public @interface ConfIntSlider 
{
    int min() default 0;
    int max() default 100;
    int step() default 1;
}
