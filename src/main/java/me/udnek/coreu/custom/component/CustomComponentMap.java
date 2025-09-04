package me.udnek.coreu.custom.component;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class CustomComponentMap<Holder> implements Iterable<CustomComponent<? super Holder>>{


    public static <H> CustomComponentMap<H> immutableEmpty(){
        return new CustomComponentMap<>(){
            @Override
            public void set(@NotNull CustomComponent<? super H> component) {
                throw new RuntimeException("Can not set component to ImmutableEmptyHolder!");
            }
        };
    }

    private @Nullable Map<
            CustomComponentType<? super Holder, ? extends CustomComponent<? super Holder>>,
            CustomComponent<? super Holder>> map = null;

    public @NotNull <SpecificComponent extends CustomComponent<? super Holder>> SpecificComponent getOrDefault(@NotNull CustomComponentType<?, SpecificComponent> type) {
        SpecificComponent component = get(type);
        return component == null ? type.getDefault() : component;
    }

    public @NotNull <SpecificComponent extends CustomComponent<? super Holder>> SpecificComponent getOrCreateDefault(@NotNull CustomComponentType<?, SpecificComponent> type) {
        SpecificComponent component = get(type);
        if (component == null) {
            SpecificComponent newDefault = type.createNewDefault();
            set(newDefault);
            return newDefault;
        }
        return component;
    }

    public @NotNull <SpecificComponent extends CustomComponent<? super Holder>> SpecificComponent getOrException(@NotNull CustomComponentType<?, SpecificComponent> type){
        return Objects.requireNonNull(get(type), "Component " + type.getKey().asString() + " is not present!");
    }

    public @Nullable <SpecificComponent extends CustomComponent<? super Holder>> SpecificComponent get(@NotNull CustomComponentType<?, SpecificComponent> type) {
        if (map == null) return null;
        return (SpecificComponent) map.get(type);
    }
    public boolean has(@NotNull CustomComponentType<? super Holder, ?> type) {
        return get(type) != null;
    }
    public void set(@NotNull CustomComponent<? super Holder> component) {
        if (map == null) map = new HashMap<>();
        map.put(component.getType(), component);
    }
    public void remove(@NotNull CustomComponentType<? super Holder, ?> type) {
        if (map != null) map.remove(type);
    }

    public @NotNull <SpecificComponent extends CustomComponent<?>> List<SpecificComponent> getAllTyped(Class<SpecificComponent> clazz){
        if (map == null) return List.of();
        List<SpecificComponent> list = new ArrayList<>();
        map.values().forEach(customComponent -> {
            if (clazz.isInstance(customComponent)) list.add((SpecificComponent) customComponent);
        });
        return list;
    }

    public @NotNull List<CustomComponent<? super Holder>> getAll() {
        if (map == null) return List.of();
        return new ArrayList<>(map.values());
    }

    @Override
    public @NotNull Iterator<CustomComponent<? super Holder>> iterator() {
        if (map == null) return Collections.emptyIterator();
        return map.values().iterator();
    }
}
