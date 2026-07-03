package me.udnek.coreu.custom.component;


@org.jspecify.annotations.NullMarked public abstract class AbstractComponentHolder<HolderType> implements ComponentHolder<HolderType>{
    private CustomComponentMap<HolderType> components = null;

    @Override
    public CustomComponentMap<HolderType> getComponents() {
        if (components == null) components = new CustomComponentMap<>();
        return components;
    }
}
