package me.udnek.coreu.custom.equipment;

import me.udnek.coreu.util.TickingTask;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@org.jspecify.annotations.NullMarked public class PlayerEquipmentManager extends TickingTask{

    public static final int DELAY = 1;
    private static @Nullable PlayerEquipmentManager instance;

    private final Map<Player, PlayerEquipment> playersData = new HashMap<>();

    public static PlayerEquipmentManager getInstance() {
        if (instance == null) instance = new PlayerEquipmentManager();
        return instance;
    }

    private PlayerEquipmentManager(){}

    public PlayerEquipment getData(Player player){
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







