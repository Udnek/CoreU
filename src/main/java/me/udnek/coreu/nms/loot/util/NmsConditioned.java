package me.udnek.coreu.nms.loot.util;

import me.udnek.coreu.nms.loot.condition.LootConditionWrapper;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface NmsConditioned {
    @NotNull List<LootConditionWrapper> getConditions();
    void setConditions(@NotNull List<LootConditionWrapper> conditions);
}
