package me.udnek.coreu.custom.component;

import me.udnek.coreu.custom.component.instance.TranslatableThing;
import me.udnek.coreu.custom.registry.AbstractRegistrable;
import me.udnek.coreu.custom.registry.AbstractRegistrableComponentable;
import me.udnek.coreu.custom.registry.Registrable;
import me.udnek.coreu.resourcepack.path.VirtualRpJsonFile;
import net.kyori.adventure.translation.Translatable;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Supplier;

public class ConstructableComponentType<HolderType, Component extends CustomComponent<HolderType>> extends AbstractRegistrableComponentable<CustomComponentType<HolderType, Component>> implements CustomComponentType<HolderType, Component>, Translatable {

    // TRIES TO TRANSLATE DEFAULT COMPONENT
    public static final TranslatableThing DEFAULT_COMPONENT_TRANSLATOR = new TranslatableThing(null){
        @Override
        public @NotNull List<VirtualRpJsonFile> getFiles(@NotNull Translatable translatable, @NotNull Registrable registrable) {
            if (registrable instanceof CustomComponentType<?,?> componentType){
                if (componentType.getDefault() instanceof Translatable defaultComponent){
                    if (defaultComponent instanceof ComponentHolder<?> defaultComponent_holder){
                        return defaultComponent_holder.getComponents().getOrDefault(ConstructableComponentType.TRANSLATABLE_THING).getFiles(defaultComponent, registrable);
                    }
                }
            }
            return List.of();
        }
    };

    protected Component defaultComponent;
    protected String rawId;
    protected Supplier<Component> componentCreator;

    public ConstructableComponentType(@NotNull String rawId, @NotNull Component defaultComponent, @NotNull Supplier<Component> componentCreator){
        this.rawId = rawId;
        this.defaultComponent = defaultComponent;
        this.componentCreator = componentCreator;
        getComponents().set(DEFAULT_COMPONENT_TRANSLATOR);
    }

    public ConstructableComponentType(@NotNull String rawId, @NotNull Component defComponent){
        this(rawId, defComponent, () -> defComponent);
    }

    @NotNull
    @Override
    public Component getDefault() {
        return defaultComponent;
    }

    @Override
    public @NotNull Component createNewDefault() {
        return componentCreator.get();
    }

    @Override
    public @NotNull String getRawId() {
        return rawId;
    }

    @Override
    public @NotNull String translationKey() {
        return "component."+key().namespace()+"."+key().value();
    }
}
