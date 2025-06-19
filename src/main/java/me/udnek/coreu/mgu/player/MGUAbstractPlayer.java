package me.udnek.coreu.mgu.player;

import me.udnek.coreu.custom.equipmentslot.universal.BaseUniversalSlot;
import me.udnek.coreu.custom.equipmentslot.universal.UniversalInventorySlot;
import me.udnek.coreu.custom.item.CustomItem;
import me.udnek.coreu.mgu.MGUManager;
import me.udnek.coreu.mgu.ability.MGUComponents;
import me.udnek.coreu.mgu.ability.MGUPlayerDataHolder;
import me.udnek.coreu.mgu.game.MGUGameInstance;
import me.udnek.coreu.custom.component.AbstractComponentHolder;
import me.udnek.coreu.custom.component.CustomComponent;
import me.udnek.coreu.custom.component.CustomComponentMap;
import me.udnek.coreu.rpgu.component.ability.RPGUItemAbility;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public abstract class MGUAbstractPlayer extends AbstractComponentHolder<MGUPlayer, CustomComponent<MGUPlayer>> implements MGUPlayer {

    private final MGUGameInstance game;
    private final Player player;

    public MGUAbstractPlayer(@NotNull Player player, @NotNull MGUGameInstance game){
        this.game = game;
        this.player = player;
        MGUManager.get().registerPlayer(this);
    }

    public @NotNull CustomComponentMap<MGUPlayerDataHolder, CustomComponent<MGUPlayerDataHolder>> getData(){
        return getComponents().getOrCreateDefault(MGUComponents.PLAYER_DATA).getComponents();
    }

    public List<CustomItem> getAllItemAbilities(){
        List<CustomItem> items = new ArrayList<>();
        for (ItemStack stack : getPlayer().getInventory().getContents()) {
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
}
