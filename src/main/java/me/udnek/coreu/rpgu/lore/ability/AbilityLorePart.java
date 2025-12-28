package me.udnek.coreu.rpgu.lore.ability;

import me.udnek.coreu.util.LoreBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;

public interface AbilityLorePart extends LoreBuilder.Componentable{

    TextColor ACTIVE_HEADER_COLOR = NamedTextColor.GREEN;
    TextColor PASSIVE_HEADER_COLOR = NamedTextColor.YELLOW;

    TextColor ACTIVE_STATS_COLOR = NamedTextColor.GRAY;
    TextColor PASSIVE_STATS_COLOR = NamedTextColor.GRAY;

    TextColor ACTIVE_DESCRIPTION_COLOR = NamedTextColor.BLUE;
    TextColor PASSIVE_DESCRIPTION_COLOR = NamedTextColor.BLUE;


    void setHeader(@NotNull Component component);

    void addAbilityProperty(@NotNull Component component);
    void addAbilityPropertyDoubleTab(@NotNull Component component);
    void addAbilityDescription(@NotNull Component component);
}
