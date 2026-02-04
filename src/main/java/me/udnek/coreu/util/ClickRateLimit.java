package me.udnek.coreu.util;

import org.bukkit.Bukkit;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlotGroup;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

@org.jspecify.annotations.NullMarked public class ClickRateLimit{

    private static final HashMap<UUID, Trigger> triggers = new HashMap<>();

    public static boolean triggerAndCanUse(PlayerInteractEvent event, int rateLimit, boolean blockBothHands){
        if (event.getHand() == null) return true;
        UUID playerId = event.getPlayer().getUniqueId();
        var trigger = triggers.get(playerId);

        // NULL OR EXPIRED
        if (trigger == null || trigger.isExpired()){
            triggers.put(playerId, Trigger.fromEvent(event, rateLimit, blockBothHands));
            return true;
        }
        // NOT EXPIRED

        // TRIGGERED WITH SAME HAND OR BOTH
        if (trigger.hand == event.getHand().getGroup() || trigger.hand == EquipmentSlotGroup.HAND){
            return false;
        }
        // TRIGGERED WITH OPPOSITE HAND
        triggers.put(playerId, Trigger.withBothHands(trigger));
        return true;
    }

    public static boolean triggerAndCanUse(PlayerInteractEvent event){
        return triggerAndCanUse(event, 1, false);
    }


    private record Trigger(EquipmentSlotGroup hand, long endsIn){

        static Trigger fromEvent(PlayerInteractEvent event, int rateLimit, boolean blockBothHands){
            if (blockBothHands){
                return new Trigger(EquipmentSlotGroup.HAND, Bukkit.getCurrentTick()+rateLimit);
            }
            return new Trigger(Objects.requireNonNull(event.getHand()).getGroup(), Bukkit.getCurrentTick()+rateLimit);
        }

        static Trigger withBothHands(Trigger old){
            return new Trigger(EquipmentSlotGroup.HAND, old.endsIn);
        }

        boolean isExpired(){
            return endsIn <= Bukkit.getCurrentTick();
        }
    }
}
