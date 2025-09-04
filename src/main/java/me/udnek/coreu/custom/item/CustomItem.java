package me.udnek.coreu.custom.item;

import me.udnek.coreu.CoreU;
import me.udnek.coreu.custom.component.ComponentHolder;
import me.udnek.coreu.custom.registry.CustomRegistries;
import me.udnek.coreu.custom.registry.Registrable;
import net.kyori.adventure.translation.Translatable;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.block.Crafter;
import org.bukkit.entity.Player;
import org.bukkit.event.block.CrafterCraftEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public interface CustomItem extends Registrable, ComponentHolder<CustomItem>, Translatable {

    NamespacedKey PERSISTENT_DATA_CONTAINER_NAMESPACE = new NamespacedKey(CoreU.getInstance(), "item");

    ///////////////////////////////////////////////////////////////////////////
    // STATIC
    ///////////////////////////////////////////////////////////////////////////

    static @Nullable String getId(@Nullable ItemStack itemStack){
        if (itemStack == null) return null;
        if (itemStack.hasItemMeta()){
            String id = itemStack.getItemMeta().getPersistentDataContainer().get(PERSISTENT_DATA_CONTAINER_NAMESPACE, PersistentDataType.STRING);
            if (id != null) return id;
        }
        VanillaBasedCustomItem replaced = VanillaItemManager.getReplaced(itemStack.getType());
        return replaced == null ? null : replaced.getId();
    }
    static @Nullable CustomItem get(@Nullable ItemStack itemStack){
        return get(getId(itemStack));
    }
    static @Nullable CustomItem get(@Nullable String id){return CustomRegistries.ITEM.get(id);}
    static void consumeIfCustom(@Nullable ItemStack itemStack, @NotNull Consumer<@NotNull CustomItem> consumer){
        CustomItem customItem = get(itemStack);
        if (customItem != null) consumer.accept(customItem);
    }
    static boolean idExists(String id){return CustomRegistries.ITEM.get(id) != null;}
    static boolean isCustom(@NotNull ItemStack itemStack) {
        if (itemStack.hasItemMeta()){
            if (itemStack.getItemMeta().getPersistentDataContainer().has(PERSISTENT_DATA_CONTAINER_NAMESPACE)) return true;
        }
        return VanillaItemManager.isReplaced(itemStack.getType());
    }
    static boolean isSameIds(@Nullable ItemStack itemStack1, @Nullable ItemStack itemStack2){
        CustomItem customItem1 = get(itemStack1);
        CustomItem customItem2 = get(itemStack2);
        if (customItem1 == null || customItem2 == null) return false;
        return customItem1 == customItem2;
    }
    default boolean isThisItem(@Nullable ItemStack itemStack){
        return get(itemStack) == this;
    }
    @NotNull NamespacedKey getNewRecipeKey();
    ///////////////////////////////////////////////////////////////////////////
    // PROPERTIES
    ///////////////////////////////////////////////////////////////////////////
    void setCooldown(@NotNull Player player, int ticks);
    default void setCooldownSeconds(@NotNull Player player, float seconds){setCooldown(player, (int) (seconds * 20));}
    int getCooldown(@NotNull Player player);
    default boolean hasCooldown(@NotNull Player player){return getCooldown(player) != 0;}
    @NotNull String getRawId();
    @NotNull ItemStack getItem();
    void getRecipes(@NotNull Consumer<@NotNull Recipe> consumer);
    void registerRecipe(@NotNull Recipe recipe);
    boolean isTagged(@NotNull Tag<Material> tag);
    @NotNull ItemStack update(@NotNull ItemStack itemStack);
    @Nullable RepairData getRepairData();
    ///////////////////////////////////////////////////////////////////////////
    // EVENTS
    ///////////////////////////////////////////////////////////////////////////
    default void onPrepareCraft(@NotNull PrepareItemCraftEvent event, @Nullable ItemStack newResult){
        event.getInventory().setResult(getItemFromCraftingMatrix(newResult, event.getInventory().getMatrix(), event.getRecipe()));
    }

    default void onCrafterCraft(@NotNull CrafterCraftEvent event){
        ItemStack item = getItemFromCraftingMatrix(event.getResult(), ((Crafter) event.getBlock().getState()).getInventory().getContents(), event.getRecipe());
        event.setResult(item == null ? new ItemStack(Material.AIR) : item);
    }

    default @Nullable ItemStack getItemFromCraftingMatrix(@Nullable ItemStack result, @Nullable ItemStack[] matrix, @NotNull Recipe recipe){
        return result;
    }

}
