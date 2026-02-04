package me.udnek.coreu.custom.recipe;

import me.udnek.coreu.custom.recipe.choice.CustomRecipeChoice;
import org.bukkit.Keyed;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.util.function.Consumer;

@org.jspecify.annotations.NullMarked public  interface CustomRecipe extends Recipe, Keyed{
    void getPossibleResults(Consumer<ItemStack> consumer);
    void getPossibleIngredients(Consumer<CustomRecipeChoice> consumer);
    CustomRecipeType<?> getType();
    void replaceItem(ItemStack oldItem, ItemStack newItem);
    @Override
    @Deprecated
    default ItemStack getResult(){
        throw new RuntimeException("#getResult is deprecated, use #getPossibleResults");
    }
}
