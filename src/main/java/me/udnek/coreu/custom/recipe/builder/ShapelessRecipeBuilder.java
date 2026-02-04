package me.udnek.coreu.custom.recipe.builder;

import me.udnek.coreu.custom.item.CustomItem;
import me.udnek.coreu.custom.recipe.RecipeManager;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

@NullMarked
public class ShapelessRecipeBuilder extends RecipeBuilder<ShapelessRecipeBuilder>{
    private final List<RecipeChoice> recipeChoices = new ArrayList<>();

    public ShapelessRecipeBuilder(Material material) {
        result(material);
    }

    public ShapelessRecipeBuilder(CustomItem result) {
        result(result);
    }

    public ShapelessRecipeBuilder addIngredient(CustomItem customItem, int amount){
        for (int i = 0; i < amount; i++){
            this.recipeChoices.add(new RecipeChoice.ExactChoice(customItem.getItem()));
        }
        return this;
    }

    public ShapelessRecipeBuilder addIngredient(Material material, int amount){
        for (int i = 0; i < amount; i++){
            this.recipeChoices.add(new RecipeChoice.MaterialChoice(material));
        }
        return this;
    }

    public ShapelessRecipeBuilder addIngredient(Tag<Material> materialTag, int amount){
        for (int i = 0; i < amount; i++){
            this.recipeChoices.add(new RecipeChoice.MaterialChoice(materialTag));
        }
        return this;
    }

    @Override
    protected void buildAndRegisterRecipe(Plugin plugin) {
        ShapelessRecipe recipe = new ShapelessRecipe(new NamespacedKey(plugin, key), result);
        for (RecipeChoice recipeChoice : recipeChoices){
            recipe.addIngredient(recipeChoice);
        }
        RecipeManager.getInstance().register(recipe);
    }
}
