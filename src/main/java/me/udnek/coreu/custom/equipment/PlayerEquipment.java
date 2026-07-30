package me.udnek.coreu.custom.equipment;


import me.udnek.coreu.custom.equipment.universal.BaseUniversalSlot;
import me.udnek.coreu.custom.item.CustomItem;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@NullMarked public class PlayerEquipment{

    private final HashMap<BaseUniversalSlot, CustomItem> equipment = new HashMap<>();

    public void getEquipment(EquipmentConsumer consumer) {
        equipment.forEach(consumer::accept);
    }


    public Map.@Nullable Entry<BaseUniversalSlot, CustomItem> getEntryBySlot(Player player, BaseUniversalSlot slot){
        return equipment.entrySet().stream()
                .filter(entry -> entry.getKey().equals(player, slot))
                .findFirst().orElse(null);
    }


    public void setByExactSlot(BaseUniversalSlot slot, @Nullable CustomItem customItem) {
        if (customItem == null) {
            equipment.remove(slot);
            return;
        }
        equipment.put(slot, customItem);
    }


    public interface EquipmentConsumer{
        void accept(BaseUniversalSlot slot, CustomItem customItem);
    }
}
