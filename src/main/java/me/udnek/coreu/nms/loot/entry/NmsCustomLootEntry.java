package me.udnek.coreu.nms.loot.entry;

import me.udnek.coreu.nms.NmsUtils;
import me.udnek.coreu.nms.loot.util.ItemStackCreator;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntries;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.bukkit.craftbukkit.v1_21_R5.CraftLootTable;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

public class NmsCustomLootEntry extends LootPoolSingletonContainer {
    protected final ItemStackCreator creator;

    protected NmsCustomLootEntry(int weight, int quality, @NotNull List<LootItemCondition> conditions, @NotNull List<LootItemFunction> functions, @NotNull ItemStackCreator creator) {
        super(weight, quality, conditions, functions);
        this.creator = creator;
    }

    @Override
    protected void createItemStack(Consumer<ItemStack> consumer, LootContext lootContext) {
        org.bukkit.inventory.ItemStack itemStack = createItemStack(CraftLootTable.convertContext(lootContext));
        consumer.accept(NmsUtils.toNmsItemStack(itemStack));
    }
    public @NotNull org.bukkit.inventory.ItemStack createItemStack(@NotNull org.bukkit.loot.LootContext lootContext){
        return creator.createItemStack(lootContext);
    }
    @Override
    public LootPoolEntryType getType() {
        return LootPoolEntries.ITEM;
    }
}
