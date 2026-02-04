package me.udnek.coreu.util;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;
import sun.misc.Unsafe;

import java.lang.reflect.*;

@org.jspecify.annotations.NullMarked public class Reflex{
    public static Field getField(Class<?> source, String name) {
        try {
            return source.getDeclaredField(name);
        }
        catch (NoSuchFieldException exception) {
            Class<?> superClass = source.getSuperclass();
            if (superClass == null) {
                if (source.getFields().length == 0){
                    LogUtils.pluginLog("No fields found in class " + source.getName());
                } else {
                    LogUtils.pluginLog("Available fields in class " + source.getName() + ":");
                    for (Field field : source.getFields()) {
                        LogUtils.pluginLog(field);
                    }
                }

                throw new RuntimeException(exception);
            }
            return getField(superClass, name);
        }
    }

    public static @UnknownNullability <T> T getFieldValue(Object source, String name) {
        Class<?> clazz;
        if (source instanceof Class<?>) clazz = (Class<?>) source;
        else clazz = source.getClass();

        Field field = getField(clazz, name);

        field.setAccessible(true);
        try {
            return (T) field.get(source);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }

    public static void setStaticFinalFieldValue(Class<?> clazz, String name, @Nullable Object value){
        try {
            Field field = getField(clazz, name);
            field.setAccessible(true);

            Field modifiers = getField(Field.class, "modifiers");
            modifiers.setAccessible(true);
            modifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL);

            field.set(null, value);
        } catch (IllegalAccessException | IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setFieldValue(Object source, String name, @Nullable Object value) {
        try {
            boolean isStatic = source instanceof Class;
            Class<?> clazz = isStatic ? (Class<?>) source : source.getClass();

            Field field = getField(clazz, name);
            field.setAccessible(true);

            field.set(isStatic ? null : source, value);
        } catch (IllegalAccessException | IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setRecordFieldValue(Record instance, String fieldName, @Nullable Object value) {
        try {
            Field field = instance.getClass().getDeclaredField(fieldName);

            Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            Unsafe unsafe = (Unsafe) unsafeField.get(null);

            Field theInternalUnsafeField = Unsafe.class.getDeclaredField("theInternalUnsafe");
            theInternalUnsafeField.setAccessible(true);
            Object theInternalUnsafe = theInternalUnsafeField.get(null);

            Method offset = Class.forName("jdk.internal.misc.Unsafe").getMethod("objectFieldOffset", Field.class);
            unsafe.putBoolean(offset, 12, true);

            unsafe.putObject(instance, (long) offset.invoke(theInternalUnsafe, field), value);
        } catch (IllegalAccessException | NoSuchFieldException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }


    public static Method getMethod(Class<?> clazz, String name, @Nullable Class<?>... parameterTypes){
        try {
            Method method = clazz.getDeclaredMethod(name, parameterTypes);
            method.setAccessible(true);
            return method;
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
    public static Method getMethod(Class<?> clazz, String name){
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            if (method.getName().equals(name)){
                method.setAccessible(true);
                return method;
            }
        }
        throw new RuntimeException(new NoSuchMethodException(name));
    }

    public static <T> @UnknownNullability T invokeMethod(@Nullable Object object, Method method, @Nullable Object ...args){
        try {
            return (T) method.invoke(object, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> Constructor<T> getFirstConstructor(Class<T> clazz){
        Constructor<?>[] constructor = clazz.getDeclaredConstructors();
        constructor[0].setAccessible(true);
        return (Constructor<T>) constructor[0];
    }

    public static <T> Constructor<T> getConstructor(Class<T> clazz, Class<?> ...parameterTypes){
        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor(parameterTypes);
            constructor.setAccessible(true);
            return constructor;
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T construct(Constructor<T> constructor, @Nullable Object ...args){
        try {
            return constructor.newInstance(args);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}

















