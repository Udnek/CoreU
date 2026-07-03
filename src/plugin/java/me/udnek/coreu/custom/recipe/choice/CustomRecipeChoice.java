package me.udnek.coreu.custom.recipe.choice;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

import java.util.List;

@org.jspecify.annotations.NullMarked public  interface CustomRecipeChoice extends RecipeChoice{
    List<ItemStack> getAllPossible();
    boolean replaceItem(ItemStack oldItem, ItemStack newItem);
}
