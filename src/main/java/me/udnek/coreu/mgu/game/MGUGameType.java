package me.udnek.coreu.mgu.game;

import me.udnek.coreu.custom.registry.Registrable;
import me.udnek.coreu.mgu.MGUManager;
import me.udnek.coreu.mgu.player.MGUPlayer;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked public  interface MGUGameType extends Registrable{
    default boolean isPlayerInThisGame(Player player){
        return getIfPlayerInThisGame(player) != null;
    }
    default <T extends MGUPlayer> @Nullable T getIfPlayerInThisGame(Player player){
        MGUPlayer mguPlayer = MGUManager.get().getPlayer(player);
        if (mguPlayer == null) return null;
        if (mguPlayer.getGame().getType() == this) return (T) mguPlayer;
        return null;
    }
}
