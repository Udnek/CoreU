package me.udnek.coreu.nms.loot.entry;

import me.udnek.coreu.nms.NmsUtils;
import me.udnek.coreu.nms.loot.util.NmsFields;
import me.udnek.coreu.nms.loot.util.NmsFunctioned;
import me.udnek.coreu.nms.loot.util.NmsLootConditionsContainer;
import me.udnek.coreu.nms.loot.util.NmsLootFunctionsContainer;
import me.udnek.coreu.util.Reflex;
import net.minecraft.util.Util;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntry;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootTable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class NmsSingletonEntryContainer implements NmsLootEntryContainer, NmsFunctioned {

    LootPoolSingletonContainer supply;

    public NmsSingletonEntryContainer(@NotNull LootPoolSingletonContainer supply) {
        this.supply = supply;
    }

    public static @Nullable NmsSingletonEntryContainer fromVanilla(@NotNull LootTable lootTable, Predicate<ItemStack> predicate){
        for (LootPoolSingletonContainer container : NmsUtils.getAllSingletonContainers(NmsUtils.toNmsLootTable(lootTable))) {
            LootPoolEntry entry = NmsUtils.getEntry(container);
            List<net.minecraft.world.item.ItemStack> loot = new ArrayList<>();
            NmsUtils.getPossibleLoot(entry, loot::add);
            for (net.minecraft.world.item.ItemStack stack : loot) {
                ItemStack itemStack = NmsUtils.toBukkitItemStack(stack);
                if (predicate.test(itemStack)) return new NmsSingletonEntryContainer(container);
            }
        }
        return null;
    }

    @Override
    public @NotNull NmsLootConditionsContainer getConditions() {
        return new NmsLootConditionsContainer(Reflex.getFieldValue(supply, NmsFields.CONDITIONS));
    }

    @Override
    public void setConditions(@NotNull NmsLootConditionsContainer conditions) {
        List<LootItemCondition> list = conditions.get();
        Reflex.setFieldValue(supply, NmsFields.CONDITIONS, list);
        Reflex.setFieldValue(supply, NmsFields.COMPOSITE_CONDITIONS, Util.allOf(list));
    }

    @Override
    public @NotNull NmsLootFunctionsContainer getFunctions() {
        return new NmsLootFunctionsContainer(Reflex.getFieldValue(supply, NmsFields.FUNCTIONS));
    }

    @Override
    public void setFunctions(@NotNull NmsLootFunctionsContainer functions) {
        List<LootItemFunction> list = functions.get();
        Reflex.setFieldValue(supply, NmsFields.CONDITIONS, list);
        Reflex.setFieldValue(supply, NmsFields.COMPOSITE_CONDITIONS, LootItemFunctions.compose(list));
    }

    public void setWeight(int n){
        Reflex.setFieldValue(supply, NmsFields.WEIGHT, n);
    }
    public int getWeight(){
        return (int) Reflex.getFieldValue(supply, NmsFields.WEIGHT);
    }
    public void setQuality(int n){
        Reflex.setFieldValue(supply, NmsFields.QUALITY, n);
    }
    public int getQuality(int n){
        return (int) Reflex.getFieldValue(supply, NmsFields.QUALITY);
    }

    @Override
    public LootPoolSingletonContainer get() {
        return supply;
    }
}
