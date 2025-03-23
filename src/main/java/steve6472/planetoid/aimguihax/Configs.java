package steve6472.planetoid.aimguihax;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import imgui.ImGui;
import imgui.flag.ImGuiColorEditFlags;
import imgui.flag.ImGuiDataType;
import imgui.type.ImBoolean;
import imgui.type.ImDouble;
import imgui.type.ImFloat;
import steve6472.planetoid.aimguihax.config.*;
import steve6472.core.util.ColorUtil;
import steve6472.planetoid.world.World;

public class Configs 
{
    private static void runConfigs(Object holder, Field field, Map<Field, Object> changeMap, World world)
    {
        getAnnotation(holder, changeMap, ConfIntColor.class, int.class, field, (ann, obj) -> {
            float[] colors = ColorUtil.getColors(obj);
            ImGui.pushItemWidth(200);

            if (ann.hasAlpha())
                ImGui.colorPicker4(field.getName() + "##" + field.hashCode(), colors, ImGuiColorEditFlags.InputRGB | ImGuiColorEditFlags.AlphaBar);
            else
                ImGui.text("Color with no alpha not suppoerted yet");

            ImGui.popItemWidth();

            int c = ColorUtil.getColor(colors[0], colors[1], colors[2], colors[3]);
            return c;
        });

        getAnnotation(holder, changeMap, ConfIntSlider.class, int.class, field, (ann, obj) -> {
            int[] i = {obj};

            //TODO: add step
            ImGui.sliderInt(field.getName() + "##" + field.hashCode(), i, ann.min(), ann.max());

            return i[0];
        });

        getAnnotation(holder, changeMap, ConfDoubleSlider.class, double.class, field, (ann, obj) -> {
            ImDouble i = new ImDouble(obj);

            //TODO: add step
            ImGui.sliderScalar(field.getName() + "##" + field.hashCode(), ImGuiDataType.Double, i, ann.min(), ann.max());

            return i.get();
        });

        getAnnotation(holder, changeMap, ConfFloatSlider.class, float.class, field, (ann, obj) -> {
            ImFloat i = new ImFloat(obj);

            //TODO: add step
            ImGui.sliderScalar(field.getName() + "##" + field.hashCode(), ImGuiDataType.Float, i, ann.min(), ann.max());

            return i.get();
        });

        getAnnotation(holder, changeMap, ConfBoolToggle.class, boolean.class, field, (ann, obj) -> {
            ImBoolean i = new ImBoolean(obj);
            ImGui.checkbox(field.getName() + "##" + field.hashCode(), i);
            return i.get();
        });
/*
        getAnnotation(holder, changeMap, ConfEntity.class, Entity.class, field, (ann, obj) -> {
            Class<?>[] filter = ann.filter();

            Entity selectedEntity = obj;

            if (ImGui.beginCombo(field.getName() + "##" + field.hashCode(), obj == null ? "None" : obj.get(All.class).id()))
            {
                entitiesLoop: for (Results.With1<All> all : world.ecs().findEntitiesWith(All.class))
                {
                    Entity entity = all.entity();
                    for (Class<?> filterType : filter)
                    {
                        if (!entity.has(filterType))
                            continue entitiesLoop;
                    }

                    if (ImGui.selectable(all.comp().id() + "##" + entity.hashCode(), obj == entity))
                    {
                        selectedEntity = entity;
                    }
                }

                ImGui.endCombo();
            }

            return selectedEntity;
        });*/
    }

    public static <T> T systemConfig(T holder, World world)
    {
        Class<?> holderClass = holder.getClass();

        Map<Field, Object> changeMap = new HashMap<>();

        Field[] declaredFields = holderClass.getDeclaredFields();

        for (Field field : declaredFields)
        {
            field.setAccessible(true);
            runConfigs(holder, field, changeMap, world);
        }

        if (changeMap.isEmpty())
            return holder;

        if (holderClass.isRecord())
        {
            Constructor<?> mainConstructor = getMainRecordConstructor(holderClass);
            if (mainConstructor == null)
                return holder;

            Object[] constructorParams = new Object[declaredFields.length];
            for (int i = 0; i < declaredFields.length; i++) 
            {
                Field field = declaredFields[i];
                
                if (changeMap.containsKey(field))
                {
                    constructorParams[i] = changeMap.get(field);
                } else
                {
                    try 
                    {
                        constructorParams[i] = field.get(holder);
                    } catch (IllegalArgumentException | IllegalAccessException e) 
                    {
                        e.printStackTrace();
                    }
                }
            }

            try 
            {
                return (T) mainConstructor.newInstance(constructorParams);
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException e) 
                    {
                e.printStackTrace();
            }
            
        } else
        {
            changeMap.forEach((field, obj) -> 
            {
                try 
                {
                    field.set(holder, obj);
                } catch (IllegalArgumentException | IllegalAccessException e) 
                {
                    e.printStackTrace();
                }
            });
        }

        return holder;
    }

    /*
     * Util methods
     */

    private static Constructor<?> getMainRecordConstructor(Class<?> record)
    {
        Field[] declaredFields = record.getDeclaredFields();
        Class<?>[] types = new Class[declaredFields.length];

        for (int i = 0; i < declaredFields.length; i++) {
            types[i] = declaredFields[i].getType();
        }

        try {
            return record.getDeclaredConstructor(types);
        } catch (NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static <AnnType extends Annotation, FieldType> void getAnnotation(Object holder, Map<Field, Object> changeMap, Class<AnnType> type, Class<FieldType> fieldType, Field field, BiFunction<AnnType, FieldType, FieldType> func)
    {
        AnnType ann = field.getAnnotation(type);

        if (ann == null)
            return;

        if (field.getType() != fieldType)
            throw new RuntimeException("Filed type mismatch, expected " + fieldType + ", got " + field.getType());

        Object obj = null;
        try 
        {
            obj = field.get(holder);
        } catch (IllegalArgumentException | IllegalAccessException e) 
        {
            e.printStackTrace();
        }

        FieldType r = func.apply(ann, (FieldType) obj);
        changeMap.put(field, r);
    }
}
