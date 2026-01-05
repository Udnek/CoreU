package me.udnek.coreu.nms.loot.util;

import me.udnek.coreu.nms.loot.function.LootFunctionWrapper;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface NmsFunctioned {
    @NotNull List<LootFunctionWrapper> getFunctions();
    void setFunctions(@NotNull List<LootFunctionWrapper> functions);
}
