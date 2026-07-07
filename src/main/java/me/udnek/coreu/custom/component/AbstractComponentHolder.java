package me.udnek.coreu.custom.component;


import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public abstract class AbstractComponentHolder<HolderType> implements ComponentHolder<HolderType>{
    private @Nullable CustomComponentMap<HolderType> components = null;

    @Override
    public CustomComponentMap<HolderType> getComponents() {
        if (components == null) components = new CustomComponentMap<>();
        return components;
    }
}
