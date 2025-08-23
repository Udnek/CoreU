package me.udnek.coreu.mgu.player;

import me.udnek.coreu.custom.component.AbstractComponentHolder;
import me.udnek.coreu.custom.component.CustomComponent;
import me.udnek.coreu.custom.item.CustomItem;
import me.udnek.coreu.mgu.MGUManager;
import me.udnek.coreu.mgu.game.MGUGameInstance;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class MGUAbstractPlayer extends AbstractComponentHolder<MGUPlayer, CustomComponent<MGUPlayer>> implements MGUPlayer {

    private final MGUGameInstance game;
    private final Player player;

    public MGUAbstractPlayer(@NotNull Player player, @NotNull MGUGameInstance game){
        this.game = game;
        this.player = player;
        MGUManager.get().registerPlayer(this);
    }

    public List<CustomItem> getAllItemAbilities(){
        List<CustomItem> items = new ArrayList<>();
        for (ItemStack stack : getPlayer().getInventory()) {
            CustomItem.consumeIfCustom(stack, items::add);
        }
        return items;
    }

    @Override
    public @NotNull MGUGameInstance getGame() {
        return game;
    }

    @Override
    public @NotNull Player getPlayer() {
        return player;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MGUPlayer mguPlayer) return mguPlayer.getPlayer().equals(player);
        return false;
    }
}
