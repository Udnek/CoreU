package me.udnek.coreu.custom.equipmentslot;


import me.udnek.coreu.custom.equipmentslot.universal.BaseUniversalSlot;
import me.udnek.coreu.custom.item.CustomItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class PlayerEquipment {

    private final HashMap<BaseUniversalSlot, CustomItem> equipment = new HashMap<>();

    public void getEquipment(@NotNull EquipmentConsumer consumer) {
        equipment.forEach(consumer::accept);
    }


    public @Nullable Map.Entry<BaseUniversalSlot, CustomItem> getFirst(@NotNull BaseUniversalSlot slot){
        return equipment.entrySet().stream().filter(entry -> entry.getKey().equals(slot)).findFirst().orElse(null);
    }

    public void set(@NotNull BaseUniversalSlot slot, @Nullable CustomItem customItem) {
        if (customItem == null) {
            equipment.remove(slot);
            return;
        }
        equipment.put(slot, customItem);
    }

    public @Nullable CustomItem get(@NotNull BaseUniversalSlot slot){
        return equipment.getOrDefault(slot, null);
    }

    public interface EquipmentConsumer{
        void accept(@NotNull BaseUniversalSlot slot, @NotNull CustomItem customItem);
    }
}
