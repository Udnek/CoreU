package me.udnek.coreu.custom.recipe.builder;

import me.udnek.coreu.custom.item.CustomItem;
import me.udnek.coreu.custom.recipe.RecipeManager;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.*;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@NullMarked
public class CookingRecipeBuilder extends RecipeBuilder<CookingRecipeBuilder>{

    private final RecipeChoice input;
    private float experience = 0.7f;
    private int cookingTime = 10 * 20;
    private final List<RecipeType> recipeTypes = new ArrayList<>();

    public CookingRecipeBuilder(Material material, RecipeChoice input) {
        this.input = input;
        result(material);
    }

    public CookingRecipeBuilder(CustomItem result, RecipeChoice input) {
        this.input = input;
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

    public CookingRecipeBuilder setRecipeTypes(RecipeType ... recipeTypes) {
        this.recipeTypes.addAll(Arrays.asList(recipeTypes));
        return this;
    }

    @Override
    protected void buildAndRegisterRecipe(Plugin plugin) {
        for (RecipeType recipeType : recipeTypes) registerByType(recipeType, plugin);
    }

    private void registerByType(RecipeType recipeType, Plugin plugin) {
        assert key != null;
        assert result != null;

        switch (recipeType) {
            case FURNACE -> RecipeManager.getInstance().register(
                    new FurnaceRecipe(new NamespacedKey(plugin, key + "_furnace"), result, input, experience, cookingTime));
            case SMOKER -> RecipeManager.getInstance().register(
                    new SmokingRecipe(new NamespacedKey(plugin, key + "_smoker"), result, input, experience, cookingTime / 2));
            case BLAST_FURNACE -> RecipeManager.getInstance().register(
                    new BlastingRecipe(new NamespacedKey(plugin, key + "_blast_furnace"), result, input, experience, cookingTime / 2));
            case CAMPFIRE -> RecipeManager.getInstance().register(
                    new CampfireRecipe(new NamespacedKey(plugin, key + "_campfire"), result, input, experience, cookingTime * 3));
        }
    }

    public enum RecipeType {
        FURNACE,
        BLAST_FURNACE,
        SMOKER,
        CAMPFIRE
    }
}
