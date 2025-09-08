package me.udnek.coreu.custom.equipmentslot.universal;

import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface UniversalInventorySlot {
    static void iterateThroughNotEmpty(@NotNull BiConsumer<BaseUniversalSlot, ItemStack> consumer, @NotNull LivingEntity entity) {
        iterateThroughAll((universalInventorySlot, itemStack) -> {
            if (itemStack == null || itemStack.isEmpty()) return;
            consumer.accept(universalInventorySlot, itemStack);
        }, entity);
    }

    static void iterateThroughAll(@NotNull BiConsumer<BaseUniversalSlot, ItemStack> consumer, @NotNull LivingEntity entity) {
        if (entity instanceof InventoryHolder inventoryHolder) {
            @Nullable ItemStack[] contents = inventoryHolder.getInventory().getContents();
            for (int i = 0; i < contents.length; i++) {
                ItemStack content = contents[i];
                consumer.accept(new BaseUniversalSlot(i), content);
            }
        } else {
            EntityEquipment equipment = Objects.requireNonNull(entity.getEquipment());
            consumer.accept(new BaseUniversalSlot(EquipmentSlot.HAND), equipment.getItemInMainHand());
            consumer.accept(new BaseUniversalSlot(EquipmentSlot.OFF_HAND), equipment.getItemInOffHand());
            consumer.accept(new BaseUniversalSlot(EquipmentSlot.HEAD), equipment.getHelmet());
            consumer.accept(new BaseUniversalSlot(EquipmentSlot.CHEST), equipment.getChestplate());
            consumer.accept(new BaseUniversalSlot(EquipmentSlot.LEGS), equipment.getLeggings());
            consumer.accept(new BaseUniversalSlot(EquipmentSlot.FEET), equipment.getBoots());
        }
    }
    @Nullable Integer integerSlotToCompare(@NotNull LivingEntity entity);

    default boolean equals(@NotNull LivingEntity entity, @NotNull UniversalInventorySlot other){
        Integer s = integerSlotToCompare(entity);
        return (s != null && s.equals(other.integerSlotToCompare(entity)));
    }

    @Nullable ItemStack getItem(@NotNull LivingEntity entity);

    void setItem(@Nullable ItemStack itemStack, @NotNull LivingEntity entity);

    default void modifyItem(@NotNull Consumer<ItemStack> consumer, @NotNull LivingEntity entity) {
        ItemStack itemStack = getItem(entity);
        consumer.accept(itemStack);
        setItem(itemStack, entity);
    }

    default void addItem(int count, @NotNull LivingEntity entity) {
        modifyItem(itemStack -> itemStack.add(count), entity);}

    default void addItem(@NotNull LivingEntity entity) {addItem(1, entity);}

    default void takeItem(int count, @NotNull LivingEntity entity){addItem(-count, entity);}

    default void takeItem(@NotNull LivingEntity entity) {takeItem(1, entity);}
}
