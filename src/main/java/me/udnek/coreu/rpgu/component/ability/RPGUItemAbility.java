package me.udnek.coreu.rpgu.component.ability;

import me.udnek.coreu.custom.component.ComponentHolder;
import me.udnek.coreu.custom.component.CustomComponent;
import me.udnek.coreu.custom.equipmentslot.slot.CustomEquipmentSlot;
import me.udnek.coreu.custom.equipmentslot.universal.UniversalInventorySlot;
import me.udnek.coreu.custom.item.CustomItem;
import me.udnek.coreu.custom.registry.Registrable;
import me.udnek.coreu.rpgu.component.RPGUComponents;
import me.udnek.coreu.rpgu.component.ability.property.RPGUAbilityProperty;
import me.udnek.coreu.util.Either;
import me.udnek.coreu.util.LoreBuilder;
import net.kyori.adventure.translation.Translatable;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface RPGUItemAbility<ActivationContext> extends ComponentHolder<RPGUItemAbility<?>>, Translatable {

    int INFINITE_COOLDOWN = 100*60*60*20;
    int INFINITE_COOLDOWN_THRESHOLD = INFINITE_COOLDOWN/2;

    void activate(@NotNull CustomItem customItem,
                  @NotNull LivingEntity livingEntity,
                  @NotNull Either<UniversalInventorySlot, CustomEquipmentSlot.Single> slot,
                  @NotNull ActivationContext activationContext);

    default int getDefaultCooldown(@NotNull LivingEntity livingEntity){
        return getComponents().getOrDefault(RPGUComponents.ABILITY_COOLDOWN_TIME).get(livingEntity).intValue();
    }
    default void cooldown(@NotNull CustomItem customItem, @NotNull LivingEntity livingEntity, int cooldown){
        if (!(livingEntity instanceof Player player)) return;
        customItem.setCooldown(player, cooldown);
    }
    default void cooldown(@NotNull CustomItem customItem, @NotNull LivingEntity livingEntity){
        cooldown(customItem, livingEntity, getDefaultCooldown(livingEntity));
    }
    default void setMissUsageCooldown(@NotNull CustomItem customItem, @NotNull LivingEntity livingEntity){
        cooldown(customItem, livingEntity, (int) (getDefaultCooldown(livingEntity) * getComponents().getOrDefault(RPGUComponents.ABILITY_COOLDOWN_TIME).get(livingEntity)));
    }
    default void infiniteCooldown(@NotNull CustomItem customItem, @NotNull LivingEntity livingEntity){
        cooldown(customItem, livingEntity, INFINITE_COOLDOWN);
    }
    default double getCurrentCooldown(@NotNull CustomItem customItem, @NotNull LivingEntity livingEntity){
        if (!(livingEntity instanceof Player player)) return 0;
        int cooldown = customItem.getCooldown(player);
        if (cooldown > INFINITE_COOLDOWN_THRESHOLD) return Double.POSITIVE_INFINITY;
        return cooldown;
    }
    default boolean isOnInfiniteCooldown(@NotNull CustomItem customItem, @NotNull LivingEntity livingEntity){
        return Double.isInfinite(getCurrentCooldown(customItem, livingEntity));
    }

    default @NotNull List<RPGUAbilityProperty> getProperties(){
        return getComponents().getAllTyped(RPGUAbilityProperty.class);
    }

    void getLore(@NotNull LoreBuilder loreBuilder);
}
