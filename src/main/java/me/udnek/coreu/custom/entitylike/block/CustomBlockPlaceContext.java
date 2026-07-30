package me.udnek.coreu.custom.entitylike.block;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked public  record CustomBlockPlaceContext(@Nullable Player player, @Nullable Block placedAgainst, @Nullable BlockPlaceEvent event){

    public static final CustomBlockPlaceContext EMPTY = new CustomBlockPlaceContext(null, null, null);
}
