package steve6472.planetoid;

import steve6472.core.registry.*;
import steve6472.planetoid.sound.GameSound;
import steve6472.planetoid.sound.Sounds;

/**
 * Created by steve6472
 * Date: 5/4/2024
 * Project: Domin <br>
 */
public class PlanetoidRegistries extends RegistryRegister
{
    public static final ObjectRegistry<GameSound> SOUND = createObjectRegistry("sound", Sounds::bootstrap);
}