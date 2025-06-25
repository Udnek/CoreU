package me.udnek.coreu.rpgu.lore.ability;

import me.udnek.coreu.rpgu.lore.AttributeLoreGenerator;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ActiveAbilityLorePart implements AbilityLorePart {

    @Nullable Component header;
    List<@NotNull Component> data = new ArrayList<>();

    @Override
    public void toComponents(@NotNull Consumer<Component> consumer) {
        if (isEmpty()) return;
        consumer.accept(Component.empty());
        if (header != null) consumer.accept(header);
        data.forEach(consumer);
    }

    @Override
    public void setHeader(@NotNull Component component){
        header = component.color(ACTIVE_HEADER_COLOR).decoration(TextDecoration.ITALIC, false);
    }

    @Override
    public void addAbilityStat(@NotNull Component component) {
        addWithAbilityFormat(component.color(ACTIVE_STATS_COLOR));
    }

    @Override
    public void addAbilityStatDoubleTab(@NotNull Component component) {
        addAbilityStat(AttributeLoreGenerator.addTab(component));
    }

    @Override
    public void addAbilityDescription(@NotNull Component component) {
        addWithAbilityFormat(component.color(ACTIVE_DESCRIPTION_COLOR));
    }

    @Override
    public void addAbilityDescription(@NotNull String rawItemName, int line){
        addAbilityDescription(Component.translatable(rawItemName + ".rpgu_active_ability." + line));
    }

    @Override
    public void addWithAbilityFormat(@NotNull Component component){
        data.add(AttributeLoreGenerator.addTab(component).decoration(TextDecoration.ITALIC, false));
    }

    @Deprecated
    @Override
    public void add(@NotNull Component component) {
        throw new RuntimeException("Can not use add on ActiveAbilityLorePart");
    }
    @Deprecated
    @Override
    public void addFirst(@NotNull Component component) {
        throw new RuntimeException("Can not use add on ActiveAbilityLorePart");
    }

    @Override
    public boolean isEmpty() {return data.isEmpty() && header == null;}

}
