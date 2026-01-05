package me.udnek.coreu.nms.loot.function;

import me.udnek.coreu.nms.NmsWrapper;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LootFunctionWrapper implements NmsWrapper<@NotNull LootItemFunction> {

    public static @NotNull List<LootFunctionWrapper> wrap(@NotNull List<LootItemFunction> functions) {
        return functions.stream().map(LootFunctionWrapper::new).toList();
    }

    public static @NotNull List<LootItemFunction> unwrap(@NotNull List<LootFunctionWrapper> functions) {
        return functions.stream().map(LootFunctionWrapper::getNms).toList();
    }

    protected @NotNull LootItemFunction function;

    public LootFunctionWrapper(@NotNull LootItemFunction function) {
        this.function = function;
    }

    @Override
    public @NotNull LootItemFunction getNms() {
        return function;
    }
}
