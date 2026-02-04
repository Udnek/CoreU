package me.udnek.coreu.custom.equipment.slot;

import me.udnek.coreu.custom.equipment.universal.UniversalInventorySlot;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.function.Consumer;

@org.jspecify.annotations.NullMarked public class ConstructableGroupSlot extends AbstractCustomEquipmentSlot implements CustomEquipmentSlot.Group{
    protected final String translation;
    protected final EquipmentSlotGroup vanillaGroup;
    private final EquipmentSlot vanillaSlot;
    protected final Set<Single> subs;

    public ConstructableGroupSlot(String rawId, Set<Single> subs, @Nullable EquipmentSlotGroup vanillaGroup, @Nullable EquipmentSlot vanillaSlot, String translation) {
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
    public void getAllUniversal(Consumer<UniversalInventorySlot> consumer) {
        subs.forEach(singleSlot -> singleSlot.getAllUniversal(consumer));
    }

    @Override
    public String translationKey() {
        return translation;
    }
    @Override
    public void getAllSingle(Consumer<Single> consumer) {
        subs.forEach(consumer);
    }

    @Override
    public boolean intersects(LivingEntity entity, CustomEquipmentSlot other) {
        return other == this || subs.stream().anyMatch(s -> other == s);
    }

    @Override
    public boolean intersects(LivingEntity entity, UniversalInventorySlot slot) {
        return subs.stream().anyMatch(singleSlot -> singleSlot.intersects(entity, slot));
    }
}














