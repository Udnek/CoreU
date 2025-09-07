package me.udnek.coreu.custom.equipmentslot.universal;

import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ActiveHandUniversalSlot implements UniversalInventorySlot{


    @Override
    public @Nullable Integer integerSlotToCompare() {
        return 98;
    }

    @Override
    public @Nullable ItemStack getItem(@NotNull LivingEntity entity) {
        if (!entity.isHandRaised()) return null;
        return new BaseUniversalSlot(entity.getActiveItemHand()).getItem(entity);
    }

    @Override
    public void setItem(@Nullable ItemStack itemStack, @NotNull LivingEntity entity) {
        if (!entity.isHandRaised()) return;
        new BaseUniversalSlot(entity.getActiveItemHand()).setItem(itemStack, entity);
    }
}
