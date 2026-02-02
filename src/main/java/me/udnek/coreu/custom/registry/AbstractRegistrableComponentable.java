package me.udnek.coreu.custom.registry;

import me.udnek.coreu.custom.component.ComponentHolder;
import me.udnek.coreu.custom.component.CustomComponentMap;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractRegistrableComponentable<HolderType> extends AbstractRegistrable implements ComponentHolder<HolderType> {

    private CustomComponentMap<HolderType> components = null;

    @Override
    public final @NotNull CustomComponentMap<HolderType> getComponents() {
        if (components == null) components = new CustomComponentMap<>();
        return components;
    }
}
