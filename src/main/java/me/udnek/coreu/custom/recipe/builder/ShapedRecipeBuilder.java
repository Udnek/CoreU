package me.udnek.coreu.custom.recipe.builder;

import me.udnek.coreu.custom.item.CustomItem;
import me.udnek.coreu.custom.recipe.RecipeManager;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NullMarked
public class ShapedRecipeBuilder extends RecipeBuilder<ShapedRecipeBuilder> {
    private String[] shape;
    private Map<Character, Material> materialIngredients = new HashMap<>();
    private Map<Character, CustomItem> customItemIngredients = new HashMap<>();
    private Map<Character, Tag<Material>> tagIngredients = new HashMap<>();
    private Map<Character, List<ItemStack>> stackIngredients = new HashMap<>();

    public ShapedRecipeBuilder(Material material) {
        result(material);
    }

    public ShapedRecipeBuilder(CustomItem result) {
        result(result);
    }

    public ShapedRecipeBuilder shape(String[] recipeShape){
        this.shape = recipeShape;
        return this;
    }

    public ShapedRecipeBuilder materialIngredients(Map<Character, Material> materialIngredients){
        this.materialIngredients = materialIngredients;
        return this;
    }

    public ShapedRecipeBuilder customItemIngredients(Map<Character, CustomItem> customItemIngredients){
        this.customItemIngredients = customItemIngredients;
        return this;
    }

    public ShapedRecipeBuilder tagIngredients(Map<Character, Tag<Material>> tagIngredients){
        this.tagIngredients = tagIngredients;
        return this;
    }

    public ShapedRecipeBuilder stackIngredients(Map<Character, List<ItemStack>> stackIngredients){
        this.stackIngredients = stackIngredients;
        return this;
    }

    @Override
    public void buildAndRegisterRecipe(Plugin plugin){
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, key), result);
        recipe.shape(shape);

        for (Map.Entry<Character, Material> material : materialIngredients.entrySet()) {
            recipe.setIngredient(material.getKey(), new RecipeChoice.MaterialChoice(material.getValue()));
        }
        for (Map.Entry<Character, CustomItem> customItem : customItemIngredients.entrySet()) {
            recipe.setIngredient(customItem.getKey(), new RecipeChoice.ExactChoice(customItem.getValue().getItem()));
        }
        for (Map.Entry<Character, Tag<Material>> tag : tagIngredients.entrySet()) {
            recipe.setIngredient(tag.getKey(), new RecipeChoice.MaterialChoice(tag.getValue()));
        }
        for (Map.Entry<Character, List<ItemStack>> entry : stackIngredients.entrySet()) {
            recipe.setIngredient(entry.getKey(), new RecipeChoice.ExactChoice(entry.getValue()));
        }

        RecipeManager.getInstance().register(recipe);
    }
}
