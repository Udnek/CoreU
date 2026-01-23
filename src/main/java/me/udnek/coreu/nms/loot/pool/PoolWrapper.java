package me.udnek.coreu.nms.loot.pool;

import com.google.common.base.Preconditions;
import me.udnek.coreu.nms.NmsUtils;
import me.udnek.coreu.nms.NmsWrapper;
import me.udnek.coreu.nms.loot.condition.LootConditionWrapper;
import me.udnek.coreu.nms.loot.entry.EntryWrapper;
import me.udnek.coreu.nms.loot.function.LootFunctionWrapper;
import me.udnek.coreu.nms.loot.util.LootInfo;
import me.udnek.coreu.nms.loot.util.NmsConditioned;
import me.udnek.coreu.nms.loot.util.NmsFields;
import me.udnek.coreu.nms.loot.util.NmsFunctioned;
import me.udnek.coreu.util.Reflex;
import net.minecraft.util.Util;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootTable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public interface PoolWrapper extends NmsWrapper<@NotNull LootPool>, NmsConditioned, NmsFunctioned {

    void addEntry(@NotNull EntryWrapper entry);
    void removeEntry(int n);
    @NotNull EntryWrapper getEntry(int n);
    @NotNull List<EntryWrapper> getEntries();
    void extractItems(@NotNull LootInfo base, @NotNull Consumer<Pair<ItemStack, LootInfo>> consumer);
    int getAverageRolls();
    int getAverageBonusRolls();
    //void extractAllSingletons(@NotNull Consumer<SingletonEntryWrapper> consumer);

    class Builder{

        protected int rolls = 1;
        protected int bonusRolls = 0;
        protected List<LootPoolEntryContainer> entries = new ArrayList<>();
        protected List<LootItemCondition> conditions;
        protected List<LootItemFunction> functions;

        public Builder(@NotNull EntryWrapper...entries){
            Arrays.asList(entries).forEach(this::addEntry);
        }

        public @NotNull Builder rolls(int rolls){
            this.rolls = rolls;
            return this;
        }

        public @NotNull Builder bonusRolls(int bonusRolls){
            this.bonusRolls = bonusRolls;
            return this;
        }

        public @NotNull Builder addEntry(@NotNull EntryWrapper entry) {
            entries.add(entry.getNms());
            return this;
        }


        protected @Nullable LootPool getPoolByPredicate(@NotNull LootTable lootTable, Predicate<ItemStack> predicate){
            LootPool found = NmsUtils.getLootPoolByPredicate(
                    NmsUtils.toNmsLootTable(lootTable),
                    itemStack -> predicate.test(NmsUtils.toBukkitItemStack(itemStack)));

            return found;
        }

        public @NotNull PoolWrapper.Builder copyConditionsFrom(@NotNull LootTable lootTable, Predicate<ItemStack> predicate){
            LootPool found = getPoolByPredicate(lootTable, predicate);
            Preconditions.checkArgument(found != null, "Pool not found!");
            this.conditions = Reflex.getFieldValue(found, NmsFields.CONDITIONS);
            return this;
        }

        public @NotNull PoolWrapper.Builder copyFunctionsFrom(@NotNull LootTable lootTable, Predicate<ItemStack> predicate){
            LootPool found = getPoolByPredicate(lootTable, predicate);
            Preconditions.checkArgument(found != null, "Pool not found!");
            this.functions = Reflex.getFieldValue(found, NmsFields.FUNCTIONS);
            return this;
        }


        public void setConditions(@NotNull List<LootConditionWrapper> conditions) {
            this.conditions = LootConditionWrapper.unwrap(conditions);
        }

        public void setFunctions(@NotNull List<LootFunctionWrapper> functions) {
            this.functions =  LootFunctionWrapper.unwrap(functions);
        }

        public void validate(){
            for (LootPoolEntryContainer container : entries) {
                Preconditions.checkArgument(container != null, "Container can not be null!");
            }
        }

        public @NotNull PoolWrapper build(){
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
            return new DefaultPoolWrapper(lootPool);
        }
    }
}
