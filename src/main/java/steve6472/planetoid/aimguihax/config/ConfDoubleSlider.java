package steve6472.planetoid.aimguihax.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.RECORD_COMPONENT, ElementType.FIELD})
public @interface ConfDoubleSlider 
{
    double min() default 0;
    double max() default 100;
    double step() default 1;
}
