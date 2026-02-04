package me.udnek.coreu.rpgu.lore.ability;

import me.udnek.coreu.rpgu.lore.AttributeLoreGenerator;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@org.jspecify.annotations.NullMarked public class ActiveAbilityLorePart implements AbilityLorePart{

    protected @Nullable Component header;
    protected List<Component> description = new ArrayList<>();
    protected List<Component> properties = new ArrayList<>();

    @Override
    public void toComponents(Consumer<Component> consumer) {
        if (isEmpty()) return;
        consumer.accept(Component.empty());
        if (header != null) consumer.accept(header);
        description.forEach(consumer);
        properties.forEach(consumer);
    }

    @Override
    public void setHeader(Component component){
        header = component.color(ACTIVE_HEADER_COLOR).decoration(TextDecoration.ITALIC, false);
    }

    @Override
    public void addAbilityProperty(Component component) {
        addWithAbilityFormat(component.color(ACTIVE_STATS_COLOR), properties);
    }

    @Override
    public void addAbilityPropertyDoubleTab(Component component) {
        addAbilityProperty(AttributeLoreGenerator.addTab(component));
    }

    @Override
    public void addAbilityDescription(Component component) {
        addWithAbilityFormat(component.color(ACTIVE_DESCRIPTION_COLOR), description);
    }

    public void addWithAbilityFormat(Component component, List<Component> to){
        to.add(AttributeLoreGenerator.addTab(component).decoration(TextDecoration.ITALIC, false));
    }

    @Deprecated
    @Override
    public void add(Component component) {
        throw new RuntimeException("Can not use add on ActiveAbilityLorePart");
    }
    @Deprecated
    @Override
    public void addFirst(Component component) {
        throw new RuntimeException("Can not use add on ActiveAbilityLorePart");
    }

    @Override
    public boolean isEmpty() {
        return properties.isEmpty() && description.isEmpty() && header == null;
    }

}
