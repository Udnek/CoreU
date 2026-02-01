package me.udnek.coreu.custom.recipe;

import me.udnek.coreu.custom.recipe.choice.CustomRecipeChoice;
import org.bukkit.Keyed;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public interface CustomRecipe extends Recipe, Keyed {
    void getPossibleResults(@NotNull Consumer<@NotNull ItemStack> consumer);
    void getPossibleIngredients(@NotNull Consumer<@NotNull CustomRecipeChoice> consumer);
    @NotNull CustomRecipeType<?> getType();
    void replaceItem(@NotNull ItemStack oldItem, @NotNull ItemStack newItem);
    @Override
    @Deprecated
    @NotNull
    default ItemStack getResult(){
        throw new RuntimeException("#getResult is deprecated, use #getPossibleResults");
    }
}
