package me.udnek.coreu.nms.loot.entry;

import me.udnek.coreu.nms.Nms;
import me.udnek.coreu.nms.NmsUtils;
import me.udnek.coreu.nms.loot.condition.LootConditionWrapper;
import me.udnek.coreu.nms.loot.function.LootFunctionWrapper;
import me.udnek.coreu.nms.loot.util.LootInfo;
import me.udnek.coreu.nms.loot.util.NmsFields;
import me.udnek.coreu.nms.loot.util.NmsFunctioned;
import me.udnek.coreu.util.Reflex;
import net.minecraft.core.Holder;
import net.minecraft.util.Util;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntry;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.ExplorationMapFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceWithEnchantedBonusCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootTable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class SingletonEntryWrapper implements EntryWrapper, NmsFunctioned {

    protected @NotNull LootPoolSingletonContainer container;

    public SingletonEntryWrapper(@NotNull LootPoolSingletonContainer container) {
        this.container = container;
    }

    public static @Nullable SingletonEntryWrapper fromVanillaByPredicate(@NotNull LootTable lootTable, @NotNull Predicate<ItemStack> predicate){
        for (LootPoolSingletonContainer container : NmsUtils.getAllSingletonContainers(NmsUtils.toNmsLootTable(lootTable))) {
            LootPoolEntry entry = NmsUtils.getEntry(container);
            List<net.minecraft.world.item.ItemStack> loot = new ArrayList<>();
            NmsUtils.getPossibleLoot(entry, loot::add);
            for (net.minecraft.world.item.ItemStack stack : loot) {
                ItemStack itemStack = NmsUtils.toBukkitItemStack(stack);
                if (predicate.test(itemStack)) return new SingletonEntryWrapper(container);
            }
        }
        return null;
    }

    @Override
    public @NotNull List<LootConditionWrapper> getConditions() {
        return LootConditionWrapper.wrap(Reflex.getFieldValue(container, NmsFields.CONDITIONS));
    }

    @Override
    public void setConditions(@NotNull List<LootConditionWrapper> conditions) {
        List<LootItemCondition> list = conditions.stream().map(LootConditionWrapper::getNms).toList();
        Reflex.setFieldValue(container, NmsFields.CONDITIONS, list);
        Reflex.setFieldValue(container, NmsFields.COMPOSITE_CONDITIONS, Util.allOf(list));
    }

    @Override
    public @NotNull List<LootFunctionWrapper> getFunctions() {
        return LootFunctionWrapper.wrap(Reflex.getFieldValue(container, NmsFields.FUNCTIONS));
    }

    @Override
    public void setFunctions(@NotNull List<LootFunctionWrapper> functions) {
        List<LootItemFunction> list = functions.stream().map(LootFunctionWrapper::getNms).toList();
        Reflex.setFieldValue(container, NmsFields.CONDITIONS, list);
        Reflex.setFieldValue(container, NmsFields.COMPOSITE_CONDITIONS, LootItemFunctions.compose(list));
    }

    public void setWeight(int n){
        Reflex.setFieldValue(container, NmsFields.WEIGHT, n);
    }
    public int getWeight(){
        return Reflex.getFieldValue(container, NmsFields.WEIGHT);
    }
    public void setQuality(int n){
        Reflex.setFieldValue(container, NmsFields.QUALITY, n);
    }
    public int getQuality(){
        return Reflex.getFieldValue(container, NmsFields.QUALITY);
    }

    @Override
    public void extractAllSingleton(@NotNull Consumer<SingletonEntryWrapper> consumer) {
        consumer.accept(this);
    }

    protected @Nullable net.minecraft.world.item.ItemStack extractExplorerMap(){
        if (!(container instanceof LootItem)) {
            return null;
        }
        Item item = Reflex.<Holder<@NotNull Item>>getFieldValue(container, "item").value();
        if (item != Items.MAP) {
            return null;
        }
        List<LootItemFunction> functions = Reflex.getFieldValue(container, "functions");
        boolean containsMapFunction = functions.stream().anyMatch(function -> function instanceof ExplorationMapFunction);
        if (!containsMapFunction) {
            return null;
        }
        net.minecraft.world.item.ItemStack map = new net.minecraft.world.item.ItemStack(Items.FILLED_MAP);
        functions.forEach(f -> {
            if (f instanceof ExplorationMapFunction) return;
            f.apply(map, Nms.get().getGenericLootContext());
        });
        return map;
    }

    protected float numberProviderCDF(@NotNull NumberProvider provider, float target, float fallback){
        if (provider instanceof ConstantValue(float value)){
            if (value <= target) return 1;
            return 0;
        } else if (provider instanceof UniformGenerator(NumberProvider minProvider, NumberProvider maxProvider)) {
            float min = NmsUtils.averageFromNumberProvider(minProvider, 1);
            float max = NmsUtils.averageFromNumberProvider(maxProvider, 1);
            float cdf = (target - min +1) / (max - min +1);
            return Math.clamp(cdf, 0, 1);
        }
        return fallback;
    }

    public void extractItems(@NotNull LootInfo baseInfo, @NotNull Consumer<Pair<ItemStack, LootInfo>> consumer) {
        net.minecraft.world.item.ItemStack map = extractExplorerMap();
        for (LootFunctionWrapper wrapped : getFunctions()) {
            LootItemFunction func = wrapped.getNms();
            if (func instanceof SetItemCountFunction countFunc) {
                NumberProvider value = Reflex.getFieldValue(countFunc, "value");
                float cdf = numberProviderCDF(value, 0, 0);
                float notEmptyStackProbability = 1 - cdf;
                baseInfo = baseInfo.copyAndMultiplyProbability(notEmptyStackProbability);
                break;
            }
        }

        LootInfo lootInfo = baseInfo.withExtraConditions(getConditions());
        if (map != null){
            consumer.accept(Pair.of(NmsUtils.toBukkitItemStack(map), lootInfo));
        } else {
            LootPoolEntry entry = Reflex.getFieldValue(container, "entry");
            entry.createItemStack(stack -> {
                if (stack.getCount() == 0) stack.setCount(1);
                consumer.accept(Pair.of(NmsUtils.toBukkitItemStack(stack), lootInfo));
            }, Nms.get().getGenericLootContext());
        }
    }

    @Override
    public @NotNull LootPoolSingletonContainer getNms() {
        return container;
    }
}
