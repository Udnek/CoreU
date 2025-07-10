package me.udnek.coreu.custom.enchantment;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemEnchantments;
import me.udnek.coreu.custom.attribute.CustomAttributeConsumer;
import me.udnek.coreu.custom.registry.AbstractRegistrable;
import me.udnek.coreu.nms.NmsUtils;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import org.bukkit.Material;
import org.bukkit.craftbukkit.CraftEquipmentSlot;
import org.bukkit.craftbukkit.enchantments.CraftEnchantment;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.index.qual.Positive;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

public abstract class ConstructableCustomEnchantment extends AbstractRegistrable implements CustomEnchantment {

    protected Enchantment nms;
    protected org.bukkit.enchantments.Enchantment bukkit;

    @Override
    public void initialize(@NotNull Plugin plugin) {
        super.initialize(plugin);

        DataComponentMap effects = DataComponentMap.builder().build();

        HolderSet<Enchantment> exclusiveSet = NmsUtils.createHolderSet(NmsUtils.getRegistry(Registries.ENCHANTMENT), getExclusives());
        HolderSet<Item> supportedItems = NmsUtils.createHolderSet(BuiltInRegistries.ITEM, getSupportedItems());
        HolderSet<Item> primaryItems;
        if (getPrimaryItems() != null){
            primaryItems = NmsUtils.createHolderSet(BuiltInRegistries.ITEM, getPrimaryItems());
        } else {
            primaryItems = null;
        }


        @NotNull EquipmentSlotGroup[] bukkitSlots = getSlots();
        net.minecraft.world.entity.EquipmentSlotGroup[] slots = new net.minecraft.world.entity.EquipmentSlotGroup[bukkitSlots.length];
        for (int i = 0; i < bukkitSlots.length; i++) {
            slots[i] = CraftEquipmentSlot.getNMSGroup(bukkitSlots[i]);
        }


        Enchantment.EnchantmentDefinition definition;
        if (primaryItems != null){
            definition = Enchantment.definition(
                    supportedItems, primaryItems, getWeight(), getMaxLevel(), getMinCost().toNms(), getMaxCost().toNms(), getAnvilCost(), slots);
        } else {
            definition = Enchantment.definition(
                    supportedItems,               getWeight(), getMaxLevel(), getMinCost().toNms(), getMaxCost().toNms(), getAnvilCost(), slots);
        }

        Component description = NmsUtils.toNmsComponent(getDescription());

        Enchantment enchantment = new Enchantment(description, definition, exclusiveSet, effects);

        NmsUtils.registerInRegistry(NmsUtils.getRegistry(Registries.ENCHANTMENT), enchantment, getKey());
        nms = enchantment;
        bukkit = CraftEnchantment.minecraftToBukkit(enchantment);
    }

    @Override
    public @NotNull ItemStack createEnchantedBook(int level) {
        ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
        enchantBook(book, level);
        return book;
    }

    @Override
    public void enchant(@NotNull ItemStack itemStack, int level) {
        itemStack.addEnchantment(bukkit, level);
    }

    @Override
    public void enchantBook(@NotNull ItemStack itemStack, int level) {
        itemStack.setData(DataComponentTypes.STORED_ENCHANTMENTS, ItemEnchantments.itemEnchantments().add(bukkit, level));
    }

    @Override
    @NotNull
    public org.bukkit.enchantments.Enchantment getBukkit() {
        return bukkit;
    }

    // PROPERTIES


    @Override
    public void getCustomAttributes(int level, @NotNull CustomAttributeConsumer consumer) {}

    public @Nullable Iterable<org.bukkit.enchantments.Enchantment> getExclusives(){return null;}
    public abstract @NotNull Iterable<@NotNull Material> getSupportedItems();
    public @Nullable Iterable<@NotNull Material> getPrimaryItems() {return null;}
    public @NotNull net.kyori.adventure.text.Component getDescription(){
        return net.kyori.adventure.text.Component.translatable("enchantment."+getKey().namespace()+"."+getRawId());
    }
    public abstract @NotNull EquipmentSlotGroup[] getSlots();
    public @Positive int getWeight(){return 1;}
    public abstract @Range(from = 1, to = 255) int getMaxLevel();
    public @Range(from = 1, to = 1024) int getAnvilCost(){return 2;}
    @ApiStatus.Experimental
    public @NotNull Cost getMinCost(){return new Cost(0, 0);}
    @ApiStatus.Experimental
    public @NotNull Cost getMaxCost(){return new Cost(0, 0);}

    public record Cost(@NonNegative int base, @NonNegative int perLevelAboveFirst){
        public @NotNull Enchantment.Cost toNms(){
            return new Enchantment.Cost(base, perLevelAboveFirst);
        }
    }
}
