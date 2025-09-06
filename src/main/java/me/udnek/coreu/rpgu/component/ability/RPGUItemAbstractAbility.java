package me.udnek.coreu.rpgu.component.ability;

import me.udnek.coreu.custom.component.AbstractComponentHolder;
import me.udnek.coreu.custom.equipmentslot.slot.CustomEquipmentSlot;
import me.udnek.coreu.custom.equipmentslot.universal.UniversalInventorySlot;
import me.udnek.coreu.custom.item.CustomItem;
import me.udnek.coreu.rpgu.component.RPGUComponents;
import me.udnek.coreu.util.Either;
import me.udnek.coreu.util.LoreBuilder;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;

public abstract class RPGUItemAbstractAbility<ActivationContext> extends AbstractComponentHolder<RPGUItemAbility<?>> implements RPGUItemAbility<ActivationContext> {

    private static final int INFINITE_COOLDOWN = 100*60*60*20;
    private static final int INFINITE_COOLDOWN_THRESHOLD = INFINITE_COOLDOWN/2;

    public void activate(@NotNull CustomItem customItem,
                         @NotNull LivingEntity livingEntity,
                         boolean canselIfCooldown,
                         @NotNull Either<UniversalInventorySlot, CustomEquipmentSlot.Single> slot,
                         @NotNull ActivationContext activationContext)
    {
        if (!(livingEntity instanceof Player player)) {
            action(customItem, livingEntity, slot, activationContext);
            return;
        }
        if (customItem.hasCooldown(player)) {
            if (canselIfCooldown && activationContext instanceof Cancellable cancellable){
                cancellable.setCancelled(true);
            }
            return;
        }
        ActionResult result = action(customItem, player, slot, activationContext);
        if (result == ActionResult.INFINITE_COOLDOWN) infiniteCooldown(customItem, player);
        else if (result == ActionResult.FULL_COOLDOWN || result == ActionResult.PENALTY_COOLDOWN){
            double cooldown = getComponents().getOrDefault(RPGUComponents.ABILITY_COOLDOWN_TIME).get(player);
            if (result == ActionResult.PENALTY_COOLDOWN) cooldown = cooldown * getComponents().getOrDefault(RPGUComponents.ABILITY_MISS_USAGE_COOLDOWN_MULTIPLIER).get(player);
            if (cooldown > 0) cooldown(customItem, player, (int) Math.ceil(cooldown));
        }
    }

    @Override
    public void activate(@NotNull CustomItem customItem, @NotNull LivingEntity livingEntity, @NotNull Either<UniversalInventorySlot, CustomEquipmentSlot.Single> slot, @NotNull ActivationContext activationContext){
        activate(customItem, livingEntity, false, slot, activationContext);
    }

    protected abstract @NotNull ActionResult action(@NotNull CustomItem customItem, @NotNull LivingEntity livingEntity,
                                                    @NotNull Either<UniversalInventorySlot, CustomEquipmentSlot.Single> slot, @NotNull ActivationContext activationContext);


    @Override
    public void getLore(@NotNull LoreBuilder loreBuilder) {}

    public enum ActionResult {
        INFINITE_COOLDOWN,
        FULL_COOLDOWN,
        PENALTY_COOLDOWN,
        NO_COOLDOWN
    }
}
