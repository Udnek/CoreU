package me.udnek.coreu.custom.equipmentslot.slot;

import me.udnek.coreu.CoreU;
import me.udnek.coreu.custom.equipmentslot.universal.ActiveHandUniversalSlot;
import me.udnek.coreu.custom.equipmentslot.universal.BaseUniversalSlot;
import me.udnek.coreu.custom.equipmentslot.universal.UniversalInventorySlot;
import me.udnek.coreu.custom.registry.CustomRegistries;
import me.udnek.coreu.custom.registry.Registrable;
import net.kyori.adventure.translation.Translatable;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.function.Consumer;

public interface CustomEquipmentSlot extends Translatable, Registrable{

    SingleSlot MAIN_HAND = register(new ConstructableSingleSlot("mainhand", EquipmentSlotGroup.MAINHAND, EquipmentSlot.HAND, new BaseUniversalSlot(EquipmentSlot.HAND), "item.modifiers.mainhand"));
    SingleSlot OFF_HAND = register(new ConstructableSingleSlot("offhand", EquipmentSlotGroup.OFFHAND, EquipmentSlot.OFF_HAND, new BaseUniversalSlot(EquipmentSlot.OFF_HAND),"item.modifiers.offhand"));
    GroupSlot HAND = register(new ConstructableGroupSlot("hand",
            Set.of(MAIN_HAND, OFF_HAND),
            EquipmentSlotGroup.HAND, null, "item.modifiers.hand"));

    SingleSlot HEAD = register(new ConstructableSingleSlot("head", EquipmentSlotGroup.HEAD, EquipmentSlot.HEAD, new BaseUniversalSlot(EquipmentSlot.HEAD), "item.modifiers.head"));
    SingleSlot CHEST = register(new ConstructableSingleSlot("chest", EquipmentSlotGroup.CHEST, EquipmentSlot.CHEST, new BaseUniversalSlot(EquipmentSlot.CHEST), "item.modifiers.chest"));
    SingleSlot LEGS = register(new ConstructableSingleSlot("legs", EquipmentSlotGroup.LEGS, EquipmentSlot.LEGS, new BaseUniversalSlot(EquipmentSlot.LEGS),"item.modifiers.legs"));
    SingleSlot FEET = register(new ConstructableSingleSlot("feet", EquipmentSlotGroup.FEET, EquipmentSlot.FEET, new BaseUniversalSlot(EquipmentSlot.FEET), "item.modifiers.feet"));

    GroupSlot ARMOR = register(new ConstructableGroupSlot("armor",
            Set.of(HEAD, CHEST, LEGS, FEET),
            EquipmentSlotGroup.ARMOR, null, "item.modifiers.armor"));

    GroupSlot ANY_VANILLA = register(new ConstructableGroupSlot("any",
            Set.of(MAIN_HAND, OFF_HAND, HEAD, CHEST, LEGS, FEET),
            EquipmentSlotGroup.ANY, null, "item.modifiers.any"));

    SingleSlot ACTIVE_HAND = register(new ConstructableSingleSlot("active_hand", null, null, new ActiveHandUniversalSlot(), "slot." + new NamespacedKey(CoreU.getInstance(),"text").getNamespace() + ".active_hand"));

    GroupSlot DUMB_INVENTORY = register(new DumbInventorySlot("dumb_inventory"));

    SingleSlot BODY = register(new ConstructableSingleSlot("body", EquipmentSlotGroup.BODY, EquipmentSlot.BODY, new BaseUniversalSlot(EquipmentSlot.BODY), "item.modifiers.body"));

    SingleSlot SADDLE = register(new ConstructableSingleSlot("saddle", EquipmentSlotGroup.SADDLE, EquipmentSlot.SADDLE, new BaseUniversalSlot(EquipmentSlot.SADDLE), "item.modifiers.saddle"));

    static @NotNull CustomEquipmentSlot getFromVanilla(@NotNull EquipmentSlot slot){
        return switch (slot){
            case HEAD -> HEAD;
            case CHEST -> CHEST;
            case LEGS -> LEGS;
            case FEET -> FEET;
            case HAND -> MAIN_HAND;
            case OFF_HAND -> OFF_HAND;
            case BODY -> BODY;
            case SADDLE -> SADDLE;
        };
    }
    static @NotNull CustomEquipmentSlot getFromVanilla(@NotNull EquipmentSlotGroup slot){
        if (slot == EquipmentSlotGroup.HEAD) return HEAD;
        if (slot == EquipmentSlotGroup.CHEST) return CHEST;
        if (slot == EquipmentSlotGroup.LEGS) return LEGS;
        if (slot == EquipmentSlotGroup.FEET) return FEET;

        if (slot == EquipmentSlotGroup.HAND) return HAND;
        if (slot == EquipmentSlotGroup.OFFHAND) return OFF_HAND;
        if (slot == EquipmentSlotGroup.ARMOR) return ARMOR;
        if (slot == EquipmentSlotGroup.BODY) return BODY;
        if (slot == EquipmentSlotGroup.SADDLE) return SADDLE;
        return ANY_VANILLA;
    }

    boolean intersects(@NotNull CustomEquipmentSlot slot);
    boolean intersects(@NotNull UniversalInventorySlot slot);
    @Nullable EquipmentSlotGroup getVanillaGroup();
    @Nullable EquipmentSlot getVanillaSlot();
    void getAllUniversal(@NotNull Consumer<@NotNull UniversalInventorySlot> consumer);
    void getAllSingle(@NotNull Consumer<@NotNull SingleSlot> consumer);

    private static <Slot extends CustomEquipmentSlot> @NotNull Slot register(@NotNull Slot slot){
        return CustomRegistries.EQUIPMENT_SLOT.register(CoreU.getInstance(), slot);
    }
}
