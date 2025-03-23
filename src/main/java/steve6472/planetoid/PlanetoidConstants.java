package steve6472.planetoid;

import steve6472.core.registry.Key;

import java.util.regex.Pattern;

/**
 * Created by steve6472
 * Date: 5/19/2024
 * Project: Domin <br>
 */
public final class PlanetoidConstants
{
    public static final String VERSION = "0.1.0";
    public static final String NAMESPACE = "planetoid";

    public static final Pattern ID_MATCH = Pattern.compile("[a-z0-9_]*");
    public static final Pattern HEX_MATCH = Pattern.compile("[a-fA-F0-9]{6}");
    public static final Pattern IS_INTEGER = Pattern.compile("([+-]?\\d)+");
    public static final Pattern IS_DECIMAL = Pattern.compile("([+-]?\\d*(\\.\\d+)?)+");

    public static Key key(String id)
    {
        return Key.withNamespace(NAMESPACE, id);
    }
}
