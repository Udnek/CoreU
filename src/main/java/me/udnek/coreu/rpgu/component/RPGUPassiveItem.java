package me.udnek.coreu.rpgu.component;

import me.udnek.coreu.custom.component.AbstractComponentHolder;
import me.udnek.coreu.custom.component.CustomComponent;
import me.udnek.coreu.custom.component.CustomComponentMap;
import me.udnek.coreu.custom.component.CustomComponentType;
import me.udnek.coreu.custom.equipmentslot.EquippableItem;
import me.udnek.coreu.custom.equipmentslot.universal.BaseUniversalSlot;
import me.udnek.coreu.custom.item.CustomItem;
import me.udnek.coreu.custom.item.LoreProvidingItemComponent;
import me.udnek.coreu.rpgu.component.ability.passive.RPGUItemPassiveAbility;
import me.udnek.coreu.util.LoreBuilder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RPGUPassiveItem extends AbstractComponentHolder<RPGUPassiveItem> implements LoreProvidingItemComponent, EquippableItem {

    public static final RPGUPassiveItem DEFAULT = new RPGUPassiveItem(){
        @Override
        public @NotNull CustomComponentMap<RPGUPassiveItem> getComponents() {
            return CustomComponentMap.immutableEmpty();
        }
    };

    public @NotNull List<RPGUItemPassiveAbility> getAbilities(){
        return getComponents().getAllTyped(RPGUItemPassiveAbility.class);
    }

    @Override
    public @NotNull CustomComponentType<? super CustomItem, ? extends CustomComponent<? super CustomItem>> getType() {
        return RPGUComponents.PASSIVE_ABILITY_ITEM;
    }

    @Override
    public void getLore(@NotNull CustomItem customItem, @NotNull LoreBuilder loreBuilder) {
        getAbilities().forEach(component -> component.getLore(loreBuilder));
    }

    @Override
    public boolean isAppropriate(@NotNull CustomItem item, @NotNull Player player, @NotNull BaseUniversalSlot slot) {
        return getAbilities().stream().anyMatch(input -> input.getSlot().intersects(player, slot));
    }

    @Override
    public void onEquipped(@NotNull CustomItem item, @NotNull Player player, @NotNull BaseUniversalSlot slot) {}

    @Override
    public void onUnequipped(@NotNull CustomItem item, @NotNull Player player, @NotNull BaseUniversalSlot slot) {}

    @Override
    public void tick(@NotNull CustomItem customItem, @NotNull Player player, @NotNull BaseUniversalSlot slot, int tickDelay) {
        getAbilities().stream().filter(ability -> ability.getSlot().intersects(player, slot))
                .forEach(ability -> ability.tick(customItem, player, slot, tickDelay));
    }
}
