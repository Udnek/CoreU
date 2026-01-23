package me.udnek.coreu.custom.recipe;

import com.google.common.base.Preconditions;
import me.udnek.coreu.custom.item.CustomItem;
import me.udnek.coreu.custom.item.ItemUtils;
import me.udnek.coreu.custom.item.VanillaItemManager;
import net.kyori.adventure.key.Keyed;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.Damageable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

public class RecipeManager {

    private static RecipeManager instance;

    private final HashMap<String, CustomRecipe> customRecipes = new HashMap<>();

    public static RecipeManager getInstance() {
        if (instance == null) instance = new RecipeManager();
        return instance;
    }

    private RecipeManager(){}

    public void register(@NotNull Recipe recipe){
        if (recipe instanceof CustomRecipe customRecipe){
            Preconditions.checkArgument(
                    !customRecipes.containsKey(customRecipe.key().asString()),
                    "Recipe id duplicate: " + customRecipe.key().asString() + "!"
            );
            customRecipes.put(customRecipe.key().asString(), customRecipe);
        }
        else {
            Bukkit.addRecipe(recipe);
        }
    }
    public void getRecipesAsResult(@NotNull ItemStack itemStack, @NotNull Consumer<Recipe> consumer){
        Set<Recipe> recipes = new HashSet<>();
        CustomItem customItem = CustomItem.get(itemStack);
        if (customItem != null && !VanillaItemManager.isReplaced(itemStack)){
            customItem.getRecipes(recipes::add);
        }
        else {
            // FIXED DURABILITY BUG
            if (itemStack.hasItemMeta()){
                if (itemStack.getItemMeta() instanceof Damageable damageable){
                    damageable.setDamage(0);
                    itemStack = itemStack.clone();
                    itemStack.setItemMeta(damageable);
                }
            }

            List<Recipe> rawRecipes = Bukkit.getRecipesFor(itemStack);
            for (Recipe recipe : rawRecipes) {
                if (!ItemUtils.isSameIds(recipe.getResult(), itemStack)) continue;
                recipes.add(recipe);
            }
        }


        for (CustomRecipe recipe : customRecipes.values()) {
            if (recipe.isResult(itemStack)) recipes.add(recipe);
        }
        recipes.forEach(consumer);
    }

    public void getRecipesAsIngredient(@NotNull ItemStack itemStack, @NotNull Consumer<Recipe> consumer){
        Set<Recipe> recipes = new HashSet<>();
        ItemUtils.getItemInRecipesUsages(itemStack, recipes::add);
        for (CustomRecipe recipe : customRecipes.values()) {
            if (recipe.isIngredient(itemStack)) recipes.add(recipe);
        }
        recipes.forEach(consumer);
    }

    public <T extends CustomRecipe> List<T> getByType(@NotNull CustomRecipeType<T> type){
        List<T> recipes = new ArrayList<>();
        for (CustomRecipe recipe : customRecipes.values()) {
            if (recipe.getType() == type) recipes.add((T) recipe);
        }
        return recipes;
    }

    public <T extends CustomRecipe> @Nullable T getCustom(@NotNull CustomRecipeType<T> type, @NotNull NamespacedKey id){
        List<T> byType = getByType(type);
        for (T recipe : byType) {
            if (recipe.getKey().equals(id)) return recipe;
        }
        return null;
    }
    
    public @Nullable Recipe get(@NotNull NamespacedKey id){
        Recipe recipe = Bukkit.getRecipe(id);
        if (recipe != null) return recipe;
        return customRecipes.get(id.asString());
    }

    public void getAll(@NotNull Consumer<Recipe> consumer){
        customRecipes.values().forEach(consumer);
        Bukkit.recipeIterator().forEachRemaining(consumer);
    }

    public void unregister(@NotNull Recipe recipe){
        if (recipe instanceof CustomRecipe customRecipe){
            customRecipes.remove(customRecipe.key().asString());
        } else {
            if (!(recipe instanceof Keyed keyed)) return;
            Bukkit.removeRecipe((NamespacedKey) keyed.key());
        }
    }
    public void unregister(@NotNull NamespacedKey key){
        Recipe recipe = get(key);
        if (recipe == null) return;
        unregister(recipe);
    }

    public void unregister(@NotNull Iterable<Recipe> recipes){
        for (Recipe recipe : recipes) unregister(recipe);
    }
    public void unregister(@NotNull Recipe[] recipes){
        for (Recipe recipe : recipes) unregister(recipe);
    }
}






















