package me.udnek.coreu.rpgu.component;

import me.udnek.coreu.custom.component.AbstractComponentHolder;
import me.udnek.coreu.custom.component.CustomComponent;
import me.udnek.coreu.custom.component.CustomComponentMap;
import me.udnek.coreu.custom.component.CustomComponentType;
import me.udnek.coreu.custom.equipment.EquippableItem;
import me.udnek.coreu.custom.equipment.universal.BaseUniversalSlot;
import me.udnek.coreu.custom.item.CustomItem;
import me.udnek.coreu.custom.item.LoreProvidingItemComponent;
import me.udnek.coreu.rpgu.component.ability.passive.RPGUItemPassiveAbility;
import me.udnek.coreu.util.LoreBuilder;
import org.bukkit.entity.Player;

import java.util.List;

@org.jspecify.annotations.NullMarked public class RPGUPassiveItem extends AbstractComponentHolder<RPGUPassiveItem>implements LoreProvidingItemComponent, EquippableItem{

    public static final RPGUPassiveItem DEFAULT = new RPGUPassiveItem(){
        @Override
        public CustomComponentMap<RPGUPassiveItem> getComponents() {
            return CustomComponentMap.immutableEmpty();
        }
    };

    public List<RPGUItemPassiveAbility> getAbilities(){
        return getComponents().getAllTyped(RPGUItemPassiveAbility.class);
    }

    @Override
    public CustomComponentType<? super CustomItem, ? extends CustomComponent<? super CustomItem>> getType() {
        return RPGUComponents.PASSIVE_ABILITY_ITEM;
    }

    @Override
    public void getLore(CustomItem customItem, LoreBuilder loreBuilder) {
        getAbilities().forEach(component -> component.getLore(loreBuilder));
    }

    @Override
    public boolean isAppropriate(CustomItem item, Player player, BaseUniversalSlot slot) {
        return getAbilities().stream().anyMatch(input -> input.getSlot().intersects(player, slot));
    }

    @Override
    public void onEquipped(CustomItem item, Player player, BaseUniversalSlot slot) {}

    @Override
    public void onUnequipped(CustomItem item, Player player, BaseUniversalSlot slot) {}

    @Override
    public void tick(CustomItem customItem, Player player, BaseUniversalSlot slot, int tickDelay) {
        getAbilities().stream().filter(ability -> ability.getSlot().intersects(player, slot))
                .forEach(ability -> ability.tick(customItem, player, slot, tickDelay));
    }
}
