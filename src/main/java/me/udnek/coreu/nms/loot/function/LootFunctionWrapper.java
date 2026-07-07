package me.udnek.coreu.nms.loot.function;

import me.udnek.coreu.nms.NmsWrapper;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;

import java.util.List;

@org.jspecify.annotations.NullMarked public class LootFunctionWrapper implements NmsWrapper<LootItemFunction>{

    public static List<LootFunctionWrapper> wrap(List<LootItemFunction> functions) {
        return functions.stream().map(LootFunctionWrapper::new).toList();
    }

    public static List<LootItemFunction> unwrap(List<LootFunctionWrapper> functions) {
        return functions.stream().map(LootFunctionWrapper::getNms).toList();
    }

    protected final LootItemFunction function;

    public LootFunctionWrapper(LootItemFunction function) {
        this.function = function;
    }

    @Override
    public LootItemFunction getNms() {
        return function;
    }
}
