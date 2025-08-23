package me.udnek.coreu.mgu.game;

import me.udnek.coreu.custom.registry.Registrable;
import me.udnek.coreu.mgu.MGUManager;
import me.udnek.coreu.mgu.player.MGUPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface MGUGameType extends Registrable {
    default boolean isPlayerInThisGame(@NotNull Player player){
        return getIfPlayerInThisGame(player) != null;
    }
    default <T extends MGUPlayer> @Nullable T getIfPlayerInThisGame(@NotNull Player player){
        MGUPlayer mguPlayer = MGUManager.get().getPlayer(player);
        if (mguPlayer == null) return null;
        if (mguPlayer.getGame().getType() == this) return (T) mguPlayer;
        return null;
    }
}
