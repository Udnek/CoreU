package me.udnek.coreu.custom.item;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Repairable;
import me.udnek.coreu.custom.equipment.universal.UniversalInventorySlot;
import me.udnek.coreu.nms.Nms;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.loot.LootTable;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;


@org.jspecify.annotations.NullMarked public class ItemUtils{

    public static List<LootTable> getWhereItemOccurs(Predicate<ItemStack> predicate) {
        Nms nms = Nms.get();
        List<LootTable> lootTables = new ArrayList<>();
        for (LootTable lootTable : nms.getRegisteredLootTables()) {
            List<ItemStack> itemStacks = nms.getPossibleLoot(lootTable);

            for (ItemStack loot : itemStacks) {
                if (predicate.test(loot)){
                    lootTables.add(lootTable);
                    break;
                }
            }
        }
        return lootTables;
    }

    public static List<LootTable> getWhereItemOccurs(ItemStack target) {
        return getWhereItemOccurs(stack -> isSameIds(stack, target));
    }

    public static void givePriorityToSlot(UniversalInventorySlot slot, Player player, ItemStack stack){
        ItemStack inSlot = slot.getItem(player);
        int leftOver;
        if (inSlot == null || inSlot.isEmpty()){
            slot.setItem(stack, player);
            leftOver = 0;
        }
        else if (inSlot.isSimilar(stack)){
            int canFit = inSlot.getMaxStackSize() - inSlot.getAmount();
            leftOver = Math.max(stack.getAmount() - canFit, 0);
            slot.addItem(stack.getAmount()-leftOver, player);
        } else {
            leftOver = stack.getAmount();
        }
        if (leftOver > 0){
            stack.setAmount(leftOver);
            giveAndDropLeftover(player, stack);
        }
    }

    public static void giveAndDropLeftover(Player player, ItemStack...itemStack){
        HashMap<Integer, ItemStack> dropItem = player.getInventory().addItem(itemStack);
        for (ItemStack stack : dropItem.values()) {
            player.getWorld().dropItem(player.getLocation(), stack);
        }
    }

    public static boolean containsSame(ItemStack itemStack, Collection<Material> materials, Collection<CustomItem> customs){
        CustomItem customItem = CustomItem.get(itemStack);
        if (customItem != null){
            if (VanillaItemManager.isReplaced(customItem)) return customs.contains(customItem) || materials.contains(itemStack.getType());
            else return customs.contains(customItem);
        }
        return materials.contains(itemStack.getType());
    }

    public static boolean isVanillaOrReplaced(ItemStack itemStack){
        CustomItem customItem = CustomItem.get(itemStack);
        if (customItem == null) return true;
        return VanillaItemManager.isReplaced(customItem);
    }

    public static boolean isVanillaMaterial(ItemStack itemStack, Material material){
        return !CustomItem.isCustom(itemStack) && itemStack.getType() == material;
    }
    public static boolean isRepairable(ItemStack item){
        CustomItem customItem = CustomItem.get(item);
        if (customItem != null){
            RepairData repairData = customItem.getRepairData();
            if (repairData != null) return !repairData.isEmpty();
        }
        Repairable repairable = item.getData(DataComponentTypes.REPAIRABLE);
        if (repairable == null) return false;
        return !repairable.types().isEmpty();
    }

    public static boolean isSameIds(ItemStack itemA, ItemStack itemB){
        CustomItem customA = CustomItem.get(itemA);
        CustomItem customB = CustomItem.get(itemB);
        if (customA == null && customB == null) return itemA.getType() == itemB.getType();
        return customA == customB;
    }

    public static String getId(ItemStack itemStack){
        CustomItem customItem = CustomItem.get(itemStack);
        if (customItem != null) return customItem.getId();
        return itemStack.getType().toString().toLowerCase();
    }

    public static Component getDisplayName(ItemStack itemStack){
        if (itemStack.hasItemMeta()){
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta.hasDisplayName()) return itemMeta.displayName();
            if (itemMeta.hasItemName()) return itemMeta.itemName();
        }
        return Component.translatable(itemStack.getType().getItemTranslationKey());
    }

    public static boolean isCustomItemOrMaterial(String name){
        if (CustomItem.idExists(name)) return true;
        return Material.getMaterial(name.toUpperCase()) != null;
    }

    public static @Nullable ItemStack getFromCustomItemOrMaterial(String name){
        CustomItem customItem = CustomItem.get(name);
        if (customItem != null) return customItem.getItem();
        Material material = Material.getMaterial(name.toUpperCase());
        if (material != null && material.isItem()) return new ItemStack(material);
        return null;
    }
}













