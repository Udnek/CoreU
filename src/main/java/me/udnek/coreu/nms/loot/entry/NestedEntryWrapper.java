package me.udnek.coreu.nms.loot.entry;

import com.mojang.datafixers.util.Either;
import me.udnek.coreu.nms.NmsUtils;
import me.udnek.coreu.nms.loot.table.DefaultLootTableWrapper;
import me.udnek.coreu.nms.loot.table.LootTableWrapper;
import me.udnek.coreu.nms.loot.util.LootInfo;
import me.udnek.coreu.nms.loot.util.NmsFields;
import me.udnek.coreu.util.Reflex;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.function.Consumer;

@org.jspecify.annotations.NullMarked public class NestedEntryWrapper extends SingletonEntryWrapper{

    public static NestedEntryWrapper createFromLootTable(LootTableWrapper lootTable){
        Constructor<NestedLootTable> constructor = Reflex.getFirstConstructor(NestedLootTable.class);
        Either<ResourceKey<LootTable>, LootTable> right = Either.right(lootTable.getNms());
        NestedLootTable nested = Reflex.construct(
                constructor,
                right,
                LootPoolSingletonContainer.DEFAULT_WEIGHT,
                LootPoolSingletonContainer.DEFAULT_QUALITY,
                List.of(),
                List.of()
        );
        return new NestedEntryWrapper(nested);
    }

    public NestedEntryWrapper(NestedLootTable nested) {
        super(nested);
    }

    public LootTableWrapper getLootTable(){
        Either<ResourceKey<LootTable>, LootTable> either = Reflex.getFieldValue(container, NmsFields.CONTENTS);
        LootTable lootTable = either.map(NmsUtils::getLootTable, table -> table);
        return new DefaultLootTableWrapper(lootTable);
    }

    public void setLootTable(LootTableWrapper lootTable){
        Either<ResourceKey<LootTable>, LootTable> either = Either.right(lootTable.getNms());
        Reflex.setFieldValue(container, NmsFields.CONTENTS, either);
    }

    @Override
    public void extractItems(LootInfo lootInfo, Consumer<Pair<ItemStack, LootInfo>> consumer) {
        getLootTable().extractItems(lootInfo.withExtraConditions(getConditions()), consumer);
    }

    @Override
    public NestedLootTable getNms() {
        return (NestedLootTable) super.getNms();
    }
}
