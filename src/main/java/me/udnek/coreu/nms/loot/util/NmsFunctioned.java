package me.udnek.coreu.nms.loot.util;

import me.udnek.coreu.nms.loot.function.LootFunctionWrapper;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public interface NmsFunctioned{
    List<LootFunctionWrapper> getFunctions();
    void setFunctions(List<LootFunctionWrapper> functions);
}
