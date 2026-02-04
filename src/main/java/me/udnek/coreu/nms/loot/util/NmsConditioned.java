package me.udnek.coreu.nms.loot.util;

import me.udnek.coreu.nms.loot.condition.LootConditionWrapper;

import java.util.List;

@org.jspecify.annotations.NullMarked public  interface NmsConditioned{
    List<LootConditionWrapper> getConditions();
    void setConditions(List<LootConditionWrapper> conditions);
}
