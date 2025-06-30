package me.udnek.coreu.custom.entitylike.block;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public record CustomBlockPlaceContext(@Nullable Player player, @Nullable Block placedAgainst) {

    public static final CustomBlockPlaceContext EMPTY = new CustomBlockPlaceContext(null, null);
}
