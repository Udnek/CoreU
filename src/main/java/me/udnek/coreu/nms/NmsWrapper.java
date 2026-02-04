package me.udnek.coreu.nms;

import org.jetbrains.annotations.NotNull;

@org.jspecify.annotations.NullMarked public  interface NmsWrapper<T>{
    @NotNull T getNms();
}
