package me.udnek.coreu.nms.loot.util;

import me.udnek.coreu.nms.loot.condition.LootConditionWrapper;

import java.util.ArrayList;
import java.util.List;

@org.jspecify.annotations.NullMarked public  record LootInfo(float probability, List<LootConditionWrapper> conditions){

    public LootInfo copyAndMultiplyProbability(float mul){
        return new LootInfo(probability*mul, new ArrayList<>(conditions));
    }

    public LootInfo withExtraConditions(List<LootConditionWrapper> conditions){
        List<LootConditionWrapper> newConditions = new ArrayList<>(conditions);
        newConditions.addAll(this.conditions);
        return new LootInfo(probability, newConditions);
    }
}
