package me.udnek.coreu.custom.recipe.choice;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface CustomRecipeChoice extends RecipeChoice {
    @NotNull List<@NotNull ItemStack> getAllPossible();
    boolean replaceItem(@NotNull ItemStack oldItem, @NotNull ItemStack newItem);
}
