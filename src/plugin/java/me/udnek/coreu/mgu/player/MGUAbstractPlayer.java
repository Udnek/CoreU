package me.udnek.coreu.mgu.player;

import me.udnek.coreu.custom.component.AbstractComponentHolder;
import me.udnek.coreu.custom.item.CustomItem;
import me.udnek.coreu.mgu.MGUManager;
import me.udnek.coreu.mgu.game.MGUGameInstance;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@org.jspecify.annotations.NullMarked public abstract class MGUAbstractPlayer extends AbstractComponentHolder<MGUPlayer>implements MGUPlayer{

    private final MGUGameInstance game;
    private final Player player;

    public MGUAbstractPlayer(Player player, MGUGameInstance game){
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
    public MGUGameInstance getGame() {
        return game;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MGUPlayer mguPlayer) return mguPlayer.getPlayer().equals(player);
        return false;
    }
}
