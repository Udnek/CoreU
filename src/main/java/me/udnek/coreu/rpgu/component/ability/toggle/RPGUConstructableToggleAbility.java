package me.udnek.coreu.rpgu.component.ability.toggle;

import io.papermc.paper.persistence.PersistentDataContainerView;
import me.udnek.coreu.CoreU;
import me.udnek.coreu.custom.equipmentslot.universal.BaseUniversalSlot;
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

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public abstract class RPGUConstructableToggleAbility<ActivationContext> extends RPGUItemAbstractAbility<ActivationContext> implements RPGUItemToggleAbility<ActivationContext> {

    public static final NamespacedKey TOGGLE_KEY = new NamespacedKey(CoreU.getInstance(), "rpgu_toggle_ability_is_toggled");

    public void addPropertyLines(@NotNull PassiveAbilityLorePart componentable){
        getProperties().forEach(c -> c.describe(componentable));
    }

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
        addPropertyLines(lorePart);
    }

    protected boolean toggle(@NotNull CustomItem customItem, @NotNull Player player, @NotNull BaseUniversalSlot slot){
        AtomicBoolean newState = new AtomicBoolean();
        slot.changeItem(new Consumer<>() {
            @Override
            public void accept(ItemStack stack) {
                stack.editPersistentDataContainer(new Consumer<>() {
                    @Override
                    public void accept(PersistentDataContainer container) {
                        newState.set(!isToggled(container));
                        container.set(TOGGLE_KEY, PersistentDataType.BOOLEAN, newState.get());
                    }
                });
            }
        }, player);
        return newState.get();
    }

    protected boolean isToggled(@NotNull PersistentDataContainerView container){
        return container.getOrDefault(TOGGLE_KEY, PersistentDataType.BOOLEAN, false);
    }

    @Override
    public boolean isToggled(@NotNull CustomItem customItem, @NotNull Player player, @NotNull BaseUniversalSlot slot) {
        return isToggled(Objects.requireNonNull(slot.getItem(player)).getPersistentDataContainer());
    }
}
