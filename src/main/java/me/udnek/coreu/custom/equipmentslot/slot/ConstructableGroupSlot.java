package me.udnek.coreu.custom.equipmentslot.slot;

import me.udnek.coreu.custom.equipmentslot.universal.UniversalInventorySlot;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.function.Consumer;

public class ConstructableGroupSlot extends AbstractCustomEquipmentSlot implements CustomEquipmentSlot.Group {
    protected final String translation;
    protected final EquipmentSlotGroup vanillaGroup;
    private final EquipmentSlot vanillaSlot;
    protected final Set<Single> subs;

    public ConstructableGroupSlot(@NotNull String rawId, @NotNull Set<@NotNull Single> subs, @Nullable EquipmentSlotGroup vanillaGroup, @Nullable EquipmentSlot vanillaSlot, @NotNull String translation) {
        super(rawId);
        this.translation = translation;
        this.vanillaGroup = vanillaGroup;
        this.vanillaSlot = vanillaSlot;
        this.subs = subs;
    }
    @Override
    public @Nullable EquipmentSlotGroup getVanillaGroup() {
        return vanillaGroup;
    }

    @Override
    @Nullable
    public EquipmentSlot getVanillaSlot() {return vanillaSlot;}

    @Override
    public void getAllUniversal(@NotNull Consumer<@NotNull UniversalInventorySlot> consumer) {
        subs.forEach(singleSlot -> singleSlot.getAllUniversal(consumer));
    }

    @Override
    public @NotNull String translationKey() {
        return translation;
    }
    @Override
    public void getAllSingle(@NotNull Consumer<@NotNull Single> consumer) {
        subs.forEach(consumer);
    }

    @Override
    public boolean intersects(@NotNull LivingEntity entity, @NotNull CustomEquipmentSlot other) {
        return other == this || subs.stream().anyMatch(s -> other == s);
    }

    @Override
    public boolean intersects(@NotNull LivingEntity entity, @NotNull UniversalInventorySlot slot) {
        return subs.stream().anyMatch(singleSlot -> singleSlot.intersects(entity, slot));
    }
}














