package me.udnek.coreu.rpgu.lore.ability;

import me.udnek.coreu.util.LoreBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

@org.jspecify.annotations.NullMarked public  interface AbilityLorePart extends LoreBuilder.Componentable{

    TextColor ACTIVE_HEADER_COLOR = NamedTextColor.GREEN;
    TextColor PASSIVE_HEADER_COLOR = NamedTextColor.YELLOW;

    TextColor ACTIVE_STATS_COLOR = NamedTextColor.GRAY;
    TextColor PASSIVE_STATS_COLOR = NamedTextColor.GRAY;

    TextColor ACTIVE_DESCRIPTION_COLOR = NamedTextColor.BLUE;
    TextColor PASSIVE_DESCRIPTION_COLOR = NamedTextColor.BLUE;


    void setHeader(Component component);

    void addAbilityProperty(Component component);
    void addAbilityPropertyDoubleTab(Component component);
    void addAbilityDescription(Component component);
}
