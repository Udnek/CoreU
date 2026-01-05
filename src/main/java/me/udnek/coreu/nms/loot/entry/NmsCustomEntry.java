package me.udnek.coreu.nms.loot.entry;

import me.udnek.coreu.nms.NmsUtils;
import me.udnek.coreu.nms.loot.condition.LootConditionWrapper;
import me.udnek.coreu.nms.loot.function.LootFunctionWrapper;
import me.udnek.coreu.nms.loot.util.ItemStackCreator;
import me.udnek.coreu.nms.loot.util.NmsFields;
import me.udnek.coreu.util.Reflex;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntries;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.bukkit.craftbukkit.CraftLootTable;
import org.bukkit.loot.LootTable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class NmsCustomEntry extends LootPoolSingletonContainer {
    protected final ItemStackCreator creator;

    protected NmsCustomEntry(int weight, int quality, @NotNull List<LootItemCondition> conditions, @NotNull List<LootItemFunction> functions, @NotNull ItemStackCreator creator) {
        super(weight, quality, conditions, functions);
        this.creator = creator;
    }

    @Override
    protected void createItemStack(@NotNull Consumer<ItemStack> consumer, @NotNull LootContext lootContext) {
        org.bukkit.inventory.ItemStack itemStack = createItemStack(CraftLootTable.convertContext(lootContext));
        consumer.accept(NmsUtils.toNmsItemStack(itemStack));
    }

    public @NotNull org.bukkit.inventory.ItemStack createItemStack(@NotNull org.bukkit.loot.LootContext lootContext){
        return creator.createItemStack(lootContext);
    }

    @Override
    public @NotNull LootPoolEntryType getType() {
        return LootPoolEntries.ITEM;
    }

    public static class Builder {

        protected int weight = LootPoolSingletonContainer.DEFAULT_WEIGHT;
        protected int quality = LootPoolSingletonContainer.DEFAULT_QUALITY;
        protected List<LootItemFunction> functions = new ArrayList<>();
        protected List<LootItemCondition> conditions = new ArrayList<>();
        protected ItemStackCreator creator;

        public Builder(@NotNull ItemStackCreator creator){
            this.creator = creator;
        }

        public @NotNull Builder fromVanilla(@NotNull LootTable lootTable, @NotNull Predicate<org.bukkit.inventory.ItemStack> predicate){
            return this.copyConditionsFrom(lootTable, predicate).copyFunctionsFrom(lootTable, predicate);
        }

        public @NotNull Builder copyConditionsFrom(@NotNull LootTable lootTable, @NotNull Predicate<org.bukkit.inventory.ItemStack> predicate){
            LootPoolSingletonContainer foundContainer = NmsUtils.getSingletonContainerByPredicate(
                    NmsUtils.toNmsLootTable(lootTable),
                    itemStack -> predicate.test(NmsUtils.toBukkitItemStack(itemStack)));
            LootPool foundPool = NmsUtils.getLootPoolByPredicate(
                    NmsUtils.toNmsLootTable(lootTable),
                    itemStack -> predicate.test(NmsUtils.toBukkitItemStack(itemStack)));
            if (foundContainer != null){
                this.conditions = Reflex.getFieldValue(foundContainer, NmsFields.CONDITIONS);
            } else if (foundPool != null) {
                this.conditions = Reflex.getFieldValue(foundPool, NmsFields.CONDITIONS);
            }
            return this;
        }
        public @NotNull Builder copyFunctionsFrom(@NotNull LootTable lootTable, @NotNull Predicate<org.bukkit.inventory.ItemStack> predicate){
            LootPoolSingletonContainer foundContainer = NmsUtils.getSingletonContainerByPredicate(
                    NmsUtils.toNmsLootTable(lootTable),
                    itemStack -> predicate.test(NmsUtils.toBukkitItemStack(itemStack)));
            LootPool foundPool = NmsUtils.getLootPoolByPredicate(
                    NmsUtils.toNmsLootTable(lootTable),
                    itemStack -> predicate.test(NmsUtils.toBukkitItemStack(itemStack)));
            if (foundContainer != null){
                this.functions = Reflex.getFieldValue(foundContainer, NmsFields.FUNCTIONS);
            } else if (foundPool != null) {
                this.functions = Reflex.getFieldValue(foundPool, NmsFields.FUNCTIONS);
            }
            return this;
        }


        public @NotNull NmsCustomEntry.Builder weight(int weight){
            this.weight = weight;
            return this;
        }
        public @NotNull NmsCustomEntry.Builder quality(int quality){
            this.quality = quality;
            return this;
        }

        public @NotNull NmsCustomEntry.Builder addFunction(@NotNull LootFunctionWrapper function){
            this.functions.add(function.getNms());
            return this;
        }
        public @NotNull NmsCustomEntry.Builder addCondition(@NotNull LootConditionWrapper condition){
            this.conditions.add(condition.getNms());
            return this;
        }

        public @NotNull Builder setConditions(@NotNull List<LootConditionWrapper> conditions) {
            this.conditions = LootConditionWrapper.unwrap(conditions);
            return this;
        }

        public @NotNull Builder setFunctions(@NotNull List<LootFunctionWrapper> functions) {
            this.functions = LootFunctionWrapper.unwrap(functions);
            return this;
        }

        public @NotNull NmsCustomEntry build(){
            return new NmsCustomEntry(weight, quality, conditions, functions, creator);
        }

        public @NotNull EntryWrapper buildAndWrap(){
            return EntryWrapper.fromNms(build());
        }
    }
}
