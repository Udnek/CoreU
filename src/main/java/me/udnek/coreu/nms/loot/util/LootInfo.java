package me.udnek.coreu.nms.loot.util;

import me.udnek.coreu.nms.loot.condition.LootConditionWrapper;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public record LootInfo(float probability, @NotNull List<LootConditionWrapper> conditions) {

    public @NotNull LootInfo copyAndMultiplyProbability(float mul){
        return new LootInfo(probability*mul, new ArrayList<>(conditions));
    }

    public @NotNull LootInfo withExtraConditions(@NotNull List<LootConditionWrapper> conditions){
        List<LootConditionWrapper> newConditions = new ArrayList<>(conditions);
        newConditions.addAll(this.conditions);
        return new LootInfo(probability, newConditions);
    }
}
