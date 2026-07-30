package me.udnek.coreu.nms.loot.util;

import me.udnek.coreu.nms.loot.condition.LootConditionWrapper;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public interface NmsConditioned{
    List<LootConditionWrapper> getConditions();
    void setConditions(List<LootConditionWrapper> conditions);
}
