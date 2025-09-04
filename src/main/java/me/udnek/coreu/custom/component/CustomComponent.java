package me.udnek.coreu.custom.component;

import org.jetbrains.annotations.NotNull;

public interface CustomComponent<HolderType> {

    default void throwCanNotChangeDefault(){
        throw new RuntimeException("Can not change default component: " + this + ", create default or apply new firstly");
    }

    @NotNull CustomComponentType<? super HolderType, ? extends CustomComponent<? super HolderType>> getType();
}
