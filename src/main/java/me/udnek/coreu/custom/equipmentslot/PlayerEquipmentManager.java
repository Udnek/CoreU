package me.udnek.coreu.custom.equipmentslot;

import me.udnek.coreu.util.TickingTask;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class PlayerEquipmentManager extends TickingTask {

    public static final int DELAY = 1;
    private static PlayerEquipmentManager instance;

    private final Map<Player, PlayerEquipment> playersData = new HashMap<>();

    public static @NotNull PlayerEquipmentManager getInstance() {
        if (instance == null) instance = new PlayerEquipmentManager();
        return instance;
    }

    private PlayerEquipmentManager(){}

    public @NotNull PlayerEquipment getData(@NotNull Player player){
        PlayerEquipment equipment = playersData.get(player);
        if (equipment != null) return equipment;
        PlayerEquipment playerEquipment = new PlayerEquipment();
        playersData.put(player, playerEquipment);
        return playerEquipment;
    }


    public void run(){
        Player toRemovePlayer = null;
        for (Map.Entry<Player, PlayerEquipment> entry : playersData.entrySet()) {
            Player player = entry.getKey();
            PlayerEquipment equipment = entry.getValue();

            if (!player.isOnline()) {
                toRemovePlayer = player;
                continue;
            }
            equipment.getEquipment((
                    slot, customItem) ->
                    customItem.getComponents().getAllTyped(EquippableItem.class).forEach(equippableItem -> {
                        //System.out.println(Bukkit.getCurrentTick() + " " + customItem.getId() + " " + equippableItem);
                        equippableItem.tick(customItem, player, slot, DELAY);
                    }));
        }
        if (toRemovePlayer != null) playersData.remove(toRemovePlayer);
    }

    @Override
    public int getDelay() {
        return DELAY;
    }
}







