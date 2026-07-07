package me.udnek.coreu.custom.recipe.choice;

import com.google.common.base.Preconditions;
import me.udnek.coreu.custom.item.CustomItem;
import me.udnek.coreu.custom.item.ItemUtils;
import me.udnek.coreu.custom.item.VanillaItemManager;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@NullMarked
public class CustomCompatibleRecipeChoice implements CustomRecipeChoice{

    protected Set<CustomItem> customs;
    protected Set<Material> materials;

    public static CustomCompatibleRecipeChoice fromCustomItems(CustomItem ...customItems){
        return new CustomCompatibleRecipeChoice(Set.of(customItems), Set.of());
    }

    public static CustomCompatibleRecipeChoice fromMaterials(Material ...materialItems){
        return new CustomCompatibleRecipeChoice(Set.of(), Set.of(materialItems));
    }

    public CustomCompatibleRecipeChoice(Set<CustomItem> customItems, Set<Material> materialItems){
        Preconditions.checkArgument(customItems.size() + materialItems.size() > 0, "Choice can not be empty!");
        customs = new HashSet<>(customItems);
        materials = new HashSet<>(materialItems);
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public RecipeChoice clone() {
        return new CustomCompatibleRecipeChoice(customs, materials);
    }

    @Override
    public boolean test(ItemStack itemStack) {
        return ItemUtils.containsSame(itemStack, materials, customs);
    }

    @Override
    public List<ItemStack> getAllPossible() {
        List<ItemStack> itemStacks = new ArrayList<>();
        for (Material material : materials) itemStacks.add(new ItemStack(material));
        for (CustomItem customItem : customs) itemStacks.add(customItem.getItem());
        return itemStacks;
    }

    @Override
    public boolean replaceItem(ItemStack oldItem, ItemStack newItem) {
        if (!removeItem(oldItem)) return false;
        return addItem(newItem);
    }

    public boolean addItem(ItemStack itemStack) {
        if (test(itemStack)) return false;
        CustomItem customItem = CustomItem.get(itemStack);
        if (customItem != null) return customs.add(customItem);
        else return materials.add(itemStack.getType());
    }

    public boolean removeItem(ItemStack itemStack) {
        if (!test(itemStack)) return false;
        CustomItem customItem = CustomItem.get(itemStack);
        if (customItem != null){
            if (VanillaItemManager.isReplaced(customItem)) return customs.remove(customItem) || materials.remove(itemStack.getType());
            else return customs.remove(customItem);
        }
        else return materials.remove(itemStack.getType());
    }

    @Override
    public ItemStack getItemStack() {
        return customs.isEmpty() ? new ItemStack(materials.stream().toList().getFirst()) : customs.stream().toList().getFirst().getItem();
    }
}

















