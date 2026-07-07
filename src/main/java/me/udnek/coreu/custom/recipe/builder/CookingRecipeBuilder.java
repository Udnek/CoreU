package me.udnek.coreu.custom.recipe.builder;

import me.udnek.coreu.custom.item.CustomItem;
import me.udnek.coreu.custom.recipe.RecipeManager;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.*;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NullMarked;

import java.util.*;

@NullMarked
public class CookingRecipeBuilder extends RecipeBuilder<CookingRecipeBuilder>{

    private final RecipeChoice input;
    private float experience = 0.7f;
    private int cookingTime = 10 * 20;
    private final Set<Type> types = new HashSet<>();

    public CookingRecipeBuilder(Material result, RecipeChoice input) {
        this.input = input;
        result(result);
    }

    public CookingRecipeBuilder(CustomItem result, RecipeChoice input) {
        this.input = input;
        result(result);
    }

    public CookingRecipeBuilder experience(float experience) {
        this.experience = experience;
        return this;
    }

    public CookingRecipeBuilder cookingTime(int cookingTime) {
        this.cookingTime = cookingTime;
        return this;
    }

    public CookingRecipeBuilder types(Type... types) {
        this.types.addAll(Arrays.asList(types));
        return this;
    }

    @Override
    protected void buildAndRegisterRecipe(Plugin plugin) {
        if (types.isEmpty()) {
            throw new RuntimeException("CookingRecipeBuilder: No recipes have been registered");
        }
        for (Type type : types) registerByType(type, plugin);
    }

    private void registerByType(Type type, Plugin plugin) {
        assert key != null;
        assert result != null;

        switch (type) {
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

    public enum Type {
        FURNACE,
        BLAST_FURNACE,
        SMOKER,
        CAMPFIRE
    }
}
