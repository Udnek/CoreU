package me.udnek.coreu.custom.recipe;

import org.bukkit.Keyed;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface CustomRecipe extends Recipe, Keyed {
    @NotNull Collection<ItemStack> getResults();
    boolean isResult(@NotNull ItemStack itemStack);
    boolean isIngredient(@NotNull ItemStack itemStack);
    @NotNull CustomRecipeType<?> getType();
    void replaceItem(@NotNull ItemStack oldItem, @NotNull ItemStack newItem);
    @Override
    @Deprecated
    @NotNull ItemStack getResult();
}
