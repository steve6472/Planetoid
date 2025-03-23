package steve6472.planetoid.aimguihax.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.RECORD_COMPONENT, ElementType.FIELD})
public @interface ConfFloatSlider 
{
    float min() default 0;
    float max() default 100;
    float step() default 1;
}
