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
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

@NullMarked
public class NmsCustomEntry extends LootPoolSingletonContainer {
    protected final ItemStackCreator creator;

    protected NmsCustomEntry(int weight, int quality, List<LootItemCondition> conditions, List<LootItemFunction> functions, ItemStackCreator creator) {
        super(weight, quality, conditions, functions);
        this.creator = creator;
    }

    @Override
    protected void createItemStack(Consumer<ItemStack> consumer, LootContext lootContext) {
        org.bukkit.inventory.ItemStack itemStack = createItemStack(CraftLootTable.convertContext(lootContext));
        consumer.accept(NmsUtils.toNmsItemStack(itemStack));
    }

    public org.bukkit.inventory.ItemStack createItemStack(org.bukkit.loot.LootContext lootContext){
        return creator.createItemStack(lootContext);
    }

    @Override
    public LootPoolEntryType getType() {
        return LootPoolEntries.ITEM;
    }

    public static class Builder {

        protected int weight = LootPoolSingletonContainer.DEFAULT_WEIGHT;
        protected int quality = LootPoolSingletonContainer.DEFAULT_QUALITY;
        protected List<LootItemFunction> functions = new ArrayList<>();
        protected List<LootItemCondition> conditions = new ArrayList<>();
        protected ItemStackCreator creator;

        public Builder(ItemStackCreator creator){
            this.creator = creator;
        }

        public Builder fromVanilla(LootTable lootTable, Predicate<org.bukkit.inventory.ItemStack> predicate){
            return this.copyConditionsFrom(lootTable, predicate).copyFunctionsFrom(lootTable, predicate);
        }

        public Builder copyConditionsFrom(LootTable lootTable, Predicate<org.bukkit.inventory.ItemStack> predicate){
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
        public Builder copyFunctionsFrom(LootTable lootTable, Predicate<org.bukkit.inventory.ItemStack> predicate){
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


        public NmsCustomEntry.Builder weight(int weight){
            this.weight = weight;
            return this;
        }
        public NmsCustomEntry.Builder quality(int quality){
            this.quality = quality;
            return this;
        }

        public NmsCustomEntry.Builder addFunction(LootFunctionWrapper function){
            this.functions.add(function.getNms());
            return this;
        }
        public NmsCustomEntry.Builder addCondition(LootConditionWrapper condition){
            this.conditions.add(condition.getNms());
            return this;
        }

        public Builder setConditions(List<LootConditionWrapper> conditions) {
            this.conditions = LootConditionWrapper.unwrap(conditions);
            return this;
        }

        public Builder setFunctions(List<LootFunctionWrapper> functions) {
            this.functions = LootFunctionWrapper.unwrap(functions);
            return this;
        }

        public NmsCustomEntry build(){
            return new NmsCustomEntry(weight, quality, conditions, functions, creator);
        }

        public EntryWrapper buildAndWrap(){
            return EntryWrapper.fromNms(build());
        }
    }
}
