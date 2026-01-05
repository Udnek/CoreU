package me.udnek.coreu.nms.loot.table;

import me.udnek.coreu.nms.loot.function.LootFunctionWrapper;
import me.udnek.coreu.nms.loot.pool.DefaultPoolWrapper;
import me.udnek.coreu.nms.loot.pool.PoolWrapper;
import me.udnek.coreu.nms.loot.util.LootInfo;
import me.udnek.coreu.nms.loot.util.NmsFields;
import me.udnek.coreu.util.Reflex;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class DefaultLootTableWrapper implements LootTableWrapper {

    @NotNull LootTable lootTable;

    public DefaultLootTableWrapper(@NotNull LootTable lootTable) {
        this.lootTable = lootTable;
    }

    @Override
    public @NotNull DefaultLootTableWrapper copy(){
        LootTable newLootTable = Reflex.construct(
                Reflex.getFirstConstructor(LootTable.class),

                Reflex.getFieldValue(lootTable, "paramSet"),
                Reflex.getFieldValue(lootTable, "randomSequence"),
                new ArrayList<>(Reflex.<List<LootPool>>getFieldValue(lootTable, NmsFields.POOLS)),
                new ArrayList<>(Reflex.<List<LootItemFunction>>getFieldValue(lootTable, NmsFields.FUNCTIONS))
        );
        return new DefaultLootTableWrapper(newLootTable);
    }

    @Override
    public void extractItems(@NotNull LootInfo base, @NotNull Consumer<Pair<ItemStack, LootInfo>> consumer) {
        for (PoolWrapper pool : getPools()) {
            pool.extractItems(base.copyAndMultiplyProbability(1), consumer);
        }
    }

    @Override
    public void addPool(@NotNull PoolWrapper container) {
        List<LootPool> pools = new ArrayList<>(Reflex.getFieldValue(lootTable, NmsFields.POOLS));
        pools.add(container.getNms());
        Reflex.setFieldValue(lootTable, NmsFields.POOLS, pools);
    }

    @Override
    public @NotNull PoolWrapper getPool(int n) {
        return new DefaultPoolWrapper(Reflex.<List<LootPool>>getFieldValue(lootTable, NmsFields.POOLS).get(n));
    }

    @Override
    public @NotNull List<PoolWrapper> getPools() {
        return Reflex.<List<LootPool>>getFieldValue(lootTable, NmsFields.POOLS).stream()
                .map(e -> (PoolWrapper) new DefaultPoolWrapper(e)).toList();
    }

    @Override
    public void removePool(int n) {
        List<LootPool> pools = new ArrayList<>(Reflex.getFieldValue(lootTable, NmsFields.POOLS));
        pools.remove(n);
        Reflex.setFieldValue(lootTable, NmsFields.POOLS, pools);
    }

    @Override
    public @NotNull List<LootFunctionWrapper> getFunctions() {
        return LootFunctionWrapper.wrap(Reflex.getFieldValue(lootTable, NmsFields.FUNCTIONS));
    }

    @Override
    public void setFunctions(@NotNull List<LootFunctionWrapper> functions) {
        List<LootItemFunction> value = LootFunctionWrapper.unwrap(functions);
        Reflex.setFieldValue(lootTable, NmsFields.FUNCTIONS, value);
        Reflex.setFieldValue(lootTable, NmsFields.COMPOSITE_FUNCTIONS, LootItemFunctions.compose(value));
    }

    @Override
    public @NotNull LootTable getNms() {
        return lootTable;
    }
}
