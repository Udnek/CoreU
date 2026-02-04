package me.udnek.coreu.nms.loot.entry;

import me.udnek.coreu.nms.loot.util.LootInfo;
import me.udnek.coreu.util.Reflex;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.entries.*;
import org.apache.commons.lang3.tuple.Pair;
import org.jspecify.annotations.NullMarked;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

@NullMarked
public class TagEntryWrapper extends SingletonEntryWrapper{

    public TagEntryWrapper(TagEntry container) {
        super(container);
    }

    public boolean expand(){
        return Reflex.getFieldValue(container, "expand");
    }

    protected TagKey<Item> tagNms(){
        return Reflex.getFieldValue(container, "tag");
    }

    @Override
    public void extractItems(LootInfo baseInfo, Consumer<Pair<org.bukkit.inventory.ItemStack, LootInfo>> consumer) {
        if (expand()){
            // decreases chance for each item
            Iterable<Holder<Item>> tag = BuiltInRegistries.ITEM.getTagOrEmpty(tagNms());
            AtomicInteger size = new AtomicInteger(0);
            tag.forEach(item -> size.addAndGet(1));
            super.extractItems(baseInfo.copyAndMultiplyProbability(1f/size.get()), consumer);
        } else {
            super.extractItems(baseInfo, consumer);
        }

    }
}
