package me.udnek.coreu.custom.equipment.universal;

import com.google.common.base.Preconditions;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@org.jspecify.annotations.NullMarked public class BaseUniversalSlot implements UniversalInventorySlot{
    public final @Nullable Integer slot;
    public final @Nullable EquipmentSlot equipmentSlot;

    public BaseUniversalSlot(int slot){
        this.slot = slot;
        equipmentSlot = null;
    }

    public BaseUniversalSlot(EquipmentSlot slot){
        this.equipmentSlot = slot;
        this.slot = null;
    }

    @Override
    public @Nullable Integer integerSlotToCompare(LivingEntity entity) {
        if (slot != null) return slot;
        if (entity instanceof  Player){
            return switch (Objects.requireNonNull(equipmentSlot)) {
                case HAND -> 98;
                case OFF_HAND -> 40;
                case FEET -> 36;
                case LEGS -> 37;
                case CHEST -> 38;
                case HEAD -> 39;
                case BODY -> 105;
                case SADDLE -> 400;
            };
        }
        return switch (Objects.requireNonNull(equipmentSlot)) {
            case HAND -> 98;
            case OFF_HAND -> 99;
            case FEET -> 100;
            case LEGS -> 101;
            case CHEST -> 102;
            case HEAD -> 103;
            case BODY -> 105;
            case SADDLE -> 400;
        };
    }

    @Override
    public @Nullable ItemStack getItem(LivingEntity entity) {
        if (slot != null && entity instanceof InventoryHolder inventoryHolder && 0 <= slot && slot <= inventoryHolder.getInventory().getSize()){
            return inventoryHolder.getInventory().getItem(slot);
        } else if (equipmentSlot != null) {
            if (entity instanceof Player player){
                return player.getInventory().getItem(equipmentSlot);
            }else  {
                EntityEquipment equipment = entity.getEquipment();
                if (equipment == null) return null;
                return equipment.getItem(equipmentSlot);
            }
        }
        return null;
    }

    @Override
    public void setItem(@Nullable ItemStack itemStack, LivingEntity entity) {
        if (slot != null){
            if (entity instanceof InventoryHolder inventoryHolder){
                if (0 <= slot && slot <= inventoryHolder.getInventory().getSize()) {
                    inventoryHolder.getInventory().setItem(slot, itemStack);
                }else {
                    throw new RuntimeException("Incorrect slot for the inventoryHolder");
                }
            }else {
                throw new RuntimeException("The integer slot is only available to the inventoryHolder");
            }
        } else if (equipmentSlot != null) {
            if (entity instanceof Player player){
                player.getInventory().setItem(equipmentSlot, itemStack);
            } else {
                EntityEquipment equipment = entity.getEquipment();
                Preconditions.checkNotNull(equipment, "Equipment for ", entity, " - null");
                equipment.setItem(equipmentSlot, itemStack);
            }
        } else {
            throw new RuntimeException("No slot added");
        }
    }

    @Override
    public String toString() {
        return "BaseUniversalSlot{" +
                "slot=" + slot +
                ", equipmentSlot=" + equipmentSlot +
                '}';
    }

    @Override
    public boolean equals(Object obj) throws RuntimeException {
        throw new RuntimeException("don't use equals method with UniversalSlot");
    }
}
