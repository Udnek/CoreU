package me.udnek.coreu.nms.loot.pool;

import me.udnek.coreu.nms.NmsUtils;
import me.udnek.coreu.nms.loot.condition.LootConditionWrapper;
import me.udnek.coreu.nms.loot.entry.EntryWrapper;
import me.udnek.coreu.nms.loot.entry.SingletonEntryWrapper;
import me.udnek.coreu.nms.loot.function.LootFunctionWrapper;
import me.udnek.coreu.nms.loot.util.LootInfo;
import me.udnek.coreu.nms.loot.util.NmsFields;
import me.udnek.coreu.util.Reflex;
import net.minecraft.util.Util;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class DefaultPoolWrapper implements PoolWrapper {

    protected final LootPool pool;

    public DefaultPoolWrapper(@NotNull LootPool pool) {
        this.pool = pool;
    }

    public @NotNull List<LootPoolEntryContainer> getEntriesNms(){
        return Reflex.getFieldValue(pool, NmsFields.ENTRIES);
    }

    @Override
    public @NotNull List<EntryWrapper> getEntries() {
        return getEntriesNms().stream().map(EntryWrapper::fromNms).toList();
    }

    @Override
    public void addEntry(@NotNull EntryWrapper entry) {
        List<LootPoolEntryContainer> newEntries = new ArrayList<>(getEntriesNms());
        newEntries.add(entry.getNms());
        Reflex.setFieldValue(pool, NmsFields.ENTRIES, newEntries);
    }

    @Override
    public void removeEntry(int n) {
        List<LootPoolEntryContainer> newEntries = new ArrayList<>(getEntriesNms());
        newEntries.remove(n);
        Reflex.setFieldValue(pool, NmsFields.ENTRIES, newEntries);
    }

    @Override
    public @NotNull EntryWrapper getEntry(int n) {
        return EntryWrapper.fromNms(getEntriesNms().get(n));
    }

    @Override
    public void extractItems(@NotNull LootInfo base, @NotNull Consumer<Pair<ItemStack, LootInfo>> consumer) {
        List<SingletonEntryWrapper> singletons = new ArrayList<>();
        getEntries().forEach(e -> e.extractAllSingleton(singletons::add));

        float totalWeight = 0;
        for (SingletonEntryWrapper singletonEntryWrapper : singletons) {
            totalWeight += singletonEntryWrapper.getWeight();
        }

        List<LootConditionWrapper> conditions = getConditions();
        for (SingletonEntryWrapper singleton : singletons) {
            float singleProbability = singleton.getWeight() / totalWeight;
            // 1 - (1-p)^rolls
            float finalProbability = (float) (1 - Math.pow(1-singleProbability, getAverageRolls()));
            singleton.extractItems(base.copyAndMultiplyProbability(finalProbability).withExtraConditions(conditions), consumer);
        }
    }

    @Override
    public int getAverageRolls() {
        NumberProvider rolls = Reflex.getFieldValue(pool, "rolls");
        return Math.round(NmsUtils.averageFromNumberProvider(rolls, 1));
    }

    @Override
    public int getAverageBonusRolls() {
        NumberProvider bonusRolls = Reflex.getFieldValue(pool, "bonusRolls");
        return Math.round(NmsUtils.averageFromNumberProvider(bonusRolls, 0));
    }

    @Override
    public @NotNull List<LootConditionWrapper> getConditions() {
        return LootConditionWrapper.wrap(Reflex.getFieldValue(pool, NmsFields.CONDITIONS));
    }

    @Override
    public void setConditions(@NotNull List<LootConditionWrapper> conditions) {
        List<LootItemCondition> list = LootConditionWrapper.unwrap(conditions);
        Reflex.setFieldValue(pool, NmsFields.CONDITIONS, list);
        Reflex.setFieldValue(pool, NmsFields.COMPOSITE_CONDITIONS, Util.allOf(list));
    }

    @Override
    public @NotNull List<LootFunctionWrapper> getFunctions() {
        return LootFunctionWrapper.wrap(Reflex.getFieldValue(pool, NmsFields.FUNCTIONS));
    }

    @Override
    public void setFunctions(@NotNull List<LootFunctionWrapper> functions) {
        List<LootItemFunction> list = LootFunctionWrapper.unwrap(functions);
        Reflex.setFieldValue(pool, NmsFields.FUNCTIONS, list);
        Reflex.setFieldValue(pool, NmsFields.COMPOSITE_FUNCTIONS, LootItemFunctions.compose(list));
    }

    @Override
    public @NotNull LootPool getNms() {
        return pool;
    }
}
