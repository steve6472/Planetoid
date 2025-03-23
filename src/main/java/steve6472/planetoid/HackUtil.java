package steve6472.planetoid;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Created by steve6472
 * Date: 6/12/2024
 * Project: Domin <br>
 */
public class HackUtil
{
    public static void printStackTrace()
    {
        for (StackTraceElement stackTraceElement : Thread.currentThread().getStackTrace())
        {
            System.out.println(stackTraceElement);
        }
    }

    private static void generateDialogBoxMultiIcon()
    {
        final int iconCentralX = 1;
        final int iconCentralY = 8;

        final int width = 13;
        final int height = 5;
        
        final int offsetX = 0;
        final int offsetY = 0;

        JsonArray array = new JsonArray();

        for (int i = 0; i < width; i++) 
        {
            for (int j = 0; j < height; j++) 
            {
                int iconX = i == 0 ? iconCentralX - 1 : (i == width - 1 ? iconCentralX + 1 : iconCentralX);
                int iconY = j == 0 ? iconCentralY - 1 : (j == height - 1 ? iconCentralY + 1 : iconCentralY);

                JsonObject icon = new JsonObject();
                icon.addProperty("x", iconX);
                icon.addProperty("y", iconY);

                JsonObject pos = new JsonObject();
                pos.addProperty("x", (i - width / 2) * 8 + offsetX);
                pos.addProperty("y", (j - height / 2) * 8 + offsetY);

                JsonObject iconEntry = new JsonObject();
                iconEntry.add("icon", icon);
                iconEntry.add("pos", pos);
                
                array.add(iconEntry);
            }
        }

        JsonObject json = new JsonObject();
        json.add("multi_icon", array);

        System.out.println(json.toString());
    }

    public static void main(String[] args) {
        generateDialogBoxMultiIcon();
    }
}
