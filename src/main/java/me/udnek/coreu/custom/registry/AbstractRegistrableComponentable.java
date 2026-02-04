package me.udnek.coreu.custom.registry;

import me.udnek.coreu.custom.component.ComponentHolder;
import me.udnek.coreu.custom.component.CustomComponentMap;

@org.jspecify.annotations.NullMarked public abstract class AbstractRegistrableComponentable<HolderType> extends AbstractRegistrable implements ComponentHolder<HolderType>{

    private CustomComponentMap<HolderType> components = null;

    @Override
    public final CustomComponentMap<HolderType> getComponents() {
        if (components == null) components = new CustomComponentMap<>();
        return components;
    }
}
