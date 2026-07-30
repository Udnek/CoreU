package me.udnek.coreu.custom.component;

import org.jspecify.annotations.NullMarked;

@NullMarked public  interface ComponentHolder<HolderType>{
     CustomComponentMap<HolderType> getComponents();
}
