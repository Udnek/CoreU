package me.udnek.coreu.rpgu.component.ability.toggle;

import com.google.common.base.Function;
import io.papermc.paper.persistence.PersistentDataContainerView;
import me.udnek.coreu.CoreU;
import me.udnek.coreu.custom.equipment.universal.UniversalInventorySlot;
import me.udnek.coreu.custom.item.CustomItem;
import me.udnek.coreu.rpgu.component.ability.RPGUItemAbstractAbility;
import me.udnek.coreu.rpgu.lore.AttributesLorePart;
import me.udnek.coreu.rpgu.lore.ability.PassiveAbilityLorePart;
import me.udnek.coreu.util.LoreBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public abstract class RPGUConstructableToggleAbility<ActivationContext> extends RPGUItemAbstractAbility<ActivationContext> implements RPGUItemToggleAbility<ActivationContext> {

    public static final NamespacedKey TOGGLE_KEY = new NamespacedKey(CoreU.getInstance(), "rpgu_toggle_ability_is_toggled");

    @Override
    public void getLore(@NotNull LoreBuilder loreBuilder){
        LoreBuilder.Componentable componentable = loreBuilder.get(LoreBuilder.Position.ATTRIBUTES);
        PassiveAbilityLorePart lorePart;
        if (!(componentable instanceof AttributesLorePart attributesLorePart)){
            AttributesLorePart newPart = new AttributesLorePart();
            loreBuilder.set(LoreBuilder.Position.ATTRIBUTES, newPart);
            lorePart = newPart;
        } else {
            lorePart = attributesLorePart;
        }

        lorePart.setEquipmentSlot(getSlot());
        lorePart.setHeader(Component.translatable("rpgu_toggle_ability.coreu.title"));
        addDescriptionLines(lorePart);
        addPropertyLines(lorePart);
    }

    // RETURNS IS TOGGLED
    public boolean toggle(@NotNull CustomItem customItem, @NotNull Player player, @NotNull UniversalInventorySlot slot){
        return setToggled(customItem, player, slot, !isToggled(customItem, player, slot));
    }

    // RETURNS IS TOGGLED
    public boolean setToggled(@NotNull CustomItem customItem, @NotNull Player player, @NotNull UniversalInventorySlot slot, boolean toggle){
        slot.modifyItem(new Function<>() {
            @Override
            public ItemStack apply(ItemStack stack) {
                stack.editPersistentDataContainer(new Consumer<>() {
                    @Override
                    public void accept(PersistentDataContainer container) {
                        container.set(TOGGLE_KEY, PersistentDataType.BOOLEAN, toggle);
                    }
                });
                return stack;
            }
        }, player);
        return toggle;
    }

    protected boolean isToggled(@NotNull PersistentDataContainerView container){
        return container.getOrDefault(TOGGLE_KEY, PersistentDataType.BOOLEAN, false);
    }

    @Override
    public boolean isToggled(@NotNull CustomItem customItem, @NotNull Player player, @NotNull UniversalInventorySlot slot) {
        ItemStack item = slot.getItem(player);
        if (item == null) return false;
        return isToggled(item.getPersistentDataContainer());
    }
}
