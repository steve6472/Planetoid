package steve6472.planetoid.sound;

import java.io.File;

import kuusisto.tinysound.Sound;
import kuusisto.tinysound.TinySound;
import steve6472.core.registry.Key;
import steve6472.core.registry.Keyable;

/**
 * Created by steve6472
 * Date: 6/18/2024
 * Project: Domin <br>
 */
public record GameSound(Key key, Sound sound) implements Keyable
{
    public GameSound(Key key, File file)
    {
        this(key, file == null ? null : TinySound.loadSound(file));
    }

    public boolean soundExists()
    {
        return sound != null;
    }

    public static void init()
    {
        TinySound.init();
    }

    public static double getGlobalVolume()
    {
        return TinySound.getGlobalVolume();
    }

    public static void setGlobalVolume(double volume)
    {
        TinySound.setGlobalVolume(volume);
    }

    public void play()
    {
        sound.play();
    }

    public void play(double volume)
    {
        sound.play();
//        double globalVolume = getGlobalVolume();
//        sound.play(volume * getGlobalVolume());
//        setGlobalVolume(globalVolume);
    }
}