package me.udnek.coreu.nms.loot.pool;

import com.google.common.base.Preconditions;
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
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootTable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class NmsLootPoolBuilder implements NmsLootPoolContainer{

    protected Integer rolls = 1;
    protected int bonusRolls = 0;
    protected List<LootPoolEntryContainer> entries = new ArrayList<>();
    protected List<LootItemCondition> conditions;
    protected List<LootItemFunction> functions;

    public NmsLootPoolBuilder(@NotNull NmsLootEntryContainer...entries){
        Arrays.asList(entries).forEach(nms -> this.entries.add(nms.get()));
    }

    public @NotNull NmsLootPoolBuilder rolls(int rolls){
        this.rolls = rolls; return this;
    }

    public @NotNull NmsLootPoolBuilder bonusRolls(int bonusRolls){
        this.bonusRolls = bonusRolls; return this;
    }

    @Override
    public @NotNull NmsLootEntryContainer getEntry(int n) {
        return NmsLootEntryContainer.from(entries.get(n));
    }

    @Override
    public void addEntry(@NotNull NmsLootEntryContainer entry) {entries.add(entry.get());}

    @Override
    public void removeEntry(int n) {entries.remove(n);}

    protected LootPool getPoolByPredicate(@NotNull LootTable lootTable, Predicate<ItemStack> predicate){
        LootPool found = NmsUtils.getLootPoolByPredicate(
                NmsUtils.toNmsLootTable(lootTable),
                itemStack -> predicate.test(NmsUtils.toBukkitItemStack(itemStack)));

        return found;
    }
    public @NotNull NmsLootPoolBuilder copyConditionsFrom(@NotNull LootTable lootTable, Predicate<ItemStack> predicate){
        LootPool found = getPoolByPredicate(lootTable, predicate);
        Preconditions.checkArgument(found != null, "Pool not found!");
        this.conditions = (List<LootItemCondition>) Reflex.getFieldValue(found, NmsFields.CONDITIONS);
        return this;
    }
    public @NotNull NmsLootPoolBuilder copyFunctionsFrom(@NotNull LootTable lootTable, Predicate<ItemStack> predicate){
        LootPool found = getPoolByPredicate(lootTable, predicate);
        Preconditions.checkArgument(found != null, "Pool not found!");
        this.functions = (List<LootItemFunction>) Reflex.getFieldValue(found, NmsFields.FUNCTIONS);
        return this;
    }

    @Override
    public @NotNull NmsLootConditionsContainer getConditions() {
        return new NmsLootConditionsContainer(conditions);
    }

    @Override
    public void setConditions(@NotNull NmsLootConditionsContainer conditions) {
        this.conditions = conditions.get();
    }

    @Override
    public @NotNull NmsLootFunctionsContainer getFunctions() {
        return new NmsLootFunctionsContainer(functions);
    }

    @Override
    public void setFunctions(@NotNull NmsLootFunctionsContainer functions) {
        this.functions = functions.get();
    }

    public void validate(){
        for (LootPoolEntryContainer container : entries) {
            Preconditions.checkArgument(container != null, "Container can not be null!");
        }
    }

    public LootPool build(){
        validate();

        LootPool.Builder builder = LootPool.lootPool()
                .setRolls(ConstantValue.exactly(rolls))
                .setBonusRolls(ConstantValue.exactly(bonusRolls));
        LootPool lootPool = builder.build();
        if (conditions != null){
            Reflex.setFieldValue(lootPool, NmsFields.CONDITIONS, conditions);
            Reflex.setFieldValue(lootPool, NmsFields.COMPOSITE_CONDITIONS, Util.allOf(conditions));
        }
        if (functions != null){
            Reflex.setFieldValue(lootPool, NmsFields.FUNCTIONS, functions);
            Reflex.setFieldValue(lootPool, NmsFields.COMPOSITE_FUNCTIONS, LootItemFunctions.compose(functions));
        }
        Reflex.setFieldValue(lootPool, "entries", new ArrayList<>(entries));
        return lootPool;
    }



    @Override
    public @NotNull LootPool get() {
        return build();
    }
}
