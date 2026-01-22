package me.udnek.coreu.custom.recipe;

import me.udnek.coreu.custom.component.ComponentHolder;
import org.bukkit.Keyed;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface CustomRecipe<T extends CustomRecipeType<?>> extends ComponentHolder<CustomRecipe<T>>, Recipe, Keyed {
    @NotNull Collection<ItemStack> getResults();
    boolean isResult(@NotNull ItemStack itemStack);
    boolean isIngredient(@NotNull ItemStack itemStack);
    @NotNull T getType();
    void replaceItem(@NotNull ItemStack oldItem, @NotNull ItemStack newItem);
    @Override
    @Deprecated
    @NotNull ItemStack getResult();
}
