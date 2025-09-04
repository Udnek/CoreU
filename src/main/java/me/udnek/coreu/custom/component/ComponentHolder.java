package me.udnek.coreu.custom.component;

import org.jetbrains.annotations.NotNull;

public interface ComponentHolder<HolderType> {
     @NotNull CustomComponentMap<HolderType> getComponents();
}
