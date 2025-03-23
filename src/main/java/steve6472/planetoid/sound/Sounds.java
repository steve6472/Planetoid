package steve6472.planetoid.sound;

import steve6472.planetoid.PlanetoidConstants;
import steve6472.core.log.Log;
import steve6472.planetoid.PlanetoidRegistries;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by steve6472
 * Date: 6/18/2024
 * Project: Domin <br>
 */
public class Sounds
{
    private static final Logger LOGGER = Log.getLogger(Sounds.class);
//    private static final String PATH = Main.BASE_NAMESPACE + "/sound/";
    private static final String PATH = "sound/";

    public static GameSound bootstrap()
    {
        GameSound[] sounds;
        try
        {
            File soundsFile = new File(PATH);
            String[] resourceListing = soundsFile.list();
            sounds = createSounds(resourceListing, PATH, "");
        } catch (URISyntaxException | IOException e)
        {
            throw new RuntimeException(e);
        }

        if (sounds.length > 0)
            return sounds[0];

        return new GameSound(null, (File) null);
    }

    private static GameSound[] createSounds(String[] resources, String totalPath, String extraPath) throws URISyntaxException, IOException
    {
        List<GameSound> sounds = new ArrayList<>();

        if (resources == null)
            return sounds.toArray(new GameSound[0]);

        for (String s : resources)
        {
            String newPath = totalPath + s + "/";
            String nexExtraPath = extraPath + (extraPath.isEmpty() ? "" : "/") + s;

            if (!s.endsWith("wav"))
            {
                Collections.addAll(sounds, createSounds(new File(newPath).list(), newPath, nexExtraPath));
            } else
            {
                GameSound sound = createComponentType(newPath.substring(0, newPath.length() - 1), nexExtraPath);
                if (sound != null)
                    sounds.add(sound);
            }
        }

        return sounds.toArray(new GameSound[0]);
    }

    private static GameSound createComponentType(String pathUrl, String extraPath)
    {
        extraPath = extraPath.substring(0, extraPath.length() - ".wav".length());
        GameSound gameSound = new GameSound(PlanetoidConstants.key(extraPath), new File(pathUrl));
        if (!gameSound.soundExists())
        {
            LOGGER.warning("Sound " + extraPath + " from " + pathUrl + " could not be loaded!");
            return null;
        }
        LOGGER.finest("Loaded Sound: " + pathUrl + " (" + gameSound.key() + ")");
        PlanetoidRegistries.SOUND.register(gameSound);
        return gameSound;
    }
}
