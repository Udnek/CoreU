package me.udnek.coreu.nms.loot.pool;

import me.udnek.coreu.nms.NmsContainer;
import me.udnek.coreu.nms.NmsUtils;
import me.udnek.coreu.nms.loot.entry.NmsLootEntryContainer;
import me.udnek.coreu.nms.loot.util.NmsFields;
import me.udnek.coreu.nms.loot.util.NmsLootConditionsContainer;
import me.udnek.coreu.nms.loot.util.NmsLootFunctionsContainer;
import me.udnek.coreu.util.Reflex;
import net.minecraft.util.Util;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.bukkit.loot.LootTable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class NmsDefaultLootPoolContainer extends NmsContainer<LootPool> implements NmsLootPoolContainer{

    public NmsDefaultLootPoolContainer(@NotNull LootPool supply) {super(supply);}

    public static @Nullable NmsDefaultLootPoolContainer fromVanilla(@NotNull LootTable lootTable, int n){
        List<LootPool> pools = NmsUtils.getPools(NmsUtils.toNmsLootTable(lootTable));
        return new NmsDefaultLootPoolContainer(pools.get(n));
    }

    public List<LootPoolEntryContainer> getEntries(){
        return (List<LootPoolEntryContainer>) Reflex.getFieldValue(supply, NmsFields.ENTRIES);
    }

    @Override
    public void addEntry(@NotNull NmsLootEntryContainer entry) {
        List<LootPoolEntryContainer> newEntries = new ArrayList<>(getEntries());
        newEntries.add(entry.get());
        Reflex.setFieldValue(supply, NmsFields.ENTRIES, newEntries);
    }

    @Override
    public void removeEntry(int n) {
        List<LootPoolEntryContainer> newEntries = new ArrayList<>(getEntries());
        newEntries.remove(n);
        Reflex.setFieldValue(supply, NmsFields.ENTRIES, newEntries);
    }
    @Override
    public @NotNull NmsLootEntryContainer getEntry(int n) {
        return NmsLootEntryContainer.from(getEntries().get(n));
    }

    @Override
    public @NotNull NmsLootConditionsContainer getConditions() {
        return new NmsLootConditionsContainer((List<LootItemCondition>) Reflex.getFieldValue(supply, NmsFields.CONDITIONS));
    }

    @Override
    public void setConditions(@NotNull NmsLootConditionsContainer conditions) {
        List<LootItemCondition> list = conditions.get();
        Reflex.setFieldValue(supply, NmsFields.CONDITIONS, list);
        Reflex.setFieldValue(supply, NmsFields.COMPOSITE_CONDITIONS, Util.allOf(list));
    }

    @Override
    public @NotNull NmsLootFunctionsContainer getFunctions() {
        return new NmsLootFunctionsContainer((List<LootItemFunction>) Reflex.getFieldValue(supply, NmsFields.FUNCTIONS));
    }

    @Override
    public void setFunctions(@NotNull NmsLootFunctionsContainer functions) {
        List<LootItemFunction> list = functions.get();
        Reflex.setFieldValue(supply, NmsFields.FUNCTIONS, list);
        Reflex.setFieldValue(supply, NmsFields.COMPOSITE_FUNCTIONS, LootItemFunctions.compose(list));
    }
}
