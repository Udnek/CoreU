package me.udnek.coreu.nms.loot.util;

import me.udnek.coreu.nms.loot.function.LootFunctionWrapper;

import java.util.List;

@org.jspecify.annotations.NullMarked public  interface NmsFunctioned{
    List<LootFunctionWrapper> getFunctions();
    void setFunctions(List<LootFunctionWrapper> functions);
}
