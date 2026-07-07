package me.udnek.coreu.custom.component;

@org.jspecify.annotations.NullMarked public  interface ComponentHolder<HolderType>{
     CustomComponentMap<HolderType> getComponents();
}
