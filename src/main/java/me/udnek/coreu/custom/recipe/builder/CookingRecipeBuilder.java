package me.udnek.coreu.custom.recipe.builder;

import me.udnek.coreu.custom.item.CustomItem;
import me.udnek.coreu.custom.recipe.RecipeManager;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.*;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class CookingRecipeBuilder extends RecipeBuilder<CookingRecipeBuilder>{

    private final RecipeChoice recipeChoice;
    private float experience = 0.7f;
    private int cookingTime = 10 * 20;
    private RecipeType recipeType = RecipeType.FURNACE;

    public CookingRecipeBuilder(Material material, RecipeChoice recipeChoice) {
        this.recipeChoice = recipeChoice;
        result(material);
    }

    public CookingRecipeBuilder(CustomItem result, RecipeChoice recipeChoice) {
        this.recipeChoice = recipeChoice;
        result(result);
    }

    public CookingRecipeBuilder setExperience(float experience) {
        this.experience = experience;
        return this;
    }

    public CookingRecipeBuilder setCookingTime(int cookingTime) {
        this.cookingTime = cookingTime;
        return this;
    }

    public  CookingRecipeBuilder addRecipeType(RecipeType recipeType) {
        this.recipeType = recipeType;
        return this;
    }

    @Override
    protected void buildAndRegisterRecipe(Plugin plugin) {
        assert key != null;
        assert result != null;

        switch (recipeType) {
            case FURNACE: {
                RecipeManager.getInstance().register(new FurnaceRecipe(new NamespacedKey(plugin, key), result, recipeChoice, experience, cookingTime));
            }
            case SMOKER: {
                RecipeManager.getInstance().register(new SmokingRecipe(new NamespacedKey(plugin, key), result, recipeChoice, experience, cookingTime));
            }
            case BLAST_FURNACE: {
                RecipeManager.getInstance().register(new BlastingRecipe(new NamespacedKey(plugin, key), result, recipeChoice, experience, cookingTime));
            }
            case CAMPFIRE: {
                RecipeManager.getInstance().register(new CampfireRecipe(new NamespacedKey(plugin, key), result, recipeChoice, experience, cookingTime));
            }
        }
    }

    public enum RecipeType {
        FURNACE,
        BLAST_FURNACE,
        SMOKER,
        CAMPFIRE
    }
}
