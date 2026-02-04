package me.udnek.coreu.custom.recipe;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import me.udnek.coreu.custom.item.CustomItem;
import me.udnek.coreu.custom.item.ItemUtils;
import net.kyori.adventure.key.Keyed;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.*;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

@org.jspecify.annotations.NullMarked public class RecipeManager{

    private static @Nullable RecipeManager instance;

    private final HashMap<String, CustomRecipe> customRecipes = new HashMap<>();

    public static RecipeManager getInstance() {
        if (instance == null) instance = new RecipeManager();
        return instance;
    }

    private RecipeManager(){}

    public void register(Recipe recipe){
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

    public void getRecipesAsResult(ItemStack itemStack, Consumer<Recipe> recipeConsumer){
        getRecipesAsResult(input -> ItemUtils.isSameIds(input, itemStack), recipeConsumer);
    }

    public void getRecipesAsResult(Predicate<ItemStack> predicate, Consumer<Recipe> recipeConsumer){
        Bukkit.recipeIterator().forEachRemaining(recipe -> {
            if (predicate.test(recipe.getResult())){
                recipeConsumer.accept(recipe);
            }
        });

        for (CustomRecipe recipe : customRecipes.values()) {
            AtomicBoolean contains = new AtomicBoolean(false);
            recipe.getPossibleResults(stack -> {
                if (contains.get()) return;
                if (predicate.test(stack)) {
                    contains.set(true);
                }
            });
            if (contains.get()) {
                recipeConsumer.accept(recipe);
            }
        }
    }

    public void getRecipesAsIngredient(ItemStack itemStack, Consumer<Recipe> recipeConsumer){
        CustomItem customItem = CustomItem.get(itemStack);
        if (customItem != null){
            getItemInRecipesUsages(itemStack,
                    stack -> CustomItem.get(stack) == customItem,
                    m -> false,
                    recipeConsumer);
        }
        else {
            Material type = itemStack.getType();
            getItemInRecipesUsages(itemStack,
                    stack -> ItemUtils.isVanillaMaterial(stack, type),
                    m -> m == type,
                    recipeConsumer);
        }
    }


    private void getItemInRecipesUsages(ItemStack stack,
                                        Predicate<ItemStack> stackPredicate,
                                        Predicate<Material> materialPredicate,
                                        Consumer<Recipe> recipeConsumer)
    {
        iterateTroughRecipesChoosing(recipeConsumer, recipeChoice -> {
            if (recipeChoice instanceof RecipeChoice.MaterialChoice choice)
                return choice.getChoices().stream().anyMatch(materialPredicate);
            if (recipeChoice instanceof RecipeChoice.ExactChoice choice)
                return choice.getChoices().stream().anyMatch(stackPredicate);
            return false;
        });

        for (CustomRecipe recipe : customRecipes.values()) {
            AtomicBoolean contains = new AtomicBoolean(false);
            recipe.getPossibleIngredients(choice -> {
                if (contains.get()) return;
                if (choice.test(stack)) {
                    contains.set(true);
                }
            });
            if (contains.get()) {
                recipeConsumer.accept(recipe);
            }
        }
    }

    public void iterateTroughRecipesChoosing(Consumer<Recipe> recipes, java.util.function.Predicate<RecipeChoice> predicate){
        Iterator<Recipe> recipeIterator = Bukkit.recipeIterator();
        while (recipeIterator.hasNext()){
            Recipe recipe = recipeIterator.next();

            if (recipe instanceof ShapedRecipe shapedRecipe){
                for(RecipeChoice recipeChoice: shapedRecipe.getChoiceMap().values()) {
                    if (predicate.test(recipeChoice)){
                        recipes.accept(recipe);
                        break;
                    }
                }
            }
            else if (recipe instanceof ShapelessRecipe shapelessRecipe){
                for (RecipeChoice recipeChoice : shapelessRecipe.getChoiceList()) {
                    if (predicate.test(recipeChoice)){
                        recipes.accept(recipe);
                        break;
                    }
                }
            }
            else if (recipe instanceof CookingRecipe<?> cookingRecipe){
                if (predicate.test(cookingRecipe.getInputChoice())){
                    recipes.accept(recipe);
                }
            }
            else if (recipe instanceof StonecuttingRecipe stonecuttingRecipe){
                if (predicate.test(stonecuttingRecipe.getInputChoice())){
                    recipes.accept(recipe);
                }
            }
            else if (recipe instanceof SmithingTransformRecipe smithingTransformRecipe){
                if (
                        predicate.test(smithingTransformRecipe.getBase()) ||
                                predicate.test(smithingTransformRecipe.getTemplate()) ||
                                predicate.test(smithingTransformRecipe.getAddition())
                )
                {recipes.accept(recipe);}
            }
            else if (recipe instanceof SmithingTrimRecipe smithingTrimRecipe){
                if (
                        predicate.test(smithingTrimRecipe.getBase()) ||
                                predicate.test(smithingTrimRecipe.getTemplate()) ||
                                predicate.test(smithingTrimRecipe.getAddition())
                )
                {recipes.accept(recipe);}
            } else if (recipe instanceof TransmuteRecipe transmuteRecipe) {
                if (predicate.test(transmuteRecipe.getInput()) || predicate.test(transmuteRecipe.getMaterial())){
                    recipes.accept(recipe);
                }
            }
        }
    }

    public <T extends CustomRecipe> List<T> getByType(CustomRecipeType<T> type){
        List<T> recipes = new ArrayList<>();
        for (CustomRecipe recipe : customRecipes.values()) {
            if (recipe.getType() == type) recipes.add((T) recipe);
        }
        return recipes;
    }

    public <T extends CustomRecipe> @Nullable T getCustom(CustomRecipeType<T> type, NamespacedKey id){
        List<T> byType = getByType(type);
        for (T recipe : byType) {
            if (recipe.getKey().equals(id)) return recipe;
        }
        return null;
    }

    public @Nullable Recipe get(NamespacedKey id){
        Recipe recipe = Bukkit.getRecipe(id);
        if (recipe != null) return recipe;
        return customRecipes.get(id.asString());
    }

    public void getAll(Consumer<Recipe> consumer){
        customRecipes.values().forEach(consumer);
        Bukkit.recipeIterator().forEachRemaining(consumer);
    }

    public void unregister(Recipe recipe){
        if (recipe instanceof CustomRecipe customRecipe){
            customRecipes.remove(customRecipe.key().asString());
        } else {
            if (!(recipe instanceof Keyed keyed)) return;
            Bukkit.removeRecipe((NamespacedKey) keyed.key());
        }
    }

    public void unregister(NamespacedKey key){
        Recipe recipe = get(key);
        if (recipe == null) return;
        unregister(recipe);
    }
}






















