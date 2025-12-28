package me.udnek.coreu.custom.equipment.universal;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ActiveHandUniversalSlot implements UniversalInventorySlot{


    @Override
    public @Nullable Integer integerSlotToCompare(@NotNull LivingEntity entity) {
        if (!entity.isHandRaised()) return null;
        if (entity instanceof Player player){
            return player.getInventory().getHeldItemSlot();
        }
        return 98; // MAIN HAND
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

    @Override
    public boolean equals(Object obj) throws RuntimeException {
        throw new RuntimeException("don't use equals method with UniversalSlot");
    }
}
